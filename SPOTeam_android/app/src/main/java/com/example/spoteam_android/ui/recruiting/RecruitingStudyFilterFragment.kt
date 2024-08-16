package com.example.spoteam_android.ui.recruiting

import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRecruitingStudyFilterBinding
import com.example.spoteam_android.ui.myinterest.ChipViewModel
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFilterLocationFragment
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class RecruitingStudyFilterFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyFilterBinding

    private val myviewModel: ChipViewModel by activityViewModels()
    private val viewModel: StudyViewModel by activityViewModels()

    private var isLocationPlusVisible: Boolean = false
    private var selectedLocationCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitingStudyFilterBinding.inflate(inflater, container, false)

        selectedLocationCode = null
        viewModel.clearRegions()

        arguments?.let {
            // 전달된 주소와 코드가 있을 경우 ViewModel에 저장
            myviewModel.selectedAddress = it.getString("ADDRESS")
            myviewModel.selectedCode = it.getString("CODE")

            val address = myviewModel.selectedAddress
            val isOffline = it.getBoolean("IS_OFFLINE", false)

            address?.let { addr ->
                updateChip(addr)
            }

            setChipState(isOffline)
            setupChipCloseListener()
            isLocationPlusVisible = isOffline
        }

        myviewModel.selectedChipId?.let {
            binding.chipGroup1.check(it)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val spinner: Spinner = binding.genderSpinner

        val toolbar = binding.toolbar

        val bundle = Bundle()


        toolbar.icBack.setOnClickListener {
            (activity as MainActivity).switchFragment(RecruitingStudyFragment())
        }

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        myviewModel.selectedSpinnerPosition?.let {
            binding.genderSpinner.setSelection(it)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                myviewModel.selectedSpinnerPosition = position
                when (position) {
                    0 -> bundle.putString("gender", "MALE")
                    1 -> bundle.putString("gender", "MALE")
                    2 -> bundle.putString("gender", "FEMALE")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val ageRangeSlider = binding.ageRangeSlider
        val minValueText = binding.minValueText
        val maxValueText = binding.maxValueText

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 60f)
        myviewModel.ageRangeValues ?: listOf(18f, 60f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minValueText.text = values[0].toInt().toString()
            maxValueText.text = values[1].toInt().toString()
            myviewModel.ageRangeValues = values
        }

        myviewModel.ageRangeValues?.let {
            binding.ageRangeSlider.values = it
            binding.minValueText.text = it[0].toInt().toString()
            binding.maxValueText.text = it[1].toInt().toString()
        }


        val chipGroup1 = binding.chipGroup1
        val editText = binding.edittext1
        val behind_et = binding.behindEt
        val chipGroup_new = binding.chipGroupNew
        val btn_add_area = binding.btnAddArea
        val chipGroup2 = binding.chipGroup2

        chipGroup_new.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = group.findViewById<Chip>(checkedId)
                if (checkedChip.id == R.id.chip01) {
                    binding.btnAddArea.visibility = View.GONE
                    binding.locationChip.visibility = View.GONE
                    bundle.putString("activityFee", "온라인") // 활동비 유무
                } else {
                    binding.btnAddArea.visibility = View.VISIBLE
                    bundle.putString("activityFee", "오프라인") // 활동비 유무
                }
            } else {
                bundle.putString("activityFee", "오프라인") // 활동비 유무
            }
            updateNextButtonState()  // 상태 업데이트 호출
        }

        editText.setOnClickListener{
            updateNextButtonState()
        }

        chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            updateNextButtonState()
        }


        btn_add_area.setOnClickListener{
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RecruitingStudyFilterLocationFragment())
                .addToBackStack(null)
                .commit()
        }




        chipGroup1.setOnCheckedChangeListener { group, checkedId ->
            myviewModel.selectedChipId = checkedId
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



        val interestFragment = MyInterestStudyFragment()
        interestFragment.arguments = bundle

        val searchbtn = binding.fragmentIntroduceStudyBt
        searchbtn.setOnClickListener {
            bundle.putString("source", "RecruitingStudyFilterFragment")

            bundle.putString("minAge", minValueText.text.toString()) // 최소 나이
            bundle.putString("maxAge", maxValueText.text.toString()) // 최대 나이

            val activityFeeAmount = editText.text.toString()
            bundle.putString("activityFeeAmount", activityFeeAmount)

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RecruitingStudyFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            binding.btnAddArea.visibility = View.VISIBLE
        }
    }

    private fun updateNextButtonState() {
        val isOnlineSelected = binding.chipGroupNew.checkedChipId == R.id.chip01
        val isOfflineSelectedWithLocation =
            binding.chipGroupNew.checkedChipId == R.id.chip02 && binding.locationChip.visibility == View.VISIBLE

        val isActivityFeeNoneSelected = binding.chipGroup1.checkedChipId == R.id.chip2

        // EditText의 값이 비어있지 않고, 숫자만 포함하는지 확인
        val activityFeeText = binding.edittext1.text.toString()
        val isActivityFeeEntered = binding.chipGroup1.checkedChipId == R.id.chip1 && activityFeeText.isNotEmpty() && activityFeeText.toIntOrNull() != null

        // 첫 번째 조건: 온라인이 선택되었거나, 오프라인이 선택되고 위치가 설정된 경우
        val isFirstConditionMet = isOnlineSelected || isOfflineSelectedWithLocation

        // 두 번째 조건: 활동비 없음이 선택되었거나, 활동비 있음이 선택되고 숫자가 입력된 경우
        val isSecondConditionMet = isActivityFeeNoneSelected || isActivityFeeEntered

        val isThirdConditionMet = binding.chipGroup2.checkedChipId != ChipGroup.NO_ID

        // 두 조건 모두 만족해야 버튼이 활성화됨
        binding.fragmentIntroduceStudyBt.isEnabled = isFirstConditionMet && isSecondConditionMet && isThirdConditionMet
    }



    private fun setChipState(isOffline: Boolean) {
        if (isOffline) {
            binding.chipGroupNew.check(R.id.chip02)
        } else {
            binding.chipGroupNew.check(R.id.chip01)
        }
    }

    private fun extractAddressUntilDong(address: String): String {
        val index = address.indexOf("동")
        return if (index != -1) {
            address.substring(0, index + 1)
        } else {
            address
        }
    }

    fun updateChip(address: String) {
        val truncatedAddress = extractAddressUntilDong(address)
        binding.locationChip.apply {
            visibility = View.VISIBLE
            text = truncatedAddress
        }
        updateNextButtonState()
    }
}