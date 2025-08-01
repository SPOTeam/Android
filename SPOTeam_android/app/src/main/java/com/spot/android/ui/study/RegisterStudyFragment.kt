package com.spot.android.ui.study

import StudyFormMode
import StudyViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spot.android.R
import com.spot.android.databinding.FragmentRegisterStudyBinding
import com.google.android.material.chip.Chip

class RegisterStudyFragment : Fragment() {

    private lateinit var binding: FragmentRegisterStudyBinding
    private var currentStep = 1 // 현재 단계
    private val viewModel: StudyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initArguments()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setChipGroup()
        observeThemes()
        setupListeners()
    }
    private fun initArguments() {
        val mode = arguments?.getSerializable("mode", StudyFormMode::class.java) ?: StudyFormMode.CREATE
        val studyId = arguments?.getInt("studyId", -1) ?: -1
        viewModel.setMode(mode)

        if (mode == StudyFormMode.CREATE) viewModel.reset()
        else if (mode == StudyFormMode.EDIT && studyId != -1) viewModel.fetchStudyDetail(studyId)
    }
    private fun initUI() {
        if (viewModel.mode.value == StudyFormMode.EDIT) {
            binding.fragmentRegisterStudyTv.text = "스터디 정보 수정"
        }
        binding.fragmentRegisterStudyBt.isEnabled = false
    }

    private fun setChipGroup() {
        // Chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            updateSelectedThemes()
        }

        // ChipGroup 내 모든 Chip에 리스너 추가
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
                    chipText.contains("전공") -> chipText.replace("/", "및") // "전공/진로학습" → "전공 및 진로학습"
                    else -> chipText.replace("/", "").replace(" ", "") // 공백 및 슬래시 제거
                }
                selectedThemes.add(processedText)
            }
        }

        // 버튼 활성화 여부 업데이트
        binding.fragmentRegisterStudyBt.isEnabled = selectedThemes.isNotEmpty()

        // ViewModel에 선택된 테마 업데이트
        viewModel.updateThemes(selectedThemes)
    }

    private fun observeThemes() {
        viewModel.themes.observe(viewLifecycleOwner) { themes ->
            for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
                val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? Chip
                chip?.let {
                    val normalizedChipText = normalizeText(it.text.toString())
                    val normalizedServerTextList = themes.map { theme -> normalizeServerText(theme) }

                    it.isChecked = normalizedServerTextList.contains(normalizedChipText)
                }
            }

            // 수정 모드에서 테마가 미리 선택되어 있는 경우, 버튼도 활성화
            binding.fragmentRegisterStudyBt.isEnabled = themes.isNotEmpty()
        }
    }



    private fun updateProgressBar() {
        currentStep++
        val progress = (currentStep / 5.0 * 100).toInt() // 총 5단계 가정
        binding.fragmentRegisterStudyPb.progress = progress
    }

    private fun goToNextFragment() {
        val nextFragment = IntroduceStudyFragment().apply {
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
