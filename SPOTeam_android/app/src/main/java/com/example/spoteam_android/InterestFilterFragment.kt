package com.example.spoteam_android


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentInterestFilterBinding
import com.example.spoteam_android.search.ApiResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        ageRangeSlider.values = listOf(18f, 25f)

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
        }

        val chipGroup2 = binding.chipGroup2

        chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val selectedChip = group.findViewById<Chip>(checkedId)
                val selectedChipText = selectedChip?.text.toString()
                bundle.putString("selectedStudyTheme", selectedChipText)
                Log.d("InterestFilterFragment", "Selected study theme: $selectedChipText")
            }
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
}
