package com.example.spoteam_android.ui.study

import StudyApiService
import StudyViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.GalleryItems
import com.example.spoteam_android.GalleryResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyGalleryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStudyGalleryFragment : Fragment() {

    private lateinit var binding: FragmentMystudyGalleryBinding
    private var currentStudyId = -1
    private var currentPage = 0
    private var nextPage = 1
    private val limit = 9 // 한 페이지에 9개의 항목을 보여줌
    private var startPage = 0
    private val studyViewModel: StudyViewModel by activityViewModels()

    // Retrofit API Service
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMystudyGalleryBinding.inflate(inflater, container, false)

        // API 서비스 초기화
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        // RecyclerView 설정
        binding.communityCategoryContentRv.layoutManager = GridLayoutManager(context, 3)

        // StudyViewModel을 통해 studyId 가져오기
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


        // 페이지 버튼 설정
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
        val call = studyApiService.getStudyImages(currentStudyId, currentPage, limit)
        call.enqueue(object : Callback<GalleryResponse> {
            override fun onResponse(call: Call<GalleryResponse>, response: Response<GalleryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { galleryResponse ->
                        val result = galleryResponse.result
                        val images = result?.images ?: emptyList()

                        if (images.isNotEmpty()) {
                            val adapter = MyStudyGalleryFragmentRVAdapter(images)
                            binding.communityCategoryContentRv.adapter = adapter

                            adapter.itemClick = object : MyStudyGalleryFragmentRVAdapter.ItemClick {
                                override fun onItemClick(data: GalleryItems) {
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
                    } ?: run {
                        Log.e("MyStudyGalleryFragment", "GalleryResponse is null")
                        showNoImagesText()
                        updatePageUI(false)
                    }
                } else {
                    Log.e("MyStudyGalleryFragment", "Failed to load images: ${response.code()}")
                    Toast.makeText(context, "Failed to load images", Toast.LENGTH_SHORT).show()
                    showNoImagesText()
                    updatePageUI(false)
                }
            }

            override fun onFailure(call: Call<GalleryResponse>, t: Throwable) {
                Log.e("MyStudyGalleryFragment", "Failed to load images: ${t.message}")
                Toast.makeText(context, "Failed to load images: ${t.message}", Toast.LENGTH_SHORT).show()
                showNoImagesText()
                updatePageUI(false)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchGalleryImages()
    }

    private fun checkNextPageAvailable() {
        val call = studyApiService.getStudyImages(currentStudyId, nextPage, limit)
        call.enqueue(object : Callback<GalleryResponse> {
            override fun onResponse(call: Call<GalleryResponse>, response: Response<GalleryResponse>) {
                val hasNext = response.body()?.result?.images?.isNotEmpty() == true
                binding.nextPage.isEnabled = hasNext
                updatePageUI(hasNext)
            }

            override fun onFailure(call: Call<GalleryResponse>, t: Throwable) {
                binding.nextPage.isEnabled = false
                updatePageUI(false)
            }
        })
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
