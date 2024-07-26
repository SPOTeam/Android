package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentActivityFeeStudyBinding


class ActivityFeeStudyFragment : Fragment() {
   private lateinit var binding:FragmentActivityFeeStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityFeeStudyBinding.inflate(inflater,container,false)
        setupChipGroupListener()
        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_activity_fee_study_chip_false -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                }
                R.id.fragment_activity_fee_study_chip_true -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE

                }
            }
        }
    }
}