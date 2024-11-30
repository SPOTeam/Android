package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCrewSolveQuizBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.GetQuizResponse
import com.example.spoteam_android.ui.community.QuizInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate


class CrewSolveQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewSolveQuizBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    private var scheduleId = -1;
    private var question = "";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewSolveQuizBinding.inflate(inflater, container, false)

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
            question = it.getString("question").toString()
            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }
        initContent(question)
        return binding.root
    }

    private fun initContent(quiz : String) {
        binding.questionFromHostTv.text = quiz
    }

}

