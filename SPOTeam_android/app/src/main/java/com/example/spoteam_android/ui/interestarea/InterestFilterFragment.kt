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
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class InterestFilterFragment : Fragment() {

    lateinit var binding: FragmentInterestFilterBinding

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
        val spinner: Spinner = binding.genderSpinner

        val toolbar = binding.toolbar

        val bundle = Bundle()

        toolbar.icBack.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFragment())
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> bundle.putString("gender", "MALE")
                    1 -> bundle.putString("gender", "MALE")
                    2 -> bundle.putString("gender", "FEMALE")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("InterestFilterFragment", "아무것도 선택되지 않았습니다.")
            }
        }

        val ageRangeSlider = binding.ageRangeSlider
        val minValueText = binding.minValueText
        val maxValueText = binding.maxValueText

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 60f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minValueText.text = values[0].toInt().toString()
            maxValueText.text = values[1].toInt().toString()
        }

        val chipGroup1 = binding.chipGroup1
        val editText = binding.edittext1
        val behind_et = binding.behindEt

        chipGroup1.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = group.findViewById<Chip>(checkedId)
                if (checkedChip.id == R.id.chip1) {
                    bundle.putString("activityFee", "있음") // 활동비 유무
                    editText.visibility = View.VISIBLE
                    behind_et.visibility = View.VISIBLE
                } else {
                    bundle.putString("activityFee", "없음") // 활동비 유무
                    editText.visibility = View.GONE
                    behind_et.visibility = View.GONE
                }
            } else {
                bundle.putString("activityFee", "없음") // 활동비 유무
                editText.visibility = View.GONE
                behind_et.visibility = View.GONE
            }
            updateNextButtonState()
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
                editText.clearFocus()  // 포커스 해제
                true
            } else {
                false
            }
        }

        val chipGroup2 = binding.chipGroup2

        chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val selectedChipText = selectedChip?.text.toString()
                bundle.putString("selectedStudyTheme", selectedChipText)
                Log.d("InterestFilterFragment", "Selected study theme: $selectedChipText")
            }
            updateNextButtonState()
        }

        val interestFragment = InterestFragment()
        interestFragment.arguments = bundle

        val searchbtn = binding.fragmentIntroduceStudyBt
        searchbtn.setOnClickListener {
            bundle.putString("source", "InterestFilterFragment")

            bundle.putString("minAge", minValueText.text.toString()) // 최소 나이
            bundle.putString("maxAge", maxValueText.text.toString()) // 최대 나이

            val activityFeeAmount = editText.text.toString()
            bundle.putString("activityFeeAmount", activityFeeAmount)

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateNextButtonState() {

        val isActivityFeeNoneSelected = binding.chipGroup1.checkedChipId == R.id.chip2

        // EditText의 값이 비어있지 않고, 숫자만 포함하는지 확인
        val activityFeeText = binding.edittext1.text.toString()
        val isActivityFeeEntered = binding.chipGroup1.checkedChipId == R.id.chip1 && activityFeeText.isNotEmpty() && activityFeeText.toIntOrNull() != null

        // 첫 번째 조건: 온라인이 선택되었거나, 오프라인이 선택되고 위치가 설정된 경우
        // 두 번째 조건: 활동비 없음이 선택되었거나, 활동비 있음이 선택되고 숫자가 입력된 경우
        val isSecondConditionMet = isActivityFeeNoneSelected || isActivityFeeEntered

        val isThirdConditionMet = binding.chipGroup2.checkedChipId != ChipGroup.NO_ID

        // 두 조건 모두 만족해야 버튼이 활성화됨
        binding.fragmentIntroduceStudyBt.isEnabled = isSecondConditionMet && isThirdConditionMet
    }
}
