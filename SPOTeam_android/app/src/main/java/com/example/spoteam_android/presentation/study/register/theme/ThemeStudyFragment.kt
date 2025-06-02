package com.example.spoteam_android.presentation.study.register.theme

import StudyFormMode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRegisterStudyBinding
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel
import com.example.spoteam_android.presentation.study.register.introduce.IntroduceStudyFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ThemeStudyFragment : Fragment() {

    private lateinit var binding: FragmentRegisterStudyBinding

    private var currentStep = 1

    private val studyViewModel: StudyViewModel by activityViewModels()

    private val registerViewModel: StudyRegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setChipGroup()
        observeThemes()
        observeStudyViewModel()
        setupListeners()
    }

    private fun initArguments() {
        val mode = arguments?.getSerializable("mode", StudyFormMode::class.java)
            ?: StudyFormMode.CREATE
        val studyId = arguments?.getInt("studyId", -1) ?: -1

        registerViewModel.setMode(mode)

        if (mode == StudyFormMode.CREATE) {
            registerViewModel.reset()
        } else if (mode == StudyFormMode.EDIT && studyId != -1) {
            registerViewModel.setStudyId(studyId)
            studyViewModel.setStudyId(studyId)
            studyViewModel.fetchStudyDetail(studyId)
        }
    }


    private fun initUI() {
        if (registerViewModel.mode.value == StudyFormMode.EDIT) {
            binding.fragmentRegisterStudyTv.text = "스터디 정보 수정"
        }
        binding.fragmentRegisterStudyBt.isEnabled = false
    }

    private fun setChipGroup() {
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            updateSelectedThemes()
        }
        for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
            val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? CompoundButton
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

    private fun setupListeners() {
        binding.fragmentRegisterStudyBt.setOnClickListener {
            updateProgressBar()
            goToNextFragment()
        }
        binding.fragmentRegisterStudyBackBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateSelectedThemes() {
        val selectedThemes = mutableListOf<String>()
        for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
            val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? CompoundButton
            if (chip?.isChecked == true) {
                val chipText = chip.text.toString()
                val processedText = when {
                    chipText.contains("전공") -> chipText.replace("/", "및")
                    else -> chipText.replace("/", "").replace(" ", "")
                }
                selectedThemes.add(processedText)
            }
        }
        binding.fragmentRegisterStudyBt.isEnabled = selectedThemes.isNotEmpty()
        registerViewModel.updateThemes(selectedThemes)
    }

    private fun observeThemes() {
        lifecycleScope.launch {
            registerViewModel.studyRequest.collect { request ->
                val themes = request?.themes ?: return@collect
                for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
                    val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? Chip
                    chip?.let {
                        val normalizedChipText = normalizeText(it.text.toString())
                        val normalizedServerTextList = themes.map { theme ->
                            normalizeServerText(theme)
                        }
                        it.isChecked = normalizedServerTextList.contains(normalizedChipText)
                    }
                }
                binding.fragmentRegisterStudyBt.isEnabled = themes.isNotEmpty()
            }
        }
    }

    private fun observeStudyViewModel() {
        studyViewModel.studyRequest.observe(viewLifecycleOwner) { dto ->
            dto?.let {
                val loadedId = studyViewModel.studyId.value ?: -1
                registerViewModel.setStudyId(loadedId)
                registerViewModel.setStudyRequest(it)
            }
        }
    }

    private fun updateProgressBar() {
        currentStep++
        val progress = (currentStep / 5.0 * 100).toInt()
        binding.fragmentRegisterStudyPb.progress = progress
    }

    private fun goToNextFragment() {
        val nextFragment = IntroduceStudyFragment().apply {
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

    private fun normalizeText(text: String): String {
        return text.replace(" ", "").replace("및", "/")
    }

    private fun normalizeServerText(text: String): String {
        return text
            .replace("전공및진로학습", "전공/진로학습")
            .replace("시사뉴스", "시사/뉴스")
            .replace("자율학습", "자율 학습")
            .replace(" ", "")
    }
}
