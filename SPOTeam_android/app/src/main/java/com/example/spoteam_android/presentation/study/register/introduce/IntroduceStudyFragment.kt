package com.example.spoteam_android.presentation.study.register.introduce

import StudyFormMode
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentIntroduceStudyBinding
import com.example.spoteam_android.presentation.study.register.online.OnlineStudyFragment
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel
import kotlinx.coroutines.launch

class IntroduceStudyFragment : Fragment() {

    private lateinit var binding: FragmentIntroduceStudyBinding

    private val registerViewModel: StudyRegisterViewModel by activityViewModels()

    private lateinit var getImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var profileImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt("studyId", -1)?.let {
            if (it != -1) {
                registerViewModel.setStudyId(it)
                Log.d("IntroduceStudyFragment", "Received studyId: $it")
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroduceStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initActivityResultLaunchers()
        initAddImageButton()
        setupTextWatchers()
        observeRegisterViewModel()
        setupListeners()
        checkButtonState()
    }

    private fun observeRegisterViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.studyRequest.collect { request ->
                    if (request != null && registerViewModel.mode.value == StudyFormMode.EDIT) {

                        binding.fragmentIntroduceStudyTv.text = "스터디 정보 수정"
                        binding.fragmentIntroduceStudynameEt.setText(request.title)
                        binding.fragmentIntroduceStudypurposeEt.setText(request.goal)
                        binding.fragmentIntroduceStudyEt.setText(request.introduction)


                        val imageUrl = registerViewModel.uploadedImageUrl.value ?: request.profileImage
                        if (!imageUrl.isNullOrBlank()) {
                            Glide.with(this@IntroduceStudyFragment)
                                .load(imageUrl)
                                .into(binding.fragmentIntroduceStudyIv)
                            profileImage = imageUrl
                        }
                        checkButtonState()
                    }
                }
            }
        }
    }

    private fun initActivityResultLaunchers() {
        getImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageURI: Uri? = result.data?.data
                    if (selectedImageURI != null) {
                        binding.fragmentIntroduceStudyIv.setImageURI(selectedImageURI)
                        profileImage = selectedImageURI.toString()
                        // 여기도 반드시 registerViewModel에 저장
                        registerViewModel.updateLocalImageUri(profileImage!!)
                        checkButtonState()
                    } else {
                        Toast.makeText(context, "이미지를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "이미지를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
                }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getImageFromAlbum()
                } else {
                    Toast.makeText(context, "권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initAddImageButton() {
        binding.fragmentIntroduceStudyIv.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        getImageFromAlbum()
                    }
                    else -> {
                        requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }
            } else {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        getImageFromAlbum()
                    }
                    else -> {
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
        binding.fragmentIntroduceStudynameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val visible = s.isNullOrBlank()
                binding.hintLeftStudyName.visibility = if (visible) View.VISIBLE else View.GONE
                binding.hintRightStudyName.visibility = if (visible) View.VISIBLE else View.GONE
                checkButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fragmentIntroduceStudypurposeEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val visible = s.isNullOrBlank()
                binding.hintLeftStudyGoal.visibility = if (visible) View.VISIBLE else View.GONE
                binding.hintRightStudyGoal.visibility = if (visible) View.VISIBLE else View.GONE
                checkButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.fragmentIntroduceStudyEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkButtonState()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun checkButtonState() {
        val isEditTextFilled = binding.fragmentIntroduceStudynameEt.text.isNotEmpty() &&
                binding.fragmentIntroduceStudypurposeEt.text.isNotEmpty() &&
                binding.fragmentIntroduceStudyEt.text.isNotEmpty()
        val isImageSelected = profileImage != null
        binding.fragmentIntroduceStudyBt.isEnabled = isEditTextFilled && isImageSelected
    }

    private fun saveStudyData() {
        val title = binding.fragmentIntroduceStudynameEt.text.toString()
        val goal = binding.fragmentIntroduceStudypurposeEt.text.toString()
        val introduction = binding.fragmentIntroduceStudyEt.text.toString()

        registerViewModel.updateTitleGoalIntro(title, goal, introduction)
    }

    private fun setupListeners() {
        binding.fragmentIntroduceStudyBt.setOnClickListener {
            if (profileImage == null) {
                Toast.makeText(context, "프로필 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveStudyData()
            goToNextFragment()
        }
        binding.fragmentIntroduceStudyBackBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun goToNextFragment() {
        val nextFragment = OnlineStudyFragment().apply {
            arguments = Bundle().apply {
                putSerializable("mode", registerViewModel.mode.value)
                registerViewModel.studyId.value?.let { putInt("studyId", it) }
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, nextFragment)
            .addToBackStack(null)
            .commit()
    }
}
