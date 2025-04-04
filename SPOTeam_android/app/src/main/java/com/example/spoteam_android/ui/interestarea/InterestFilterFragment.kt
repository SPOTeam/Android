package com.example.spoteam_android.ui.interestarea


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class InterestFilterFragment : Fragment() {

    lateinit var binding: FragmentInterestFilterBinding
    private val viewModel: InterestFilterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterestFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecruitingChips()
        setupGenderChips()
        setupAgeRangeSlider()
        setupActivityFeeChips()
        setupStudyThemeChips()
        setupSearchButton()

        restoreRecruitingChip()
        restoreGenderChip()
        restoreActivityFeeChip()
        restoreStudyThemeChip()
    }

    private fun setupToolbar() {
        binding.toolbar.icBack.setOnClickListener {
            viewModel.reset() // 1. ViewModel 값 초기화
            val bundle = Bundle().apply {
                putString("source", "InterestFilterFragment")
            }
            val interestFragment = InterestFragment().apply {
                arguments = bundle
            }
            (activity as MainActivity).switchFragment(interestFragment) // 2. 초기화된 인스턴스로 이동
        }
    }

    private fun setupGenderChips() {
        binding.chipGroupGender.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender = when (checkedId) {
                R.id.chip1_gender -> "UNKNOWN"
                R.id.chip2_gender -> "MALE"
                R.id.chip3_gender -> "FEMALE"
                else -> null
            }
            updateNextButtonState()
        }
    }

    private fun setupAgeRangeSlider() {
        val ageRangeSlider = binding.ageRangeSlider
        val minValueText = binding.minValueText
        val maxValueText = binding.maxValueText

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 60f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val minAge = values[0].toInt()
            val maxAge = values[1].toInt()
            viewModel.minAge = minAge
            viewModel.maxAge = maxAge
            minValueText.text = minAge.toString()
            maxValueText.text = maxAge.toString()
        }
    }

    private fun setupActivityFeeChips() {
        val chipGroup1 = binding.chipGroup1
        val editText = binding.edittext1
        val behindEt = binding.behindEt

        chipGroup1.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = chipGroup1.findViewById<Chip>(checkedId)
                if (checkedChip.id == R.id.chip1) {
                    viewModel.activityFee = "있음"
                    editText.visibility = View.VISIBLE
                    behindEt.visibility = View.VISIBLE
                } else {
                    viewModel.activityFee = "없음"
                    editText.visibility = View.GONE
                    behindEt.visibility = View.GONE
                }
            } else {
                viewModel.activityFee = "없음"
                editText.visibility = View.GONE
                behindEt.visibility = View.GONE
            }
            updateNextButtonState()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(editText.windowToken, 0)
                editText.clearFocus()

                viewModel.activityFeeAmount = editText.text.toString()
                updateNextButtonState()
                true
            } else false
        }
    }

    private fun setupStudyThemeChips() {
        binding.chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val selectedText = selectedChip?.text.toString()
                viewModel.selectedStudyTheme = selectedText
                Log.d("InterestFilterFragment", "Selected study theme: $selectedText")
            }
            updateNextButtonState()
        }
    }

    private fun setupSearchButton() {
        binding.fragmentIntroduceStudyBt.setOnClickListener {
            // ViewModel 값 → Bundle
            val bundle = Bundle().apply {
                putString("source", "InterestFilterFragment")
            }

            val interestFragment = InterestFragment().apply {
                arguments = bundle
            }

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateNextButtonState() {
        val activityFee = viewModel.activityFee
        val activityFeeAmount = binding.edittext1.text.toString()

        val isActivityFeeNoneSelected = activityFee == "없음"
        val isActivityFeeEntered = activityFee == "있음" &&
                activityFeeAmount.isNotEmpty() &&
                activityFeeAmount.toIntOrNull() != null

        val isSecondConditionMet = isActivityFeeNoneSelected || isActivityFeeEntered
        val isThirdConditionMet = viewModel.selectedStudyTheme != null

        binding.fragmentIntroduceStudyBt.isEnabled = isSecondConditionMet && isThirdConditionMet
    }

    private fun restoreGenderChip() {
        when (viewModel.gender) {
            "UNKNOWN" -> binding.chipGroupGender.check(R.id.chip1_gender)
            "MALE" -> binding.chipGroupGender.check(R.id.chip2_gender)
            "FEMALE" -> binding.chipGroupGender.check(R.id.chip3_gender)
        }
    }
    private fun restoreActivityFeeChip() {
        when (viewModel.activityFee) {
            "있음" -> {
                binding.chipGroup1.check(R.id.chip1)
                binding.edittext1.setText(viewModel.activityFeeAmount)
                binding.edittext1.visibility = View.VISIBLE
                binding.behindEt.visibility = View.VISIBLE
            }
            "없음" -> {
                binding.chipGroup1.check(R.id.chip2)
                binding.edittext1.visibility = View.GONE
                binding.behindEt.visibility = View.GONE
            }
        }
    }

    private fun restoreStudyThemeChip() {
        val selectedText = viewModel.selectedStudyTheme ?: return
        val chipGroup = binding.chipGroup2

        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            if (chip?.text == selectedText) {
                chipGroup.check(chip.id)
                break
            }
        }
    }

    private fun setupRecruitingChips() {
        binding.chipGroupRecruiting.setOnCheckedChangeListener { group, checkedId ->
            viewModel.isRecruiting = when (checkedId) {
                R.id.chip1_recruiting -> "있음"
                R.id.chip2_recruiting -> "없음"
                else -> null
            }
        }
    }
    private fun restoreRecruitingChip() {
        when (viewModel.isRecruiting) {
            "있음" -> binding.chipGroupRecruiting.check(R.id.chip1_recruiting)
            "없음" -> binding.chipGroupRecruiting.check(R.id.chip2_recruiting)
        }
    }

}

