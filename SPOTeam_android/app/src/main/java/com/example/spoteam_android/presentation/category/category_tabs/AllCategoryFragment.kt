package com.example.spoteam_android.presentation.category.category_tabs

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
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.presentation.community.CategoryStudyResponse
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.interestarea.InterestFilterFragment
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class AllCategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryStudyContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()

    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0
    private var isLoading = false
    private var selectedSortBy: String = "ALL"
    private lateinit var categoryStudyAdapter: CategoryStudyContentRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryStudyAdapter = CategoryStudyContentRVAdapter(arrayListOf(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        categoryStudyAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: StudyDataResponse.StudyContent) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                val detailFragment = com.example.spoteam_android.presentation.study.DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailFragment)?.addToBackStack(null)?.commit()
            }
        })

        binding.communityCategoryContentRv.apply {
            adapter = categoryStudyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    fetchBestCommunityContent(currentPage, size, selectedSortBy)
                }
            }
        }

        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchBestCommunityContent(currentPage, size, selectedSortBy)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchBestCommunityContent(currentPage, size, selectedSortBy)
            }
        }

        initBottomFilterView()
        fetchBestCommunityContent(currentPage, size, selectedSortBy)
    }

    private fun initBottomFilterView() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_interest_spinner, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        dialogView.findViewById<FrameLayout>(R.id.framelayout_recently).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "최신 순"
            selectedSortBy = "ALL"
            fetchBestCommunityContent(currentPage, size, selectedSortBy)
        }
        dialogView.findViewById<FrameLayout>(R.id.framelayout_view).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            selectedSortBy = "HIT"
            fetchBestCommunityContent(currentPage, size, selectedSortBy)
        }
        dialogView.findViewById<FrameLayout>(R.id.framelayout_hot).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            selectedSortBy = "LIKED"
            fetchBestCommunityContent(currentPage, size, selectedSortBy)
        }

        binding.contentFilterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }

        initFilter()
    }

    private fun initFilter() {
        binding.icFilter.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }
        binding.icFilterActive.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }
    }

    private fun fetchBestCommunityContent(page: Int, size: Int, sortBy: String) {

        isLoading = true
        val service = com.example.spoteam_android.RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getAllStudy(page, size, sortBy).enqueue(object : Callback<CategoryStudyResponse> {
            override fun onResponse(call: Call<CategoryStudyResponse>, response: Response<CategoryStudyResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { res ->
                        val contentList = res.result?.content ?: emptyList()
                        val mappedList = contentList.map {
                            StudyDataResponse.StudyContent(
                                studyId = it.studyId,
                                title = it.title,
                                goal = "", // CategoryStudyDetail에 없으므로 빈값 처리
                                introduction = it.introduction,
                                memberCount = it.memberCount,
                                heartCount = it.heartCount,
                                hitNum = it.hitNum,
                                maxPeople = it.maxPeople,
                                studyState = it.studyState,
                                themeTypes = it.themeTypes,
                                regions = it.regions,
                                imageUrl = it.imageUrl,
                                liked = it.liked,
                                createdAt = it.createdAt
                            )
                        }
                        totalPages = res.result?.totalPages ?: 0

                        binding.contentCountTv.text = res.result?.totalElements?.toString() ?: "0"

                        if (contentList.isNotEmpty()) {
                            categoryStudyAdapter.updateList(mappedList)
                            binding.emptyTv.visibility = View.GONE
                        } else if (currentPage == 0) {
                            binding.emptyTv.visibility = View.VISIBLE
                        }
                        updatePageUI()
                    }
                } else {
                    Toast.makeText(requireContext(), "불러오기 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }

            override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        })
    }

    private fun updatePageUI() {
        startPage = if (currentPage <= 2) 0 else minOf(totalPages - 5, maxOf(0, currentPage - 2))

        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(if (pageNum == currentPage) R.drawable.btn_page_bg else 0)
                textView.isEnabled = true
                textView.alpha = 1f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = ""
                textView.setBackgroundResource(0)
                textView.isEnabled = false
                textView.alpha = 0.3f
                textView.visibility = View.INVISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPages - 1
    }

    private fun toggleLikeStatus(studyItem: StudyDataResponse.StudyContent, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    val icon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(icon)
                    studyItem.heartCount += if (liked) 1 else -1
                    categoryStudyAdapter.notifyItemChanged(
                        categoryStudyAdapter.dataList.indexOf(studyItem)
                    )
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}