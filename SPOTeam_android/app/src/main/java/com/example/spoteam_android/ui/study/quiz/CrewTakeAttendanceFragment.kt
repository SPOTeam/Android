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

        if(question != "") {
            binding.startAttendanceTv.isEnabled = true;
        }

        // parentFragment 접근 후 수정
        parentFragment?.let { parent ->
            if (parent is CheckAttendanceFragment) { // YourParentFragmentInterface는 수정하려는 메서드를 포함
                parent.changeDescription()
            }
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