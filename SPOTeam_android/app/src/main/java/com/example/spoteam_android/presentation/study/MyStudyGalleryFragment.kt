package com.example.spoteam_android.presentation.study

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMystudyGalleryBinding
import com.example.spoteam_android.domain.study.entity.ImageResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyStudyGalleryFragment : Fragment() {

    private lateinit var binding: FragmentMystudyGalleryBinding
    private var currentStudyId = -1
    private var currentPage = 0
    private var nextPage = 1
    private val limit = 9 // 한 페이지에 9개의 항목을 보여줌
    private var startPage = 0
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyGalleryBinding.inflate(inflater, container, false)

        // RecyclerView 설정
        binding.communityCategoryContentRv.layoutManager = GridLayoutManager(context, 3)

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null && studyId != 0) {
                currentStudyId = studyId
                fetchGalleryImages()
            } else {
                Log.e("MyStudyGalleryFragment", "Invalid studyId: $studyId")
                showNoImagesText()
            }
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
                    nextPage = currentPage + 1
                    fetchGalleryImages()
                }
            }
        }

        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                nextPage--
                fetchGalleryImages()
            }
        }

        binding.nextPage.setOnClickListener {
            currentPage++
            nextPage++
            fetchGalleryImages()
        }

        return binding.root
    }

    private fun fetchGalleryImages() {
        lifecycleScope.launch {
            val result = studyViewModel.getStudyImages(currentStudyId, currentPage, limit)
            result.onSuccess { imageResponse ->
                val images = imageResponse.images
                if (images.isNotEmpty()) {
                    val adapter = MyStudyGalleryFragmentRVAdapter(images)
                    binding.communityCategoryContentRv.adapter = adapter

                    adapter.itemClick = object : MyStudyGalleryFragmentRVAdapter.ItemClick {
                        override fun onItemClick(data: ImageResponse.ImagesInfo) {
                            val intent = Intent(requireContext(), MyStudyPostContentActivity::class.java)
                            intent.putExtra("myStudyId", currentStudyId.toString())
                            intent.putExtra("myStudyPostId", data.postId.toString())
                            startActivity(intent)
                        }
                    }

                    showGallery()
                    checkNextPageAvailable()
                } else {
                    showNoImagesText()
                    updatePageUI(false)
                }
            }.onFailure {
                Log.e("MyStudyGalleryFragment", "Failed to load images: ${it.message}")
                Toast.makeText(context, "Failed to load images", Toast.LENGTH_SHORT).show()
                showNoImagesText()
                updatePageUI(false)
            }
        }
    }

    private fun checkNextPageAvailable() {
        lifecycleScope.launch {
            val result = studyViewModel.getStudyImages(currentStudyId, nextPage, limit)
            val hasNext = result.getOrNull()?.images?.isNotEmpty() == true
            binding.nextPage.isEnabled = hasNext
            updatePageUI(hasNext)
        }
    }

    private fun showGallery() {
        binding.communityCategoryContentRv.visibility = View.VISIBLE
        binding.noImagesText.visibility = View.GONE
        binding.imageNoneIv.visibility = View.GONE
        binding.pageNumberLayout.visibility = View.VISIBLE
    }

    private fun showNoImagesText() {
        binding.communityCategoryContentRv.visibility = View.GONE
        binding.noImagesText.visibility = View.VISIBLE
        binding.imageNoneIv.visibility = View.VISIBLE
        binding.pageNumberLayout.visibility = View.GONE
    }

    private fun updatePageUI(hasNext: Boolean) {
        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        startPage = maxOf(0, currentPage - 2)
        val maxAvailablePage = if (hasNext) currentPage + 1 else currentPage

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum <= maxAvailablePage) {
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
                textView.isEnabled = false
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
    }
}