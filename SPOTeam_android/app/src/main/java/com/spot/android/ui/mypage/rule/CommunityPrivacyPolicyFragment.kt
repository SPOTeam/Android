package com.spot.android.ui.mypage.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spot.android.databinding.FragmentCommunityPrivacyBinding

class CommunityPrivacyPolicyFragment : Fragment() {
    private lateinit var binding: FragmentCommunityPrivacyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityPrivacyBinding.inflate(inflater, container, false)


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}
