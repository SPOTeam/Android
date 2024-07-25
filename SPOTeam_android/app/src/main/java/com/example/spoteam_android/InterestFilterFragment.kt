package com.example.spoteam_android


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentInterestFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.RangeSlider


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

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

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
                    editText.visibility = View.VISIBLE
                    behind_et.visibility = View.VISIBLE
                } else {
                    editText.visibility = View.GONE
                    behind_et.visibility = View.GONE
                }
            } else {
                editText.visibility = View.GONE
                behind_et.visibility = View.GONE
            }
        }

        val chipGroup2 = binding.chipGroup2

        chipGroup2.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = group.findViewById<Chip>(checkedId)
                checkedChip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
//                        checkedChip.setChipBackgroundColorResource(R.color.active_blue)
//                        checkedChip.setTextColor(getResources().getColor(R.color.white, null))
                    } else {
//                        checkedChip.setChipBackgroundColorResource(android.R.color.transparent)
//                        checkedChip.setTextColor(getResources().getColor(R.color.active_blue, null))
                    }
                }
            }
        }



    }

}

