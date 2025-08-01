package com.umcspot.android.ui.category.category_tabs

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.umcspot.android.LikeResponse
import com.umcspot.android.MainActivity
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentCategoryStudyContentBinding
import com.umcspot.android.ui.community.CategoryStudyDetail
import com.umcspot.android.ui.community.CategoryStudyResponse
import com.umcspot.android.ui.community.CommunityAPIService
import com.umcspot.android.ui.study.DetailStudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentCategoryStudyContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService

    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0
    private var isLoading = false
    private var selectedSortBy: String = "ALL"
    private val theme :String = "기타"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)


        val categoryStudyAdapter = CategoryStudyContentRVAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        binding.communityCategoryContentRv.adapter = categoryStudyAdapter

        categoryStudyAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryStudyDetail) {
                // DetailStudyFragment로 이동
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })
        binding.communityCategoryContentRv.adapter = categoryStudyAdapter
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext())




        fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        initBottomFilterView()
        return binding.root
    }

    private fun initBottomFilterView() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_interest_spinner, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        val recentlyLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_recently)
        val viewLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_view)
        val hotLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_hot)

        recentlyLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "최신 순"
            selectedSortBy="ALL"  // 최신 순
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        viewLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            selectedSortBy="HIT"
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        hotLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            selectedSortBy="LIKED"  // 관심 많은 순
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        binding.contentFilterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
                }
            }
        }

        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
            }
        }
        fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
    }



    private fun fetchBestCommunityContent(theme: String, page: Int, size: Int, sortBy: String) {
        isLoading = true
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getCategoryStudy(theme, page, size, sortBy)
            .enqueue(object : Callback<CategoryStudyResponse> {
                override fun onResponse(
                    call: Call<CategoryStudyResponse>,
                    response: Response<CategoryStudyResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { categoryStudyResponse ->
                            if (categoryStudyResponse.isSuccess == "true") {
                                val contentList = categoryStudyResponse.result?.content ?: emptyList()
                                totalPages = categoryStudyResponse.result.totalPages

                                val totalElements = categoryStudyResponse.result.totalElements
                                binding.contentCountTv.text = totalElements.toString()

                                if (contentList.isNotEmpty()) {
                                    updateRecyclerView(contentList)
                                    binding.emptyTv.visibility = View.GONE
                                } else if (currentPage == 0) {
                                    binding.emptyTv.visibility = View.VISIBLE
                                }

                                updatePageUI()
                            }
                        }
                    } else {
                        showLog(response.code().toString())
                    }
                    isLoading = false
                }

                override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
                    showLog(t.message)
                    isLoading = false
                }
            })
    }

    private fun updateRecyclerView(contentList: List<CategoryStudyDetail>) {
        // 어댑터가 초기화되지 않은 경우 초기화
        if (binding.communityCategoryContentRv.adapter == null) {
            val categoryStudyAdapter = CategoryStudyContentRVAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
                toggleLikeStatus(selectedItem, likeButton)
            })
            binding.communityCategoryContentRv.adapter = categoryStudyAdapter
            binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext())
        }

        // 어댑터가 이미 존재하는 경우 데이터를 추가
        val adapter = binding.communityCategoryContentRv.adapter as CategoryStudyContentRVAdapter
        adapter.updateList(contentList)
    }


    private fun updatePageUI() {
        startPage = if (currentPage <= 2) {
            0
        } else {
            minOf(totalPages - 5, maxOf(0, currentPage - 2))
        }

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

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "DiscussionFragment: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        selectedSortBy = when (selectedItem) {
            "전체" -> "ALL"
            "모집중" -> "RECRUITING"
            "모집완료" -> "COMPLETED"
            "조회수순" -> "HIT"
            "관심순" -> "LIKED"
            else -> "ALL"
        }
        currentPage = 0
        fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
    }

    private fun toggleLikeStatus(studyItem: CategoryStudyDetail, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 CategoryStudyDetail의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.communityCategoryContentRv.adapter as CategoryStudyContentRVAdapter
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


    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedSortBy = "ALL"
    }
}
