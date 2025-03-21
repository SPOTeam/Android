package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCrewWrongQuizBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding
import com.example.spoteam_android.databinding.FragmentHostTakeAttendanceBinding


class HostTakeAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentHostTakeAttendanceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostTakeAttendanceBinding.inflate(inflater, container, false)

        return binding.root
    }

}