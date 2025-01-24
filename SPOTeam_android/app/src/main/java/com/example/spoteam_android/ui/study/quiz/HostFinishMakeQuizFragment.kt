package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentHostFinishMakeQuizBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class HostFinishMakeQuizFragment : Fragment() {

    private lateinit var binding: FragmentHostFinishMakeQuizBinding
    private lateinit var countDownTimer: CountDownTimer
    private var createdAt: String = ""
    private var timeInMillis: Long = 0L
    private val fiveMinutesInMillis = 5 * 60 * 1000L // 5 minutes in milliseconds

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostFinishMakeQuizBinding.inflate(inflater, container, false)

        arguments?.let {
            createdAt = it.getString("createdAt").toString()
            Log.d("HostFinishMakeQuizFragment", "Received createdAt: $createdAt")

            // Calculate time difference
            timeInMillis = calculateTimeLeftInMillis(createdAt)
            Log.d("HostFinishMakeQuizFragment", "Calculated timeInMillis: $timeInMillis")

            if (timeInMillis <= 0) {
                binding.timerTv.text = "만료되었습니다."
                return@let
            }
        }

        startTimer()

        return binding.root
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
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.timerTv.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timerTv.text = "00:00"
            }
        }.start()
    }
}
