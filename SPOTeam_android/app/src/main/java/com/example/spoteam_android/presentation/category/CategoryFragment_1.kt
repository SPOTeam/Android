package com.example.spoteam_android.presentation.category

import android.content.Context
import android.os.Bundle
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
import com.example.spoteam_android.presentation.home.HomeFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryBinding
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.presentation.search.SearchFragment
import com.example.spoteam_android.presentation.alert.AlertFragment
import com.example.spoteam_android.presentation.category.category_tabs.CategoryStudyContentRVAdapter
import com.example.spoteam_android.presentation.community.CategoryStudyResponse
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.interestarea.ApiResponse
import com.example.spoteam_android.presentation.interestarea.MyInterestStudySpecificApiService
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class CategoryFragment_1 : Fragment() {

    lateinit var binding: FragmentCategoryBinding
    private lateinit var tabLayout: TabLayout
    lateinit var communityAPIService: CommunityAPIService
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
    private var selectedStudyTheme: String = "전체"
    private val viewModel: com.example.spoteam_android.presentation.category.CategoryInterestViewModel by activityViewModels()
    private var currentPage = 0
    private var totalPages = 0
    private var startPage = 0

    private val tabList =
        arrayListOf("전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        communityAPIService = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
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
        fetchMyInterestAll(
            selectedItem,
            gender,
            minAge,
            maxAge,
            hasFee,
            minFee,
            maxFee,
            currentPage
        )
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
            customView =
                LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
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
        }
        viewModel.theme = selectedStudyTheme
        currentPage = 0  // ⭐ 페이지 초기화

        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
        } else {
            fetchMyInterestSpecific(
                selectedStudyTheme,
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
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
                replaceFragment(com.example.spoteam_android.presentation.study.DetailStudyFragment())
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
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
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
            fetchMyInterestAll(
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
        } else {
            fetchMyInterestSpecific(
                selectedStudyTheme,
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
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
            fetchMyInterestAll(
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
        } else {
            fetchMyInterestSpecific(
                selectedStudyTheme,
                selectedItem,
                gender,
                minAge,
                maxAge,
                hasFee,
                minFee,
                maxFee,
                currentPage
            )
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
        currentPage: Int? = null
    ) {
        val boardItems = arrayListOf<BoardItem>()

        communityAPIService.getAllStudyWithDetail(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            hasFee = hasFee,
            minFee = minFee,
            maxFee = maxFee,
            page = currentPage ?: 0,
            size = 5,
            sortBy = selectedItem ?: "ALL",
        ).enqueue(object : Callback<CategoryStudyResponse> {
            override fun onResponse(
                call: Call<CategoryStudyResponse>,
                response: Response<CategoryStudyResponse>
            ) {
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
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                        binding.communityCategoryContentRv.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
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
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        currentPage: Int? = null,
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service =
            RetrofitInstance.retrofit.create(MyInterestStudySpecificApiService::class.java)

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
                        binding.communityCategoryContentRv.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.contentCountTv.text = String.format("%02d", totalElements)
                        updateRecyclerView(boardItems)

                        startPage = calculateStartPage()

                    } else {
                        binding.contentCountTv.text = "00"
//                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.communityCategoryContentRv.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showErrorToast()
            }
        })
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    studyItem.heartCount += if (liked) 1 else -1
                    val icon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(icon)

                    // RecyclerView 갱신
                    interestBoardAdapter.notifyItemChanged(
                        interestBoardAdapter.dataList.indexOf(studyItem)
                    )
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
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
            replaceFragment(HomeFragment())
        }

        binding.icAlarm.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }
}
