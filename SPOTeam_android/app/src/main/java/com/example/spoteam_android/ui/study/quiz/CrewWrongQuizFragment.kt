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


class CrewWrongQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewWrongQuizBinding
    private var wrongCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewWrongQuizBinding.inflate(inflater, container, false)

        arguments?.let {
            wrongCount = it.getInt("tryNum")
        }
        binding.wrongCountTv.text = wrongCount.toString()

        binding.retryTv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}