package com.spot.android.search

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spot.android.BoardItem
import com.spot.android.HouseFragment
import com.spot.android.LikeResponse
import com.spot.android.MainActivity
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentSearchResultBinding
import com.spot.android.ui.alert.AlertFragment
import com.spot.android.ui.interestarea.ApiResponse
import com.spot.android.ui.interestarea.InterestVPAdapter
import com.spot.android.ui.study.DetailStudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchResultFragment : Fragment() {

    private var currentPage = 0
    private var totalPages = 0
    private var sortBy = "ALL"
    private var keyword: String? = null
    private var startPage = 0

    private lateinit var interestBoardAdapter: InterestVPAdapter
    private lateinit var binding: FragmentSearchResultBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)


        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        },studyViewModel = studyViewModel)

        binding.spotLogo.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HouseFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 어댑터 초기화
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

        binding.icFind.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarm.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.searchResultStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }

        // 전달된 키워드를 통해 API 호출
        val keyword = arguments?.getString("search_keyword")
        keyword?.let {
            fetchGetSearchStudy(keyword=it,sortBy="ALL")
        }

        setupBottomSheet()
        return binding.root
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
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
                                val adapter = binding.searchResultStudyReyclerview.adapter as InterestVPAdapter
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
            Log.d("Search","${keyword}, ALL")
            keyword?.let { it1 -> fetchGetSearchStudy(keyword = it1,sortBy="ALL") }  // 최신 순
        }

        viewLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            Log.d("Search","${keyword}, HIT")
            keyword?.let { it1 -> fetchGetSearchStudy(keyword = it1,sortBy="HIT") }  // 최신 순
        }

        hotLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            Log.d("Search","${keyword}, Liked")
            keyword?.let { it1 -> fetchGetSearchStudy(keyword = it1,sortBy="LIKED") }  // 최신 순
        }

        binding.filterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }


    private fun fetchGetSearchStudy(keyword: String, page: Int = 0, sortBy:String) {
        this.keyword = keyword
        this.sortBy = sortBy

        val service = RetrofitInstance.retrofit.create(SearchApiService::class.java)

        service.PostSearchApi(
            keyword = keyword,
            page = currentPage ?: 0,
            size = 5,
            sortBy = sortBy
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        val boardItems = apiResponse.result?.content?.map { study ->
                            BoardItem(
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
                        } ?: emptyList()

                        totalPages = apiResponse.result.totalPages
                        updateRecyclerView(boardItems)
                        binding.searchResultStudyReyclerview.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = String.format("%02d건", totalElements)

                        binding.pageNumberLayout.visibility = View.VISIBLE
                        startPage = calculateStartPage()
                        updatePageNumberUI()
                        updatePageUI()
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        binding.checkAmount.text = "00건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.searchResultStudyReyclerview.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
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
            val selectedPage = startPage + index
            textView.setOnClickListener {
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    requestPageUpdate()
                }
            }
        }

        // 왼쪽 버튼: 첫 페이지면 마지막 페이지로 이동
        binding.previousPage.setOnClickListener {
            currentPage = if (currentPage == 0) {
                totalPages - 1
            } else {
                currentPage - 1
            }
            requestPageUpdate()
        }

        // 오른쪽 버튼: 마지막 페이지면 첫 페이지로 이동
        binding.nextPage.setOnClickListener {
            currentPage = if (currentPage == totalPages - 1) {
                0
            } else {
                currentPage + 1
            }
            requestPageUpdate()
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

    private fun requestPageUpdate() {
        keyword?.let {
            fetchGetSearchStudy(keyword=it,sortBy="ALL")
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
}
