package com.example.spoteam_android.presentation.category.category_tabs

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
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.presentation.community.CategoryStudyResponse
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.study.StudyViewModel

import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentCategoryStudyContentBinding
    private val studyViewModel: StudyViewModel by activityViewModels()

    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0
    private var isLoading = false
    private var selectedSortBy: String = "ALL"
    private val theme :String = "기타"
    private val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)

        val categoryStudyAdapter = CategoryStudyContentRVAdapter(
            ArrayList(),
            onLikeClick = { selectedItem, likeButton ->
                toggleLikeStatus(selectedItem, likeButton)
            })

        binding.communityCategoryContentRv.adapter = categoryStudyAdapter

        categoryStudyAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: StudyDataResponse.StudyContent) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                val detailStudyFragment = com.example.spoteam_android.presentation.study.DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)?.addToBackStack(null)?.commit()
            }
        })

        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext())
        fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        initBottomFilterView()
        return binding.root
    }

    private fun initBottomFilterView() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_interest_spinner, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        dialogView.findViewById<FrameLayout>(R.id.framelayout_recently).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "최신 순"
            selectedSortBy = "ALL"
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        dialogView.findViewById<FrameLayout>(R.id.framelayout_view).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            selectedSortBy = "HIT"
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        dialogView.findViewById<FrameLayout>(R.id.framelayout_hot).setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            selectedSortBy = "LIKED"
            fetchBestCommunityContent(theme, currentPage, size, selectedSortBy)
        }

        binding.contentFilterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
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
    }

    private fun fetchBestCommunityContent(theme: String, page: Int, size: Int, sortBy: String) {
        isLoading = true
        service.getCategoryStudy(theme, page, size, sortBy).enqueue(object : Callback<CategoryStudyResponse> {
            override fun onResponse(call: Call<CategoryStudyResponse>, response: Response<CategoryStudyResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val contentList = it.result?.content ?: emptyList()
                        val mappedList = contentList?.map {
                            StudyDataResponse.StudyContent(
                                studyId = it.studyId,
                                title = it.title,
                                goal = it.goal,
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
                        } ?: emptyList()
                        totalPages = it.result?.totalPages ?: 0
                        binding.contentCountTv.text = it.result?.totalElements?.toString() ?: "0"
                        if (contentList.isNotEmpty()) {
                            updateRecyclerView(mappedList)
                            binding.emptyTv.visibility = View.GONE
                        } else if (currentPage == 0) {
                            binding.emptyTv.visibility = View.VISIBLE
                        }
                        updatePageUI()
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

    private fun updateRecyclerView(contentList: List<StudyDataResponse.StudyContent>) {
        val adapter = binding.communityCategoryContentRv.adapter as? CategoryStudyContentRVAdapter
        adapter?.updateList(contentList)
    }

    private fun updatePageUI() {
        startPage = if (currentPage <= 2) 0 else minOf(totalPages - 5, maxOf(0, currentPage - 2))
        val pageButtons = listOf(binding.page1, binding.page2, binding.page3, binding.page4, binding.page5)

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
                    val adapter = binding.communityCategoryContentRv.adapter as? CategoryStudyContentRVAdapter
                    adapter?.notifyItemChanged(adapter.dataList.indexOf(studyItem))
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedSortBy = when (parent?.getItemAtPosition(position).toString()) {
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

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedSortBy = "ALL"
    }
}