package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCrewWrongQuizBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding
import com.example.spoteam_android.databinding.FragmentTimeOutQuizBinding


class CrewTimeOutFragment : Fragment() {

    private lateinit var binding: FragmentTimeOutQuizBinding
    private var wrongCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTimeOutQuizBinding.inflate(inflater, container, false)
        
        return binding.root
    }

}