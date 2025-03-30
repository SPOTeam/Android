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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class CrewTakeAttendanceFragment : Fragment() {

    private lateinit var binding: FragmentCrewTakeAttendanceBinding
    private var scheduleId = -1;
    private var question = "";

    private lateinit var countDownTimer: CountDownTimer
    private var createdAt : String = ""
    private var timeInMillis: Long = 0L
    private val fiveMinutesInMillis = 5 * 60 * 1000L // 5 minutes in milliseconds
    private var remainingTime: Long = 0L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewTakeAttendanceBinding.inflate(inflater, container, false)

        arguments?.let {
            createdAt = it.getString("createdAt").toString()
            scheduleId = it.getInt("scheduleId")
            question = it.getString("question").toString()
//            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }

        if(question != "") {
            binding.startAttendanceTv.isEnabled = true;
        }

        // parentFragment 접근 후 수정
        parentFragment?.let { parent ->
            if (parent is CheckAttendanceFragment) { // YourParentFragmentInterface는 수정하려는 메서드를 포함
                parent.changeDescription()
            }
        }

        initTimer()

        binding.startAttendanceTv.setOnClickListener {
            val crewSolveQuizFragment = CrewSolveQuizFragment().apply {
                arguments = Bundle().apply {
                    putInt("scheduleId", scheduleId) // scheduleId 전달
                    putString("question", question) // question 전달
                    putLong("remainingTimeMillis", remainingTime) // 남은 시간 millis 단위로 전달
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, crewSolveQuizFragment)
                .commitAllowingStateLoss()
        }
        return binding.root
    }

    private fun initTimer() {
        binding.timerTv.visibility = View.VISIBLE
        timeInMillis = calculateTimeLeftInMillis(createdAt)
        startTimer()
    }


    private fun calculateTimeLeftInMillis(createdAt: String): Long {
        return try {
            // Parse createdAt to UTC time
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val createdTime = sdf.parse(createdAt)?.time ?: 0L

            // Get current time in UTC
            val currentTime = System.currentTimeMillis()

            // Calculate the time left (5 minutes - elapsed time)
            val elapsedTime = currentTime - createdTime
            val timeLeft = fiveMinutesInMillis - elapsedTime

            if (timeLeft > 0) timeLeft else 0L
        } catch (e: Exception) {
            Log.e("HostFinishMakeQuizFragment", "Error parsing createdAt: $e")
            0L
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.timerTv.text = String.format("%02d : %02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTv.text = "00 : 00"
            }
        }.start()
    }

}