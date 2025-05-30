package com.example.spoteam_android.ui.mypage.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCommunityPrivacyBinding
import com.example.spoteam_android.databinding.FragmentCommunityRuleBinding
import com.example.spoteam_android.databinding.FragmentCommunityTermsBinding
import com.example.spoteam_android.databinding.FragmentMypageBinding

class CommunityTermsOfUseFragment : Fragment() {
    private lateinit var binding: FragmentCommunityTermsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityTermsBinding.inflate(inflater, container, false)


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}
