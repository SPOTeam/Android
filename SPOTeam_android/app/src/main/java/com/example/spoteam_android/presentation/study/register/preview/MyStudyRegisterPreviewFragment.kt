package com.example.spoteam_android.presentation.study.register.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMyStudyRegisterPreviewBinding
import com.example.spoteam_android.presentation.study.register.util.StudyRegisterCompleteDialog
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MyStudyRegisterPreviewFragment : Fragment() {

    private lateinit var binding: FragmentMyStudyRegisterPreviewBinding

    private val registerViewModel: StudyRegisterViewModel by activityViewModels()

    private val studyViewModel: StudyViewModel by activityViewModels()

    private var imageUri: Uri? = null
    private val tabList = arrayListOf("홈", "일정", "커뮤니티", "갤러리", "투두쉐어링")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyStudyRegisterPreviewBinding.inflate(inflater, container, false)

        studyViewModel.loadNickname()

        setupViewPagerAndTabs()

        setProfileImageUri()

        updateUIWithData()

        lifecycleScope.launch {
            registerViewModel.localImageUri.collect { uriString ->
                imageUri = uriString?.let { Uri.parse(it) }
            }
        }

        binding.fragmentMyStudyRegisterPreviewRegisterBt.setOnClickListener {
            onRegisterButtonClicked()
        }

        // 6) “뒤로가기” 버튼
        binding.fragmentMyStudyRegisterPreviewBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = MyStudyRegisterPreviewVPAdapter(this)
        binding.fragmentMyStudyRegisterPreviewVp.adapter = pagerAdapter

        TabLayoutMediator(
            binding.fragmentMyStudyRegisterPreviewTl,
            binding.fragmentMyStudyRegisterPreviewVp
        ) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0)?.select()
        binding.fragmentMyStudyRegisterPreviewTl.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null && tab.position != 0) {
                    binding.fragmentMyStudyRegisterPreviewTl.selectTab(
                        binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0)
                    )
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        for (i in 1 until binding.fragmentMyStudyRegisterPreviewTl.tabCount) {
            binding.fragmentMyStudyRegisterPreviewTl.getTabAt(i)?.view?.isEnabled = false
        }
    }
    private fun setProfileImageUri() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val loginPlatform = sharedPreferences.getString("loginPlatform", null)
            val profileImageUrl = if (!email.isNullOrEmpty() && !loginPlatform.isNullOrEmpty()) {
                sharedPreferences.getString("${email}_${loginPlatform}ProfileImageUrl", null)
            } else {
                null
            }
            Glide.with(binding.root.context)
                .load(profileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentMyStudyRegisterPreviewUserIv)

        }
    }
    private fun updateUIWithData() {
        studyViewModel.nickname.observe(viewLifecycleOwner) { nick ->
            binding.fragmentDetailStudyUsernameTv.text = "$nick 님"
        }

        binding.fragmentDetailStudyPreviewTitleTv.text =
            registerViewModel.studyRequest.value?.title
        binding.fragmentDetailStudyGoalTv.text =
            registerViewModel.studyRequest.value?.goal

        binding.fragmentDetailStudyOnlineTv.text =
            if (registerViewModel.studyRequest.value?.isOnline == true) "온라인" else "오프라인"
        binding.fragmentDetailStudyFeeTv.text =
            if (registerViewModel.studyRequest.value?.hasFee == true) "유료" else "무료"
        binding.fragmentDetailStudyAgeTv.text =
            "${registerViewModel.studyRequest.value?.minAge}-${registerViewModel.studyRequest.value?.maxAge}세"
        binding.fragmentDetailStudyMemberMaxTv.text =
            registerViewModel.studyRequest.value?.maxPeople.toString()
        binding.fragmentDetailStudyBookmarkTv.text = "0"
        binding.fragmentDetailStudyChipTv.text =
            registerViewModel.studyRequest.value?.themes?.take(3)?.joinToString("/") ?: "테마 없음"
    }

    private fun onRegisterButtonClicked() {

        if (imageUri == null) {
            Toast.makeText(requireContext(), "이미지를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 로컬 이미지가 null이 아니면 Multipart로 변환 후 업로드
        prepareImagePart(imageUri!!)?.let { imagePart ->
            registerViewModel.uploadImageAndSubmitStudy(
                imagePart = imagePart,
                onSuccess = { showCompletionDialog() },
                onFailure = {
                    Toast.makeText(requireContext(), "스터디 등록 실패", Toast.LENGTH_SHORT).show()
                }
            )
        } ?: run {
            Toast.makeText(requireContext(), "이미지 파일 준비 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun prepareImagePart(uri: Uri): MultipartBody.Part? {
        return saveUriAsFile(uri)?.let { file ->
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("images", file.name, requestFile)
        }
    }

    private fun saveUriAsFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val tempFile = File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Log.d("PreviewFragment", "Saved temp file at: ${tempFile.absolutePath}")
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showCompletionDialog() {
        val dialog = StudyRegisterCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }
}
