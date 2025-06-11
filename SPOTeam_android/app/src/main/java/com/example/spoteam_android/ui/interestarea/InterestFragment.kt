package com.example.spoteam_android.ui.interestarea

import StudyApiService
import StudyViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
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
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.FixedRoundedSpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class InterestFragment : Fragment() {

    lateinit var binding: FragmentInterestBinding
    private lateinit var tabLayout: TabLayout
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private var gender: String? = null
    private var minAge: Int = 18
    private var maxAge: Int = 60
    private var minFee: Int? = null
    private var maxFee: Int? = null
    private var hasFee: Boolean? = null
    private var themeTypes: List<String>? = null
    private var source: String? = null
    private var selectedItem: String = "ALL"
    private var selectedRegion: String? = "0000000000"
    private val viewModel: InterestFilterViewModel by activityViewModels()
    private var currentPage = 0
    private var totalPages = 0
    private var startPage = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInterestBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        selectedItem = when (viewModel.isRecruiting) {
            true -> "RECRUITING"
            false -> "COMPLETED"
            null -> "RECRUITING"
        }

        setupRecyclerView()

        binding.spotLogo.setOnClickListener {
            replaceFragment(HouseFragment())
        }

        binding.icFindInterest.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.icAlarmInterest.setOnClickListener {
            replaceFragment(AlertFragment())
        }


        gender = viewModel.gender
        minAge = viewModel.minAge
        maxAge = viewModel.maxAge
        hasFee = viewModel.hasFee
        themeTypes = viewModel.themeTypes
        minFee = viewModel.finalMinFee
        maxFee = viewModel.finalMaxFee
        source = arguments?.getString("source") // source만 그대로 Bundle에서 받음

        setupBottomSheet()


        tabLayout = binding.tabs

        when (source) {
            "HouseFragment" -> {
                binding.icFilter.visibility = View.VISIBLE
                binding.icFilterActive.visibility = View.GONE
                viewModel.reset()
                fetchData(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    hasFee = hasFee,
                    minFee = minFee,
                    maxFee = maxFee,
                    themeTypes = themeTypes,
                    currentPage = currentPage,
                )
                fetchDataGetInterestArea() { regions ->
                    setupTabs(regions)
                }
            }
            "InterestFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                binding.icFilter.visibility = View.GONE
                fetchData(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    hasFee = hasFee,
                    minFee = minFee,
                    maxFee = maxFee,
                    themeTypes = themeTypes,
                    currentPage = currentPage,
                )
                fetchDataGetInterestArea() { regions ->
                    setupTabs(regions)
                }
            }
        }


        binding.icFilter.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }

        binding.icFilterActive.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }
        updatePageUI()

        return binding.root
    }

    private fun setupRecyclerView() {
        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        }, studyViewModel = studyViewModel)

        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.interestAreaStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }
    }

    private fun setupTabs(
        regions: List<Region>?,
    ) {
        // 로그 태그 정의
        val TAG = "SetupTabs"


        regions?.let {
            val allTab = tabLayout.newTab().apply {
                customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                    val textView = findViewById<TextView>(R.id.tabText)
                    textView.text = "전체"
                }
                tag = "0000000000"
                selectedRegion = "0000000000"
            }


            tabLayout.addTab(allTab)

            // 지역별 탭 로그
            regions.forEach { region ->
                val tab = tabLayout.newTab().apply {
                    customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                        findViewById<TextView>(R.id.tabText).text = region.neighborhood
                    }
                    tag = region.code
                }
                tabLayout.addTab(tab)
            }

            // 탭 선택 리스너 로그
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    val selectedRegionCode = tab.tag as String
                    if (selectedRegionCode == "0000000000") {
                        selectedRegion = "0000000000"
                        fetchData(
                            selectedItem,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            hasFee = hasFee,
                            minFee = minFee,
                            maxFee = maxFee,
                            themeTypes = themeTypes,
                            currentPage = currentPage
                        )
                    } else {
                        selectedRegion = selectedRegionCode
                        fetchData(
                            selectedItem,
                            regionCode = selectedRegionCode,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            hasFee = hasFee,
                            minFee = minFee,
                            maxFee = maxFee,
                            themeTypes = themeTypes,
                            currentPage = currentPage
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        } ?: run {
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
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
        fetchData(
            selectedItem = selectedItem,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            hasFee = hasFee,
            minFee = minFee,
            maxFee = maxFee,
            themeTypes = themeTypes,
            currentPage = currentPage
        )
    }

    private fun fetchData(
        selectedItem: String,
        regionCode: String? = null,
        gender: String?,
        minAge: Int,
        maxAge: Int,
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        themeTypes: List<String>?,
        currentPage: Int?= null
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val interestAreaBoard = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount

        val theme = if (themeTypes.isNullOrEmpty()) null else themeTypes

        val call: Call<ApiResponse> = if (regionCode != null) {
            val service = RetrofitInstance.retrofit.create(InterestSpecificAreaApiService::class.java)
            service.InterestSpecificArea(
                regionCode = regionCode,
                gender = gender ?: "MALE",
                minAge = minAge,
                maxAge = maxAge,
                isOnline = false,
                hasFee = hasFee,
                maxFee = maxFee,
                minFee = minFee,
                themeTypes = theme,
                page = currentPage ?: 0,
                size = 5,
                sortBy = selectedItem
            )
        } else {
            val service = RetrofitInstance.retrofit.create(InterestAreaApiService::class.java)
            service.InterestArea(
                gender = gender,
                minAge = minAge,
                maxAge = maxAge,
                isOnline = false,
                hasFee = hasFee,
                maxFee = maxFee,
                minFee = minFee,
                themeTypes = theme,
                page = currentPage ?: 0,
                size = 5,
                sortBy = selectedItem
            )
        }
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    boardItems.clear()
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
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

                            binding.pageNumberLayout.visibility = View.VISIBLE
                            startPage = calculateStartPage()
                            updatePageNumberUI()
                            updatePageUI()
                        }
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)
                        interestAreaBoard.visibility = View.VISIBLE
                        updateRecyclerView(boardItems)
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        checkcount.text = "00건"
                        interestAreaBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
    }


    private fun updatePageNumberUI() {
        startPage = calculateStartPage()

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val selectedPage = startPage + index
            textView.setOnClickListener {
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    fetchByCurrentPage()
                }
            }
        }

        binding.previousPage.setOnClickListener {
            currentPage = if (currentPage == 0) {
                totalPages - 1 // 첫 페이지일 경우 마지막 페이지로
            } else {
                currentPage - 1
            }
            fetchByCurrentPage()
        }

        binding.nextPage.setOnClickListener {
            currentPage = if (currentPage == totalPages - 1) {
                0 // 마지막 페이지일 경우 첫 페이지로
            } else {
                currentPage + 1
            }
            fetchByCurrentPage()
        }
    }

    private fun fetchByCurrentPage() {
        if (selectedRegion == "0000000000") {
            fetchData(
                selectedItem,
                gender = gender,
                minAge = minAge,
                maxAge = maxAge,
                hasFee = hasFee,
                minFee = minFee,
                maxFee = maxFee,
                themeTypes = themeTypes,
                currentPage = currentPage
            )
        } else {
            fetchData(
                selectedItem,
                regionCode = selectedRegion,
                gender = gender,
                minAge = minAge,
                maxAge = maxAge,
                hasFee = hasFee,
                minFee = minFee,
                maxFee = maxFee,
                themeTypes = themeTypes,
                currentPage = currentPage
            )
        }
    }


    private fun calculateStartPage(): Int {
        return when {
            totalPages <= 5 -> 0
            currentPage >= totalPages - 3 -> totalPages - 5
            currentPage >= 2 -> currentPage - 2
            else -> 0
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

        pageButtons.forEachIndexed { index, button ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                button.visibility = View.VISIBLE
                button.text = (pageNum + 1).toString()
                button.setTextColor(
                    if (pageNum == currentPage)
                        requireContext().getColor(R.color.b500)
                    else
                        requireContext().getColor(R.color.g400)
                )
            } else {
                button.visibility = View.GONE
            }
        }

        val gray = ContextCompat.getColor(requireContext(), R.color.g300)
        val blue = ContextCompat.getColor(requireContext(), R.color.b500)

        if (totalPages <= 1) {
            binding.previousPage.isEnabled = false
            binding.nextPage.isEnabled = false
            binding.previousPage.setColorFilter(gray, android.graphics.PorterDuff.Mode.SRC_IN)
            binding.nextPage.setColorFilter(gray, android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            binding.previousPage.isEnabled = true
            binding.nextPage.isEnabled = true
            binding.previousPage.setColorFilter(blue, android.graphics.PorterDuff.Mode.SRC_IN)
            binding.nextPage.setColorFilter(blue, android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }



    private fun fetchDataGetInterestArea( callback: (List<Region>?) -> Unit) {
        val service = RetrofitInstance.retrofit.create(GetMemberInterestAreaApiService::class.java)
        service.GetInterestArea(
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val regions = response.body()?.result?.regions
                    callback(regions)
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
                                val adapter = binding.interestAreaStudyReyclerview.adapter as InterestVPAdapter
                                val position = adapter.dataList.indexOf(studyItem)
                                if (position != -1) {
                                    adapter.notifyItemChanged(position)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
