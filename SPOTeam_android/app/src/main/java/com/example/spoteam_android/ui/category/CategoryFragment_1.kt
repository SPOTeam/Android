package com.example.spoteam_android.ui.category

import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCategoryBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.community.CategoryStudyResponse
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.MyInterestStudySpecificApiService
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFragment_1 : Fragment() {

    lateinit var binding: FragmentCategoryBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: CategoryVPAdapter
    private var gender: String? = null
    private var minAge: Int = 18
    private var maxAge: Int = 60
    private var minFee: Int? = null
    private var maxFee: Int? = null
    private var hasFee: Boolean? = null
    private var source: Boolean = false
    private var selectedStudyCategory: String? = "전체"
    private var selectedItem: String = "ALL"
    private var selectedStudyTheme: String ="전체"
    private val viewModel: CategoryInterestViewModel by activityViewModels()
    private var currentPage = 0
    private var totalPages = 0
    private var startPage = 0

    private val tabList = arrayListOf("전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)  // ← 이거 추가

        initArguments()
        setupRecyclerView()
        setupTabs()
        setupBottomSheet()
        setupFilterIcon()
        setupNavigationClickListeners()

        initialFetch()

        return binding.root
    }

    private fun initArguments() {
        gender = viewModel.gender
        minAge = viewModel.minAge
        maxAge = viewModel.maxAge
        hasFee = viewModel.hasFee
        minFee = viewModel.finalMinFee
        maxFee = viewModel.finalMaxFee
        selectedItem = viewModel.selectedItem
        source = viewModel.source
        selectedStudyTheme = viewModel.theme
    }

    private fun initialFetch() {
        if (!source) {
                binding.icFilter.visibility = View.VISIBLE
                binding.icFilterActive.visibility = View.GONE
        } else {
            binding.icFilter.visibility = View.GONE
            binding.icFilterActive.visibility = View.VISIBLE
        }
        fetchMyInterestAll(selectedItem, gender, minAge, maxAge,hasFee,minFee,maxFee,currentPage)
    }

    private fun setupTabs() {
        tabLayout = binding.categoryTl

        setupTabsWithThemes(tabList)
    }

    private fun setupTabsWithThemes(themes: List<String>) {
        themes.forEachIndexed { index, theme ->
            val tab = createTab(theme)
            tabLayout.addTab(tab)

            // ✨ viewModel.theme에 맞는 탭 자동 선택
            if (theme == viewModel.theme) {
                tabLayout.selectTab(tab)  // ← 여기 추가
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                handleTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
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
        if (selectedStudyTheme == "전공/진로") {
            selectedStudyTheme = "전공및진로학습"
        } else if (selectedStudyTheme == "시사/뉴스") {
            selectedStudyTheme = "시사뉴스"
        }
        viewModel.theme = selectedStudyTheme
        currentPage = 0  // ⭐ 페이지 초기화

        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge,hasFee,minFee,maxFee,currentPage)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, hasFee, minFee, maxFee, currentPage)
        }
    }


    private fun setupRecyclerView() {
        interestBoardAdapter = CategoryVPAdapter(
            dataList = ArrayList(),
            onLikeClick = { item, btn -> toggleLikeStatus(item, btn) },
            studyViewModel = studyViewModel,
            onPageSelected = { selectedPage ->
                currentPage = selectedPage
                requestPageUpdate()
            },
            onNextPrevClicked = { isNext ->
                currentPage = when {
                    isNext && currentPage < totalPages - 1 -> currentPage + 1
                    !isNext && currentPage > 0 -> currentPage - 1
                    else -> currentPage
                }
                requestPageUpdate()
            },
            getCurrentPage = { currentPage },
            getTotalPages = { totalPages }
        )

        interestBoardAdapter.setItemClickListener(object : CategoryVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                replaceFragment(DetailStudyFragment())
            }
        })

        binding.communityCategoryContentRv.apply {
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

        binding.contentFilterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun fetchFilteredStudy(selectedItem: String) {
        this.selectedItem = selectedItem
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge,hasFee,minFee,maxFee,currentPage)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, hasFee, minFee, maxFee, currentPage)
        }
    }

    private fun setupFilterIcon() {
        binding.icFilter.setOnClickListener {
            replaceFragment(CategoryInterestFilterFragment())
        }

        binding.icFilterActive.setOnClickListener {
            replaceFragment(CategoryInterestFilterFragment())
        }
    }

    private fun requestPageUpdate() {
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge,hasFee,minFee,maxFee,currentPage)
        } else {
            fetchMyInterestSpecific(selectedStudyTheme,selectedItem, gender, minAge, maxAge, hasFee, minFee, maxFee, currentPage)
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

    private fun fetchMyInterestAll(
        selectedItem: String,
        gender: String?,
        minAge: Int,
        maxAge: Int,
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        currentPage: Int?= null
    )
    {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)

        service.getAllStudyWithDetail(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            hasFee = hasFee,
            minFee = minFee,
            maxFee = maxFee,
            page = currentPage ?: 0 ,
            size = 5,
            sortBy = selectedItem ?: "ALL",
        ).enqueue(object : Callback<CategoryStudyResponse> {
            override fun onResponse(call: Call<CategoryStudyResponse>, response: Response<CategoryStudyResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess != null) {
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
                            startPage = calculateStartPage()

                        }
                        binding.communityCategoryContentRv.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.contentCountTv.text = String.format("%02d", totalElements)
                        updateRecyclerView(boardItems)
                    } else {
                        binding.contentCountTv.text = "00"
//                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.communityCategoryContentRv.visibility = View.GONE
                    }
                } else {
//                    Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
//                Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun visibleUpperTab(isVisible :Boolean) {
        if(isVisible) {
            binding.contentCountTv.visibility = View.VISIBLE
            binding.measureCountTv.visibility = View.VISIBLE
            binding.contentFilterToggleContainer.visibility = View.VISIBLE
            binding.icFilter.visibility = View.VISIBLE

            binding.emptyWaiting.visibility = View.GONE
            binding.communityCategoryContentRv.visibility = View.VISIBLE

        } else {
            binding.contentCountTv.visibility = View.GONE
            binding.measureCountTv.visibility = View.GONE
            binding.contentFilterToggleContainer.visibility = View.GONE
            binding.icFilter.visibility = View.GONE

            binding.emptyWaiting.visibility = View.VISIBLE
            binding.communityCategoryContentRv.visibility = View.GONE
        }
    }

    private fun fetchMyInterestSpecific(
        selectedStudyTheme: String?,
        selectedItem: String,
        gender: String?,
        minAge: Int,
        maxAge: Int,
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        currentPage: Int?= null,
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(MyInterestStudySpecificApiService::class.java)

        service.GetMyInterestStudys(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            hasFee = hasFee,
            isOnline = null,
            minFee = minFee,
            maxFee = maxFee,
            page = currentPage ?: 0,
            size = 5,
            sortBy = selectedItem,
            theme = selectedStudyTheme,
            regionCodes = null
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

                        visibleUpperTab(true)

                        val totalElements = apiResponse.result.totalElements
                        binding.contentCountTv.text = String.format("%02d", totalElements)
                        updateRecyclerView(boardItems)

                        startPage = calculateStartPage()

                    } else {
                        visibleUpperTab(false)
//                        binding.contentCountTv.text = "00"
//                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun showErrorToast() {
//        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 BoardItem의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.communityCategoryContentRv.adapter as CategoryVPAdapter
                                val position = adapter.dataList.indexOf(studyItem)
                                if (position != -1) {
                                    adapter.notifyItemChanged(position)
                                }
                            }
                        } else {
//                            Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
//                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
//            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun setupNavigationClickListeners() {
        binding.icFind.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.icSpotLogo.setOnClickListener {
            replaceFragment(HouseFragment())
        }

        binding.icAlarm.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }
}
