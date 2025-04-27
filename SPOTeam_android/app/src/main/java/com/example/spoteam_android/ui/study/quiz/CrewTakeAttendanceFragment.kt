package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCrewTakeAttendanceBinding

class CrewTakeAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentCrewTakeAttendanceBinding
    private var scheduleId = -1
    private val timerViewModel: TimerViewModel by activityViewModels()

    private val totalTimeMillis = 5 * 60 * 1000L // 5 minutes

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewTakeAttendanceBinding.inflate(inflater, container, false)

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
        }

        // parentFragment 접근해서 설명 수정
        (parentFragment as? CheckAttendanceFragment)?.changeDescription()

        initTimerObserve()

        binding.startAttendanceTv.setOnClickListener {
            val crewSolveQuizFragment = CrewSolveQuizFragment().apply {
                arguments = Bundle().apply {
                    putInt("scheduleId", scheduleId)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, crewSolveQuizFragment)
                .commitAllowingStateLoss()
        }
        return binding.root
    }

    private fun initTimerObserve() {
        binding.timerTv.visibility = View.VISIBLE

        timerViewModel.timerSeconds.observe(viewLifecycleOwner) { elapsedSeconds ->
            val elapsedMillis = elapsedSeconds * 1000
            val remainingMillis = totalTimeMillis - elapsedMillis

            Log.d("TimerDebug", "elapsedSeconds: $elapsedSeconds, remainingMillis: $remainingMillis")

            if (remainingMillis > 0) {
                val minutes = (remainingMillis / 1000) / 60
                val seconds = (remainingMillis / 1000) % 60
                binding.timerTv.text = String.format("%02d : %02d", minutes, seconds)
                binding.startAttendanceTv.isEnabled = true
            } else {
                binding.timerTv.text = "00 : 00"
                binding.startAttendanceTv.isEnabled = false
            }
        }
    }


    private fun calculateRemainingMillis(): Long {
        val lastElapsedSeconds = timerViewModel.timerSeconds.value ?: 0L
        val remainingMillis = totalTimeMillis - (lastElapsedSeconds * 1000)
        return if (remainingMillis > 0) remainingMillis else 0L
    }
}
