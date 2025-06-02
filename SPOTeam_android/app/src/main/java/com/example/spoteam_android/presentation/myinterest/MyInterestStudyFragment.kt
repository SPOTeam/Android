package com.example.spoteam_android.presentation.myinterest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMyInterestStudyBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.presentation.alert.AlertFragment
import com.example.spoteam_android.presentation.home.HomeFragment
import com.example.spoteam_android.presentation.interestarea.ApiResponse
import com.example.spoteam_android.presentation.interestarea.GetInterestCategoryApiService
import com.example.spoteam_android.presentation.interestarea.InterestVPAdapter
import com.example.spoteam_android.presentation.interestarea.MyInterestStudyAllApiService
import com.example.spoteam_android.presentation.interestarea.MyInterestStudySpecificApiService
import com.example.spoteam_android.presentation.search.SearchFragment
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class MyInterestStudyFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyBinding
    private lateinit var tabLayout: TabLayout

    lateinit var myInterestAllApiService: MyInterestStudyAllApiService

    lateinit var myInterestSpecificApiService: MyInterestStudySpecificApiService

    lateinit var categoryApiService: GetInterestCategoryApiService

    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private var gender: String? = null
    private var minAge: Int = 18
    private var maxAge: Int = 60
    private var minFee: Int? = null
    private var maxFee: Int? = null
    private var hasFee: Boolean? = null
    private var isOnline: Boolean? = null
    private var source: String? = null
    private var regionCodes: MutableList<String>? = null
    private var selectedStudyCategory: String? = "전체"
    private var selectedItem: String = "ALL"
    private var selectedStudyTheme: String ="전체"
    private val viewModel: MyInterestChipViewModel by activityViewModels()
    private var currentPage = 0
    private var totalPages = 0
    private var startPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)

        selectedItem = when (viewModel.isRecruiting) {
            true -> "RECRUITING"
            false -> "COMPLETED"
            null -> "RECRUITING"
        }

        initArguments()
        setupRecyclerView()
        setupTabs()
        setupBottomSheet()
        setupFilterIcon()
        setupPageNavigationButtons()
        setupNavigationClickListeners()

        initialFetch()
        updatePageUI()

        return binding.root
    }

    private fun initArguments() {

        gender = viewModel.gender
        minAge = viewModel.minAge
        maxAge = viewModel.maxAge
        hasFee = viewModel.hasFee
        minFee = viewModel.finalMinFee
        maxFee = viewModel.finalMaxFee
        isOnline = viewModel.isOnline
        regionCodes = viewModel.selectedCode

        source = arguments?.getString("source") // source만 그대로 Bundle에서 받음
    }

    private fun initialFetch() {
        when (source) {
            "HouseFragment" -> {
                binding.icFilter.visibility = View.VISIBLE
                binding.icFilterActive.visibility = View.GONE
                viewModel.reset()
            }
            "MyInterestStudyFilterFragment" -> {
                binding.icFilter.visibility = View.GONE
                binding.icFilterActive.visibility = View.VISIBLE
            }
        }
        fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
    }

    private fun setupTabs() {
        tabLayout = binding.tabs

        fetchDataGetInterestCategory { themes ->
            themes?.let {
                setupTabsWithThemes(it)
            }
        }
    }

    private fun setupTabsWithThemes(themes: List<String>) {
        tabLayout.addTab(createTab("전체"))

        themes.forEach { theme ->
            tabLayout.addTab(createTab(theme))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                handleTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun createTab(text: String): TabLayout.Tab {
        return tabLayout.newTab().apply {
            customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                val textView = findViewById<TextView>(R.id.tabText)
                textView.text = text
            }
            tag = text
        }
    }

    private fun handleTabSelected(tab: TabLayout.Tab) {
        selectedStudyCategory = tab.tag as String
        selectedStudyTheme = selectedStudyCategory.toString()  // ← 탭 선택값으로 테마 업데이트

        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
        }
    }


    private fun setupRecyclerView() {
        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { item, btn ->
            toggleLikeStatus(item, btn)
        }, studyViewModel)

        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                replaceFragment(com.example.spoteam_android.presentation.study.DetailStudyFragment())
            }
        })

        binding.myInterestStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
    }

    private fun setupBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_interest_spinner, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        val recentlyLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_recently)
        val viewLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_view)
        val hotLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_hot)

        recentlyLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "최신 순"
            fetchFilteredStudy("ALL")  // 최신 순
        }

        viewLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            fetchFilteredStudy("HIT")
        }

        hotLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            fetchFilteredStudy("LIKED")  // 관심 많은 순
        }

        binding.filterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun fetchFilteredStudy(selectedItem: String) {
        this.selectedItem = selectedItem
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
        }
    }

    private fun setupFilterIcon() {
        binding.icFilter.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
            val filterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }

        binding.icFilterActive.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
            val filterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }
    }

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                requestPageUpdate()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                requestPageUpdate()
            }
        }
    }

    private fun requestPageUpdate() {
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
        }
    }

    private fun updatePageNumberUI() {

        startPage = calculateStartPage()
        Log.d("PageDebug", "📄 페이지 번호 UI 업데이트 - currentPage: $currentPage, startPage: $startPage")

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    if (selectedStudyCategory == "전체") {
                        fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
                    } else {
                        fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
                    }
                }
            }
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                if (selectedStudyCategory == "전체") {
                    fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
                } else {
                    fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
                }
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                if (selectedStudyCategory == "전체") {
                    fetchMyInterestAll(selectedItem, gender, minAge, maxAge, isOnline,hasFee,minFee,maxFee,currentPage,regionCodes)
                } else {
                    fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, isOnline, hasFee, minFee, maxFee, currentPage,regionCodes)
                }
            }
        }
    }

    private fun calculateStartPage(): Int {
        return when {
            totalPages <= 5 -> 0
            currentPage <= 2 -> 0
            currentPage >= totalPages - 3 -> totalPages - 5
            else -> currentPage - 2
        }
    }

    private fun getTotalPages(): Int {
        return totalPages // 올바른 페이지 수 계산
    }

    private fun updatePageUI() {
        startPage = calculateStartPage()

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(
                    if (pageNum == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.isEnabled = true
                textView.alpha = 1.0f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(0)
                textView.isEnabled = false // 클릭 안 되게
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPages - 1
    }

    private fun fetchMyInterestAll(
            selectedItem: String,
            gender: String?,
            minAge: Int,
            maxAge: Int,
            isOnline: Boolean?,
            hasFee: Boolean?,
            minFee: Int?,
            maxFee: Int?,
            currentPage: Int?= null,
            regionCodes: MutableList<String>?
    )
    {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(MyInterestStudyAllApiService::class.java)

        service.GetMyInterestStudy(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            isOnline = isOnline,
            hasFee = hasFee,
            minFee = minFee,
            maxFee = maxFee,
            page = currentPage ?: 0 ,
            size = 5,
            sortBy = selectedItem ?: "ALL",
            regionCodes = regionCodes,
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result.content.forEach { study ->
                            totalPages = apiResponse.result.totalPages
                            val boardItem = BoardItem(
                                studyId = study.studyId,
                                title = study.title,
                                goal = study.goal,
                                introduction = study.introduction,
                                memberCount = study.memberCount,
                                heartCount = study.heartCount,
                                hitNum = study.hitNum,
                                maxPeople = study.maxPeople,
                                studyState = study.studyState,
                                themeTypes = study.themeTypes,
                                regions = study.regions,
                                imageUrl = study.imageUrl,
                                liked = study.liked,
                                isHost = false
                            )
                            boardItems.add(boardItem)
                            binding.pageNumberLayout.visibility = View.VISIBLE
                            startPage = calculateStartPage()
                            updatePageNumberUI()
                            updatePageUI()
                        }
                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)
                        updateRecyclerView(boardItems)
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        binding.checkAmount.text = "00건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMyInterestSpecific(
        selectedStudyTheme: String?,
        selectedItem: String,
        gender: String?,
        minAge: Int,
        maxAge: Int,
        isOnline: Boolean?,
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        currentPage: Int?= null,
        regionCodes: MutableList<String>?
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(MyInterestStudySpecificApiService::class.java)

        service.GetMyInterestStudys(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            isOnline = isOnline,
            hasFee = hasFee,
            minFee = minFee,
            maxFee = maxFee,
            page = currentPage ?: 0,
            size = 5,
            sortBy = selectedItem,
            theme = selectedStudyTheme,
            regionCodes = regionCodes
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
                            totalPages = apiResponse.result.totalPages
                            val boardItem = BoardItem(
                                studyId = study.studyId,
                                title = study.title,
                                goal = study.goal,
                                introduction = study.introduction,
                                memberCount = study.memberCount,
                                heartCount = study.heartCount,
                                hitNum = study.hitNum,
                                maxPeople = study.maxPeople,
                                studyState = study.studyState,
                                themeTypes = study.themeTypes,
                                regions = study.regions,
                                imageUrl = study.imageUrl,
                                liked = study.liked,
                                isHost = false
                            )
                            boardItems.add(boardItem)
                        }
                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)
                        updateRecyclerView(boardItems)

                        binding.pageNumberLayout.visibility = View.VISIBLE
                        startPage = calculateStartPage()
                        updatePageNumberUI()
                        updatePageUI()
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        binding.checkAmount.text = "00건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun showEmptyState() {
        binding.checkAmount.text = "0건"
        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
        binding.myInterestStudyReyclerview.visibility = View.GONE
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
    }


    private fun fetchDataGetInterestCategory(callback: (List<String>?) -> Unit) {
        val service = RetrofitInstance.retrofit.create(GetInterestCategoryApiService::class.java)
        service.GetMyInterestStudy(
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        if (it.isSuccess) {
                            callback(it.result.themes)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    val newIcon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(newIcon)

                    // heartCount 갱신
                    studyItem.heartCount += if (liked) 1 else -1

                    // RecyclerView 업데이트
                    val position = interestBoardAdapter.dataList.indexOf(studyItem)
                    if (position != -1) {
                        interestBoardAdapter.notifyItemChanged(position)
                    }
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateLikeButtonUI(likeButton: ImageView, isLiked: Boolean) {
        val newIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.study_like
        likeButton.setImageResource(newIcon)
    }

    private fun replaceFragment(fragment: Fragment) {
    (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
        .replace(R.id.main_frm, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
    }

    private fun setupNavigationClickListeners() {
        binding.icFindMyInterest.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.spotLogo.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        binding.icAlarmMyInterest.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }
}
