package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentToMakeQuizBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HostMakeQuizFirstFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToMakeQuizBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    private var scheduleId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentToMakeQuizBinding.inflate(inflater, container, false)

        // 전달받은 scheduleId를 arguments에서 가져옴
        scheduleId = arguments?.getInt("scheduleId")!!
        Log.d("HostMakeQuizFirstFragment", "Received scheduleId: $scheduleId")

        binding.startAttendanceTv.setOnClickListener {
            val hostMakeQuizFragment = HostMakeQuizFragment().apply {
                arguments = Bundle().apply {
                    putInt("scheduleId", scheduleId) // scheduleId 다시 전달
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, hostMakeQuizFragment)
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}
