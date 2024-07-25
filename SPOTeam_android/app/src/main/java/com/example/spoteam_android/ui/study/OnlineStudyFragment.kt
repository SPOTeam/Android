package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentOnlineStudyBinding

class OnlineStudyFragment : Fragment() {
    private lateinit var binding: FragmentOnlineStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineStudyBinding.inflate(inflater, container, false)

        setupChipGroupListener()
        binding.fragmentOnlineStudyLocationPlusBt.setOnClickListener {
            goToNextFragment()
        }

        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_online_study_chip_online -> {
                    binding.fragmentOnlineStudyLocationPlusCl.visibility = View.GONE
                }
                R.id.fragment_online_study_chip_offline -> {
                    binding.fragmentOnlineStudyLocationPlusCl.visibility = View.VISIBLE
                }
                else -> {
                    binding.fragmentOnlineStudyLocationPlusCl.visibility = View.GONE
                }
            }
        }
    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, LocationStudyFragment()) // 변경할 Fragment로 교체
        transaction.commit()
    }
}