package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRegisterStudyBinding


class RegisterStudyFragment : Fragment() {

    lateinit var binding:FragmentRegisterStudyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterStudyBinding.inflate(inflater,container,false)
        return binding.root
    }

}