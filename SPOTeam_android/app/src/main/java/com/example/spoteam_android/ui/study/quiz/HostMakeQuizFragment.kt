package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding
import com.example.spoteam_android.databinding.FragmentHostMakeQuizBinding


class HostMakeQuizFragment : Fragment() {

    private lateinit var binding: FragmentHostMakeQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostMakeQuizBinding.inflate(inflater, container, false)



        return binding.root
    }

}