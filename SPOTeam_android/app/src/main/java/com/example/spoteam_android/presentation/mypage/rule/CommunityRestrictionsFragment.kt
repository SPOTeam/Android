package com.example.spoteam_android.presentation.mypage.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCommunityRestrictionBinding
import com.example.spoteam_android.databinding.FragmentCommunityRuleBinding
import com.example.spoteam_android.databinding.FragmentMypageBinding

class CommunityRestrictionsFragment : Fragment() {
    private lateinit var binding: FragmentCommunityRestrictionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityRestrictionBinding.inflate(inflater, container, false)


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}
