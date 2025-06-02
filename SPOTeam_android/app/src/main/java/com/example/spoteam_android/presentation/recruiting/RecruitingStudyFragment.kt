package com.example.spoteam_android.presentation.recruiting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.presentation.home.HomeFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentRecruitingStudyBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.presentation.search.SearchFragment
import com.example.spoteam_android.presentation.alert.AlertFragment
import com.example.spoteam_android.presentation.interestarea.ApiResponse
import com.example.spoteam_android.presentation.interestarea.InterestVPAdapter
import com.example.spoteam_android.presentation.interestarea.RecruitingStudyApiService
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var recruitingStudyAdapter: InterestVPAdapter
    private var gender: String? = null
    private var minAge: Int = 18
    private var maxAge: Int = 60
    private var minFee: Int? = null
    private var maxFee: Int? = null
    private var hasFee: Boolean? = null
    private var isOnline: Boolean? = null
    private var source: String? = null
    private var selectedItem: String = "ALL"
    private var regionCodes: MutableList<String>? = null
    private var themeTypes: List<String>? = null
    private val viewModel: RecruitingChipViewModel by activityViewModels()
    private var currentPage = 0
    private var totalPages = 0
    private var startPage = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitingStudyBinding.inflate(inflater, container, false)

        initArguments()
        setupRecyclerView()
        setupBottomSheet()
        setupFilterIcon()
        setupPageNavigationButtons()
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
        isOnline = viewModel.isOnline
        themeTypes = viewModel.themeTypes
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
            "RecruitingStudyFilterFragment" -> {
                binding.icFilter.visibility = View.GONE
                binding.icFilterActive.visibility = View.VISIBLE
            }
        }
        fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage, regionCodes)
    }

    private fun setupRecyclerView() {
        recruitingStudyAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { item, btn ->
            toggleLikeStatus(item, btn)
        }, studyViewModel)

        recruitingStudyAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                replaceFragment(com.example.spoteam_android.presentation.study.DetailStudyFragment())
            }
        })

        binding.recruitingStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recruitingStudyAdapter
        }
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
        fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage, regionCodes)
    }

    private fun setupFilterIcon() {
        binding.icFilter.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "RecruitingStudyFragment") }
            val filterFragment = RecruitingStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }

        binding.icFilterActive.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "RecruitingStudyFragment") }
            val filterFragment = RecruitingStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }
    }

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage,regionCodes)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage,regionCodes)
            }
        }
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
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage,regionCodes)
                }
            }
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage,regionCodes)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage,regionCodes)
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

    private fun fetchRecruitingStudy(
        selectedItem: String,
        gender: String?,
        minAge: Int,
        maxAge: Int,
        isOnline: Boolean?,
        hasFee: Boolean?,
        minFee: Int?,
        maxFee: Int?,
        themeTypes: List<String>?,
        currentPage: Int?= null,
        regionCodes: MutableList<String>?
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(RecruitingStudyApiService::class.java)


        service.GetRecruitingStudy(
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            isOnline = isOnline,
            hasFee = hasFee,
            maxFee = maxFee,
            minFee = minFee,
            themeTypes = themeTypes,
            page = currentPage ?: 0,
            size = 5,
            sortBy = selectedItem,
            regionCodes = regionCodes
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
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
                        totalPages = apiResponse.result.totalPages
                        updatePageNumberUI()
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)
                        binding.recruitingStudyReyclerview.visibility = View.VISIBLE
                        binding.pageNumberLayout.visibility = View.VISIBLE
                        startPage = calculateStartPage()
                        updatePageNumberUI()
                        updatePageUI()

                        updateRecyclerView(boardItems)
                    } else {
                        binding.checkAmount.text = "00건"
                        binding.pageNumberLayout.visibility = View.GONE
                        showNoResults()
                    }
                } else {
                    val errorMsg = response.errorBody()?.string()
                    showError(errorMsg)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showError(t.message)
            }
        })
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        recruitingStudyAdapter.updateList(boardItems)
    }

    private fun showNoResults() {
        binding.checkAmount.text = "0 건"
        binding.recruitingStudyReyclerview.visibility = View.GONE
        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun showError(error: String?) {
        Toast.makeText(requireContext(), "API 호출 실패: $error", Toast.LENGTH_SHORT).show()
    }
    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val adapter = binding.recruitingStudyReyclerview.adapter as InterestVPAdapter
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    studyItem.heartCount += if (liked) 1 else -1
                    val icon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(icon)

                    // RecyclerView 갱신
                    adapter.notifyItemChanged(
                        adapter.dataList.indexOf(studyItem)
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
        binding.icFindRecruiting.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.spotLogo.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        binding.icAlarmRecruiting.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }
}
