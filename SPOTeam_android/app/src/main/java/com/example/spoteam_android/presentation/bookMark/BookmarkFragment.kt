package com.example.spoteam_android.presentation.bookMark

import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentBookmarkBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.domain.study.entity.StudyDataResponse.StudyContent
import com.example.spoteam_android.presentation.study.DetailStudyFragment
import com.example.spoteam_android.presentation.study.StudyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookMarkRVAdapter: BookMarkRVAdapter
    private var bookitemList = ArrayList<StudyContent>()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private var startPage = 0
    private var currentPage = 0
    private val size = 5
    private var totalPages = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        binding.prevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookMarkRVAdapter = BookMarkRVAdapter(bookitemList, onItemClick = { selectedItem ->
            studyViewModel.setStudyData(
                selectedItem.studyId,
                selectedItem.imageUrl,
                selectedItem.introduction
            )
            val detailStudyFragment = DetailStudyFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, detailStudyFragment)
                .addToBackStack(null)
                .commit()
        }, onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        binding.fragmentBookmarkRv.apply {
            adapter = bookMarkRVAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

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
                    studyViewModel.getBookmarkList(currentPage, size)
                }
            }
        }

        setupPageNavigationButtons()
        observeBookmarkList()
        studyViewModel.getBookmarkList(currentPage, size)
    }

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                studyViewModel.getBookmarkList(currentPage, size)
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                studyViewModel.getBookmarkList(currentPage, size)
            }
        }
    }

    private fun observeBookmarkList() {
        studyViewModel.bookmarkListLiveData.observe(viewLifecycleOwner, Observer { content ->
            if (!content.isNullOrEmpty()) {
                bookitemList.clear()
                bookitemList.addAll(content)
                bookMarkRVAdapter.updateList(bookitemList)
                binding.bookmarkEmpty.visibility = View.GONE
                binding.fragmentBookmarkRv.visibility = View.VISIBLE
            } else {
                binding.bookmarkEmpty.visibility = View.VISIBLE
                binding.fragmentBookmarkRv.visibility = View.GONE
            }
            updatePageUI()
        })

        studyViewModel.totalPages.observe(viewLifecycleOwner, Observer { pages ->
            totalPages = pages
            updatePageUI()
        })
    }

    private fun toggleLikeStatus(studyItem: StudyContent, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    val newIcon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(newIcon)
                    studyItem.heartCount += if (liked) 1 else -1
                    bookMarkRVAdapter.notifyItemChanged(bookitemList.indexOf(studyItem))
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
}