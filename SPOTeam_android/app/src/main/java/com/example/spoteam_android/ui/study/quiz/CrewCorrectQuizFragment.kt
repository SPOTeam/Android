package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCrewCorrectQuizBinding
import com.example.spoteam_android.databinding.FragmentCrewSolveQuizBinding
import com.example.spoteam_android.databinding.FragmentCrewWrongQuizBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding


class CrewCorrectQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewCorrectQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewCorrectQuizBinding.inflate(inflater, container, false)


        return binding.root
    }

}