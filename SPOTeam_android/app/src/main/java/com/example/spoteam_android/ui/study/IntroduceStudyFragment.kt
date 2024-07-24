package com.example.spoteam_android.ui.study

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
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentIntroduceStudyBinding

class IntroduceStudyFragment : Fragment() {

    private lateinit var binding: FragmentIntroduceStudyBinding
    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroduceStudyBinding.inflate(inflater, container, false)

        // 초기화 함수 호출
        initActivityResultLaunchers()
        initAddImageButton()
        setupTextWatchers()
        checkButtonState()

        binding.fragmentIntroduceStudyBt.setOnClickListener {
            goToNextFragment()
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

        val isImageSelected = binding.fragmentIntroduceStudyIv.drawable != null

        binding.fragmentIntroduceStudyBt.isEnabled = isEditTextFilled && isImageSelected
    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, HouseFragment()) // 변경할 Fragment로 교체
        transaction.commit()
    }
}
