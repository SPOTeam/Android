package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCrewTakeAttendanceBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding


class CrewTakeAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentCrewTakeAttendanceBinding
    private lateinit var countDownTimer: CountDownTimer
    private val timeInMillis = 300000L // 5분 (5 * 60 * 1000 milliseconds)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewTakeAttendanceBinding.inflate(inflater, container, false)

        return binding.root
    }
}