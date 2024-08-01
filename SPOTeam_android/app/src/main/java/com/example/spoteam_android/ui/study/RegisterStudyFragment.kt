package com.example.spoteam_android.ui.study

import StudyViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRegisterStudyBinding

class RegisterStudyFragment : Fragment() {

    private lateinit var binding: FragmentRegisterStudyBinding
    private var currentStep = 1 // 현재 단계
    private val viewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterStudyBinding.inflate(inflater, container, false)

        // ChipGroup 설정 메소드 호출
        setChipGroup()

        // '다음' 버튼 클릭 리스너 설정
        binding.fragmentRegisterStudyBt.setOnClickListener {
            updateProgressBar()
            goToNextFragment()
        }

        return binding.root
    }

    private fun setChipGroup() {
        // 초기 버튼 비활성화
        binding.fragmentRegisterStudyBt.isEnabled = false

        // Chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val isAnyChipChecked =
                binding.fragmentRegisterStudyChipgroup.checkedChipIds.isNotEmpty()
            binding.fragmentRegisterStudyBt.isEnabled = isAnyChipChecked

            updateSelectedThemes()
        }

        for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
            val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? CompoundButton
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

    private fun updateSelectedThemes() {
        val selectedThemes = mutableListOf<String>()
        for (i in 0 until binding.fragmentRegisterStudyChipgroup.childCount) {
            val chip = binding.fragmentRegisterStudyChipgroup.getChildAt(i) as? CompoundButton
            if (chip?.isChecked == true) {
                selectedThemes.add(chip.text.toString())
            }
        }
        viewModel.updateThemes(selectedThemes)
    }

    private fun updateProgressBar() {
        currentStep++
        val progress = (currentStep / 5.0 * 100).toInt() // 총 5단계 가정
        binding.fragmentRegisterStudyPb.progress = progress
    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, IntroduceStudyFragment())
        transaction.commit()
    }
}
