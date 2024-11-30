package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCrewTakeAttendanceBinding
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding


class CrewTakeAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentCrewTakeAttendanceBinding
    private var scheduleId = -1;
    private var question = "";
    private lateinit var countDownTimer: CountDownTimer
    private val timeInMillis = 300000L // 5분 (5 * 60 * 1000 milliseconds)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewTakeAttendanceBinding.inflate(inflater, container, false)

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
            question = it.getString("question").toString()
            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }

        binding.startAttendanceTv.setOnClickListener {
            val crewSolveQuizFragment = CrewSolveQuizFragment().apply {
                arguments = Bundle().apply {
                    putInt("scheduleId", scheduleId) // scheduleId 전달
                    putString("question", question) // question 전달
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, crewSolveQuizFragment)
                .commitAllowingStateLoss()
        }
        return binding.root
    }
}