package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCrewWrongQuizBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding


class CrewWrongQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewWrongQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewWrongQuizBinding.inflate(inflater, container, false)


        return binding.root
    }

}