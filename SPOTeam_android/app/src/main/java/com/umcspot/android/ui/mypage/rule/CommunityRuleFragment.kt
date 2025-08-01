package com.umcspot.android.ui.mypage.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umcspot.android.databinding.FragmentCommunityRuleBinding

class CommunityRuleFragment : Fragment() {
    private lateinit var binding: FragmentCommunityRuleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityRuleBinding.inflate(inflater, container, false)


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}
