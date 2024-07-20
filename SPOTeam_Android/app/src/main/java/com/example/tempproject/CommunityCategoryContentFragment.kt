package com.example.tempproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tempproject.databinding.FragmentCommunityCategoryContentBinding

class CommunityCategoryContentFragment: Fragment()  {

    lateinit var binding: FragmentCommunityCategoryContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryContentBinding.inflate(inflater, container, false)

        return binding.root
    }
}