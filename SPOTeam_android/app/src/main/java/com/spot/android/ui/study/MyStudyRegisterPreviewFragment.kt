package com.spot.android.ui.study

import StudyApiService
import StudyViewModel
import UploadResponse
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentMyStudyRegisterPreviewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class MyStudyRegisterPreviewFragment : Fragment() {

    private lateinit var binding: FragmentMyStudyRegisterPreviewBinding
    private val tabList = arrayListOf("홈", "일정", "커뮤니티", "갤러리", "투두쉐어링")
    private val viewModel: StudyViewModel by activityViewModels()
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStudyRegisterPreviewBinding.inflate(inflater, container, false)

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
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${email}_nickname", "Unknown")

        binding.fragmentDetailStudyUsernameTv.text = "${kakaoNickname}님"
        // 스터디 제목 설정
        binding.fragmentDetailStudyPreviewTitleTv.text = viewModel.studyRequest.value?.title

        // 스터디 목표 설정
        binding.fragmentDetailStudyGoalTv.text = viewModel.studyRequest.value?.goal

        val loginPlatform = sharedPreferences.getString("loginPlatform", null)

        val profileImageUrl = when (loginPlatform) {
            "kakao" -> sharedPreferences.getString("${email}_kakaoProfileImageUrl", null)
            "naver" -> sharedPreferences.getString("${email}_naverProfileImageUrl", null)
            else -> viewModel.profileImageUri.value
        }

        Glide.with(this)
            .load(profileImageUrl)
            .error(R.drawable.fragment_calendar_spot_logo)
            .fallback(R.drawable.fragment_calendar_spot_logo)
            .into(binding.fragmentMyStudyRegisterPreviewUserIv)


        // 온라인/오프라인 설정
        binding.fragmentDetailStudyOnlineTv.text = if (viewModel.studyRequest.value?.isOnline == true) "온라인" else "오프라인"

        // 유료/무료 설정
        binding.fragmentDetailStudyFeeTv.text = if (viewModel.studyRequest.value?.hasFee == true) "유료" else "무료"

        // 나이대 설정
        binding.fragmentDetailStudyAgeTv.text = "${viewModel.studyRequest.value?.minAge}-${viewModel.studyRequest.value?.maxAge}세"

        // 스터디 멤버 수 설정
        binding.fragmentDetailStudyMemberMaxTv.text = "${viewModel.studyRequest.value?.maxPeople}"

        binding.fragmentDetailStudyBookmarkTv.text = "0"


        // 스터디 테마 설정 (chip으로 나열)
        val themes = viewModel.studyRequest.value?.themes?.take(3)?.joinToString("/") ?:"테마 없음" // 예: "취업/프로젝트"
        binding.fragmentDetailStudyChipTv.text = themes

    }


    private fun uploadImage(imageUri: Uri, memberId: Int) {
        Log.d("MyStudyDebug", "uploadImage() 호출됨 - memberId: $memberId, imageUri: $imageUri")

        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val imagePart = prepareImagePart(imageUri)

        if (imagePart != null) {
            apiService.uploadImages(listOf(imagePart)).enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    Log.d("MyStudyDebug", "업로드 onResponse - code: ${response.code()}")
                    if (response.isSuccessful) {
                        val uploadResponse = response.body()
                        Log.d("MyStudyDebug", "업로드 성공 응답 body: $uploadResponse")
                        if (uploadResponse != null && uploadResponse.isSuccess) {
                            val imageUrl = uploadResponse.result.imageUrls.firstOrNull()?.imageUrl
                            Log.d("MyStudyDebug", "받은 imageUrl: $imageUrl")
                            if (imageUrl != null) {
                                viewModel.setProfileImageUri(imageUrl)
                            }
                            viewModel.submitStudyData(memberId)
                            showCompletionDialog()
                        } else {
                            Log.e("MyStudyDebug", "업로드 실패 message: ${uploadResponse?.message}")
                        }
                    } else {
                        Log.e("MyStudyDebug", "업로드 실패 code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("MyStudyDebug", "업로드 네트워크 실패: ${t.message}", t)
                }
            })
        } else {
            Log.e("MyStudyDebug", "imagePart가 null이라 업로드 중단")
            Toast.makeText(context, "이미지 파일을 준비할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveUriAsFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // EXIF 회전값 읽기
            val exif = requireContext().contentResolver.openInputStream(uri)?.use { ExifInterface(it) }
            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
                else -> bitmap
            }

            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Bitmap을 회전시키는 확장 함수
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part? {
        val file = saveUriAsFile(uri) ?: return null
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("images", file.name, requestFile)
    }





    private fun showCompletionDialog() {
        val dialog = StudyRegisterCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }

    private fun goToPreviusFragment() {
        parentFragmentManager.popBackStack()
    }
}
