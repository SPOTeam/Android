package com.example.spoteam_android.ui.study

import StudyApiService
import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.GalleryResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyGalleryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStudyGalleryFragment : Fragment() {

    private lateinit var binding: FragmentMystudyGalleryBinding
    private var currentPage = 0
    private val limit = 15 // 한 페이지에 15개의 항목을 보여줌
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
                loadPage(studyId, currentPage)
            } else {
                Log.e("MyStudyGalleryFragment", "Invalid studyId: $studyId")
                showNoImagesText()
            }
        }

        // 페이지 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                studyViewModel.studyId.value?.let { loadPage(it, currentPage) }
            }
        }

        binding.nextPage.setOnClickListener {
            currentPage++
            studyViewModel.studyId.value?.let { loadPage(it, currentPage) }
        }

        return binding.root
    }

    private fun loadPage(studyId: Int, page: Int) {
        val offset = page // 페이지 번호를 그대로 offset으로 사용
        Log.d("MyStudyGalleryFragment", "loadPage() called with: studyId = $studyId, page = $page, offset = $offset")
        fetchGalleryImages(studyId, offset, limit)
    }

    private fun fetchGalleryImages(studyId: Int, offset: Int, limit: Int) {
        val call = studyApiService.getStudyImages(studyId, offset, limit)
        call.enqueue(object : Callback<GalleryResponse> {
            override fun onResponse(call: Call<GalleryResponse>, response: Response<GalleryResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { galleryResponse ->
                        val result = galleryResponse.result
                        val images = result?.images ?: emptyList()

                        if (images.isNotEmpty()) {
                            val adapter = MyStudyGalleryFragmentRVAdapter(images)
                            binding.communityCategoryContentRv.adapter = adapter

                            // 이미지가 있을 때
                            showGallery()
                        } else {
                            // 이미지가 없을 때
                            showNoImagesText()
                        }

                        updatePagination(images.size)
                    } ?: run {
                        Log.e("MyStudyGalleryFragment", "GalleryResponse is null")
                        showNoImagesText()
                    }
                } else {
                    Log.e("MyStudyGalleryFragment", "Failed to load images: ${response.code()}")
                    Toast.makeText(context, "Failed to load images", Toast.LENGTH_SHORT).show()
                    showNoImagesText()
                }
            }

            override fun onFailure(call: Call<GalleryResponse>, t: Throwable) {
                Log.e("MyStudyGalleryFragment", "Failed to load images: ${t.message}")
                Toast.makeText(context, "Failed to load images: ${t.message}", Toast.LENGTH_SHORT).show()
                showNoImagesText()
            }
        })
    }

    private fun showGallery() {
        binding.communityCategoryContentRv.visibility = View.VISIBLE
        binding.noImagesText.visibility = View.GONE
        Log.d("MyStudyGalleryFragment", "Images loaded, hiding no images text")
    }

    private fun showNoImagesText() {
        binding.communityCategoryContentRv.visibility = View.GONE
        binding.noImagesText.visibility = View.VISIBLE
        Log.d("MyStudyGalleryFragment", "No images found, showing no images text")
    }

    private fun updatePagination(imageCount: Int) {
        binding.currentPage.text = (currentPage + 1).toString()
        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = imageCount == limit
    }
}
