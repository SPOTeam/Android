package com.example.spoteam_android.ui.study.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCrewWrongQuizBinding

class CrewWrongQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewWrongQuizBinding
    private val timerViewModel: TimerViewModel by activityViewModels()

    private var wrongCount = 0
    private var timeInMillis: Long = 0L // 추가

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

        initTimerObserve() // 타이머 관찰 시작

        return binding.root
    }

    private fun initTimerObserve() {

        timerViewModel.timerSeconds.observe(viewLifecycleOwner) { elapsedSeconds ->
            val elapsedMillis = elapsedSeconds * 1000
            val remainingMillis = timeInMillis - elapsedMillis

            if (remainingMillis > 0) {

            } else {

                parentFragmentManager.beginTransaction()
                    .replace(R.id.child_fragment, CrewTimeOutFragment())
                    .commitAllowingStateLoss()
            }
        }
    }
}
