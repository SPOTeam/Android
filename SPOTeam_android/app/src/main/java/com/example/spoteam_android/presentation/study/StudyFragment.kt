package com.example.spoteam_android.presentation.study

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentStudyBinding
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.presentation.study.adapter.StudyAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    private lateinit var studyAdapter: StudyAdapter
    private var itemList = ArrayList<StudyDataResponse.StudyContent>()

    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0

    private lateinit var pageTextViews: List<TextView>

    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)

        binding.prevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studyAdapter = StudyAdapter(
            itemList,
            onItemClick = { selectedItem ->
                studyViewModel.setStudyData(
                    selectedItem.studyId,
                    selectedItem.imageUrl,
                    selectedItem.introduction
                )
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, DetailStudyFragment())
                    .addToBackStack(null)
                    .commit()
            },
            onLikeClick = { selectedItem, likeButton ->
                studyViewModel.toggleLikeStatus(selectedItem.studyId) { result ->
                    result.onSuccess { liked ->
                        selectedItem.liked = liked
                        val newIcon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                        likeButton.setImageResource(newIcon)
                        selectedItem.heartCount += if (liked) 1 else -1
                        studyAdapter.notifyItemChanged(itemList.indexOf(selectedItem))
                    }.onFailure {
                        Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )

        binding.fragmentStudyRv.apply {
            adapter = studyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        initPaginationControls()
        fetchStudyData()
    }

    private fun initPaginationControls() {
        pageTextViews = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        pageTextViews.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (selectedPage < totalPages) {
                    currentPage = selectedPage
                    fetchStudyData()
                }
            }
        }

        binding.previousPage.setOnClickListener {
            if (startPage >= 5) {
                startPage -= 5
                currentPage = startPage
                fetchStudyData()
            }
        }

        binding.nextPage.setOnClickListener {
            if (startPage + 5 < totalPages) {
                startPage += 5
                currentPage = startPage
                fetchStudyData()
            }
        }
    }

    private fun updatePaginationUI() {
        pageTextViews.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                textView.visibility = View.VISIBLE
                textView.text = (pageNum + 1).toString()
                textView.setTextColor(
                    if (pageNum == currentPage) resources.getColor(R.color.b500)
                    else resources.getColor(R.color.black)
                )
            } else {
                textView.visibility = View.GONE
            }
        }
    }

    private fun fetchStudyData() {
        Log.d("StudyFragment", "Fetching page $currentPage, size $size")

        studyViewModel.fetchStudyData(currentPage, size) { result ->
            result.onSuccess { data ->
                val content = data.content
                if (!content.isNullOrEmpty()) {
                    itemList.clear()
                    itemList.addAll(content)
                    totalPages = data.totalPages
                    studyAdapter.updateList(itemList)

                    updatePaginationUI()

                    binding.fragmentStudyRv.visibility = View.VISIBLE
                    binding.emptyMessage.visibility = View.GONE
                } else {
                    binding.fragmentStudyRv.visibility = View.GONE
                    binding.emptyMessage.visibility = View.VISIBLE
                }
            }.onFailure {
                Toast.makeText(requireContext(), "불러오기 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

