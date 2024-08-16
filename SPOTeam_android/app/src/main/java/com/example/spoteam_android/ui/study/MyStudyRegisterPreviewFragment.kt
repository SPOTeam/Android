package com.example.spoteam_android.ui.study

import ActivityFeeStudyFragment
import StudyApiService
import StudyViewModel
import UploadResponse
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMyStudyRegisterPreviewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class MyStudyRegisterPreviewFragment : Fragment() {

    private lateinit var binding: FragmentMyStudyRegisterPreviewBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")
    private val viewModel: StudyViewModel by activityViewModels()
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStudyRegisterPreviewBinding.inflate(inflater, container, false)

        // ViewModel에서 이미지 URI를 가져옵니다.
        val profileImageUriString = viewModel.profileImageUri.value
        imageUri = profileImageUriString?.let { Uri.parse(it) }

        setupViewPagerAndTabs()
        updateUIWithData()

        // 버튼 클릭 이벤트 설정
        binding.fragmentMyStudyRegisterPreviewRegisterBt.setOnClickListener {
            if (imageUri != null) {
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val email = sharedPreferences.getString("currentEmail", null)

                if (email != null) {
                    val memberId = sharedPreferences.getInt("${email}_memberId", -1)

                    if (memberId != -1) {
                        uploadImage(imageUri!!, memberId)  // memberId를 인자로 전달
                    } else {
                        Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fragmentMyStudyRegisterPreviewBt.setOnClickListener {
            goToPreviusFragment()
        }

        return binding.root
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = MyStudyRegisterPreviewVPAdapter(this)
        binding.fragmentMyStudyRegisterPreviewVp.adapter = pagerAdapter

        TabLayoutMediator(binding.fragmentMyStudyRegisterPreviewTl, binding.fragmentMyStudyRegisterPreviewVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0)?.select()

        binding.fragmentMyStudyRegisterPreviewTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null && tab.position != 0) {
                    binding.fragmentMyStudyRegisterPreviewTl.selectTab(binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        for (i in 1 until binding.fragmentMyStudyRegisterPreviewTl.tabCount) {
            binding.fragmentMyStudyRegisterPreviewTl.getTabAt(i)?.view?.isEnabled = false
        }
    }

    private fun updateUIWithData() {
        // UI 업데이트 코드 (이전 프래그먼트에서 전달된 데이터를 활용)
    }

    private fun uploadImage(imageUri: Uri, memberId: Int) {
        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val imagePart = prepareImagePart(imageUri)

        if (imagePart != null) {
            apiService.uploadImages(listOf(imagePart)).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        val uploadResponse = response.body()
                        if (uploadResponse != null && uploadResponse.isSuccess) {
                            val imageUrls = uploadResponse.result.imageUrls
                            val imageUrl = imageUrls.firstOrNull()?.imageUrl

                            // 이미지 URL을 ViewModel의 profileImageUri에 저장
                            if (imageUrl != null) {
                                viewModel.setProfileImageUri(imageUrl)
                                Log.d("MyStudy", "imageUrl: $imageUrl")
                            }
                            viewModel.submitStudyData(memberId)  // memberId를 사용하여 데이터 전송

                            showCompletionDialog()
                        } else {
                            println("Upload failed: ${uploadResponse?.message}")
                        }
                    } else {
                        println("Upload failed with response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    println("Upload error: ${t.message}")
                }
            })
        } else {
            Toast.makeText(context, "이미지 파일을 준비할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUriAsPng(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // PNG 파일로 저장할 임시 파일 생성
            val tempFile = File(requireContext().cacheDir, "temp_image.png")
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            tempFile // 생성된 파일 반환
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part? {
        val file = saveUriAsPng(uri)
        return file?.let {
            val requestFile = it.asRequestBody("image/png".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", it.name, requestFile)
        }
    }



    private fun showCompletionDialog() {
        val dialog = StudyRegisterCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm,ActivityFeeStudyFragment()) // 이전 프래그먼트로 전환
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
