package com.umcspot.android.ui.recruiting

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.umcspot.android.BoardItem
import com.umcspot.android.HouseFragment
import com.umcspot.android.LikeResponse
import com.umcspot.android.MainActivity
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentRecruitingStudyBinding
import com.umcspot.android.search.SearchFragment
import com.umcspot.android.ui.alert.AlertFragment
import com.umcspot.android.ui.interestarea.ApiResponse
import com.umcspot.android.ui.interestarea.InterestVPAdapter
import com.umcspot.android.ui.interestarea.RecruitingStudyApiService
import com.umcspot.android.ui.study.DetailStudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding
    private lateinit var studyApiService: StudyApiService
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
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        initArguments()
        setupRecyclerView()
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
                replaceFragment(DetailStudyFragment())
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

    private fun calculateStartPage(): Int {
        return when {
            totalPages <= 5 -> 0
            currentPage <= 2 -> 0
            currentPage >= totalPages - 3 -> totalPages - 5
            else -> currentPage - 2
        }
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
                button.setOnClickListener {
                    currentPage = pageNum
                    fetchRecruitingStudy(
                        selectedItem, gender, minAge, maxAge, isOnline,
                        hasFee, minFee, maxFee, themeTypes, currentPage, regionCodes
                    )
                }
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

        binding.previousPage.setOnClickListener {
            currentPage = if (currentPage == 0) totalPages - 1 else currentPage - 1
            fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage, regionCodes)
        }

        binding.nextPage.setOnClickListener {
            currentPage = if (currentPage == totalPages - 1) 0 else currentPage + 1
            fetchRecruitingStudy(selectedItem,gender, minAge,maxAge,isOnline,hasFee,minFee,maxFee,themeTypes,currentPage, regionCodes)
        }
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
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)
                        binding.recruitingStudyReyclerview.visibility = View.VISIBLE
                        binding.pageNumberLayout.visibility = View.VISIBLE
                        startPage = calculateStartPage()
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
//        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
    }


    private fun showError(error: String?) {
//        Toast.makeText(requireContext(), "API 호출 실패: $error", Toast.LENGTH_SHORT).show()
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
                                val adapter = binding.recruitingStudyReyclerview.adapter as InterestVPAdapter
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

    private fun updateLikeButtonUI(likeButton: ImageView, isLiked: Boolean) {
        val newIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.study_like
        likeButton.setImageResource(newIcon)
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
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
            replaceFragment(HouseFragment())
        }

        binding.icAlarmRecruiting.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }
}
