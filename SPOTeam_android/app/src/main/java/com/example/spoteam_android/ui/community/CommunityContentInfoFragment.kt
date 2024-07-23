package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCommunityContentBinding

class CommunityContentInfoFragment : Fragment() {

    lateinit var binding: FragmentCommunityContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityContentBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener{
            requireActivity().finish()
        }

        return binding.root
    }
}
