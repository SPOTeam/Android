package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMemberStudyBinding

class MemberStudyFragment : Fragment() {
    private lateinit var binding: FragmentMemberStudyBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMemberStudyBinding.inflate(inflater,container,false)

        binding.fragmentMemberStudyBt.setOnClickListener {
            goToNextFragment()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val genderspinner: Spinner = binding.fragmentMemberStudyGenderSpinner

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        genderspinner.adapter = adapter

        val numSpinner: Spinner = binding.fragmentMemberStudyNumSpinner
        val numAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.fragment_study_num_spinner_array,
            R.layout.spinner_item
        )
        numAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        numSpinner.adapter = numAdapter

        val ageRangeSlider = binding.fragmentMemberStudyAgeAgeRangeSlider
        val minValueText = binding.fragmentMemberStudyAgeMinValueTv
        val maxValueText = binding.fragmentMemberStudyAgeMaxValueTv

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 25f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minValueText.text = values[0].toInt().toString()
            maxValueText.text = values[1].toInt().toString()
        }
    }
    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, ActivityFeeStudyFragment()) // 변경할 Fragment로 교체
        transaction.commit()
    }


}


