package com.example.spoteam_android.ui.study

import StudyViewModel
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentIntroduceStudyBinding

class IntroduceStudyFragment : Fragment() {

    private lateinit var binding: FragmentIntroduceStudyBinding
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val viewModel: StudyViewModel by activityViewModels() // Activity 범위에서 ViewModel을 공유
    private var profileImage: String? = null // 이미지 URI를 저장할 변수 추가

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroduceStudyBinding.inflate(inflater, container, false)

        viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
            if (viewModel.mode.value == StudyFormMode.EDIT && request != null) {
                binding.fragmentIntroduceStudynameEt.setText(request.title)
                binding.fragmentIntroduceStudypurposeEt.setText(request.goal)
                binding.fragmentIntroduceStudyEt.setText(request.introduction)

                request.profileImage?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .into(binding.fragmentIntroduceStudyIv)

                    profileImage = imageUrl
                }
                checkButtonState()

            }
        }



        // 초기화 함수 호출
        initActivityResultLaunchers()
        initAddImageButton()
        setupTextWatchers()
        checkButtonState() // 초기 상태 체크

        binding.fragmentIntroduceStudyBt.setOnClickListener {
            if (profileImage == null) { // profileImage가 null인지 확인
                Toast.makeText(context, "프로필 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // 이미지가 선택되지 않으면 함수 종료
            }

            saveStudyData()
            goToNextFragment()
        }

        binding.fragmentIntroduceStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }

        return binding.root
    }

    private fun initActivityResultLaunchers() {
        // 이미지 선택을 위한 ActivityResultLauncher 초기화
        getImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageURI: Uri? = result.data?.data
                if (selectedImageURI != null) {
                    binding.fragmentIntroduceStudyIv.setImageURI(selectedImageURI)
                    profileImage = selectedImageURI.toString() // 이미지 URI를 변수에 저장
                    viewModel.setProfileImageUri(profileImage) // URI를 ViewModel에 저장
                    checkButtonState() // 이미지 선택 후 버튼 상태 체크
                } else {
                    Toast.makeText(context, "이미지를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "이미지를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 권한 요청을 위한 ActivityResultLauncher 초기화
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getImageFromAlbum()
            } else {
                Toast.makeText(context, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initAddImageButton() {
        binding.fragmentIntroduceStudyIv.setOnClickListener {
            Log.d("IntroduceStudyFragment", "addImageButton called!!")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13 이상에서는 새로운 권한을 요청
                when {
                    ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                        // 권한이 존재하는 경우
                        getImageFromAlbum()
                    }
                    else -> {
                        // 권한 요청
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            } else {
                // Android 12 이하에서는 기존 권한 사용
                when {
                    ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                        // 권한이 존재하는 경우
                        getImageFromAlbum()
                    }
                    else -> {
                        // 권한 요청
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        }
    }

    private fun getImageFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        getImageLauncher.launch(intent)
    }

    private fun setupTextWatchers() {
        val editTextList = listOf(
            binding.fragmentIntroduceStudynameEt,
            binding.fragmentIntroduceStudypurposeEt,
            binding.fragmentIntroduceStudyEt
        )

        for (editText in editTextList) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkButtonState()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun checkButtonState() {
        val isEditTextFilled = binding.fragmentIntroduceStudynameEt.text.isNotEmpty() &&
                binding.fragmentIntroduceStudypurposeEt.text.isNotEmpty() &&
                binding.fragmentIntroduceStudyEt.text.isNotEmpty()

        // profileImage가 null이 아닌지 확인
        val isImageSelected = profileImage != null

        // 모든 조건이 만족해야 버튼을 활성화
        binding.fragmentIntroduceStudyBt.isEnabled = isEditTextFilled && isImageSelected
    }

    private fun saveStudyData() {
        val title = binding.fragmentIntroduceStudynameEt.text.toString()
        val goal = binding.fragmentIntroduceStudypurposeEt.text.toString()
        val introduction = binding.fragmentIntroduceStudyEt.text.toString()

        // ✅ ViewModel에서 기존 값을 가져옴
        val isOnline = viewModel.studyRequest.value?.isOnline ?: true
        val regions = viewModel.studyRequest.value?.regions
        val maxPeople = viewModel.studyRequest.value?.maxPeople ?: 0
        val gender = viewModel.studyRequest.value?.gender ?: Gender.UNKNOWN
        val minAge = viewModel.studyRequest.value?.minAge ?: 0
        val maxAge = viewModel.studyRequest.value?.maxAge ?: 0
        val fee = viewModel.studyRequest.value?.fee ?: 0

        viewModel.setStudyData(
            title = title,
            goal = goal,
            introduction = introduction,
            isOnline = isOnline, // ✅ 수정 모드에서도 덮어쓰지 않음
            profileImage = profileImage,
            regions = regions,
            maxPeople = maxPeople,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            fee = fee
        )
    }


    private fun goToNextFragment() {
        val nextFragment = OnlineStudyFragment().apply {
            arguments = Bundle().apply {
                putSerializable("mode", viewModel.mode.value)
                viewModel.studyId.value?.let { putInt("studyId", it) }
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, RegisterStudyFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }
}
