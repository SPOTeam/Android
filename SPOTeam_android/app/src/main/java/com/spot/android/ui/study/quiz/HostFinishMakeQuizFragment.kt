package com.spot.android.ui.study.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spot.android.databinding.FragmentHostFinishMakeQuizBinding

class HostFinishMakeQuizFragment : Fragment() {

    private lateinit var binding: FragmentHostFinishMakeQuizBinding
    private var createdAt: String = ""
    private val timerViewModel: TimerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostFinishMakeQuizBinding.inflate(inflater, container, false)

        arguments?.let {
            createdAt = it.getString("createdAt").toString()
//            Log.d("HostFinishMakeQuizFragment", "Received createdAt: $createdAt")
        }

        initTimerObserve()

        return binding.root
    }

    private fun initTimerObserve() {
        binding.timerTv.visibility = View.VISIBLE

        timerViewModel.remainingMillis.observe(viewLifecycleOwner) { remainingMillis ->
            if (remainingMillis > 0) {
                val minutes = (remainingMillis / 1000) / 60
                val seconds = (remainingMillis / 1000) % 60
                binding.timerTv.text = String.format("%02d : %02d", minutes, seconds)
            } else {
                binding.timerTv.text = "00 : 00"
                // 필요하면 여기서 시간 만료 시 처리 추가 가능
            }
        }
    }
}
