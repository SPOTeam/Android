package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.databinding.FragmentMystudyCommunityBinding


class MyStudyCommunityFragment : Fragment() {

    private lateinit var binding: FragmentMystudyCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMystudyCommunityBinding.inflate(inflater,container,false)

        return binding.root
    }


}