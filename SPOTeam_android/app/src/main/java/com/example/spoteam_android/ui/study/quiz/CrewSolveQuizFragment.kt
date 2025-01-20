package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCrewSolveQuizBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.CrewAnswerRequest
import com.example.spoteam_android.ui.community.GetCrewQuizResponse
import com.example.spoteam_android.ui.community.GetQuizResponse
import com.example.spoteam_android.ui.community.QuizContentRequest
import com.example.spoteam_android.ui.community.QuizContentResponse
import com.example.spoteam_android.ui.community.QuizInfo
import com.example.spoteam_android.ui.community.StudyMemberResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate


class CrewSolveQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewSolveQuizBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    private var studyId = -1;
    private var scheduleId = -1;
    private var question = "";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("CrewSolveQuizFragment", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                this.studyId = studyId
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding = FragmentCrewSolveQuizBinding.inflate(inflater, container, false)

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
            question = it.getString("question").toString()
            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }
        initContent()

        binding.sendCrewAnswer.setOnClickListener {
            val answer = binding.answerFromCrewEt.text.toString()
            checkCrewAnswer(answer)
        }
        return binding.root
    }

    private fun checkCrewAnswer(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "정답을 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("CrewSolveQuizFragment", "Crew answer: $text")

        val requestBody = CrewAnswerRequest(
            dateTime = Instant.now().toString(),
            answer = text
        )

        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .checkCrewAnswer(studyId, scheduleId, requestBody)
            .enqueue(object : Callback<GetCrewQuizResponse> {
                override fun onResponse(
                    call: Call<GetCrewQuizResponse>,
                    response: Response<GetCrewQuizResponse>
                ) {
                    if (response.isSuccessful) {
                        val quizResponse = response.body()
                        if(quizResponse?.message == "스터디 출석 완료") {
                            crewSuccess()
                        } else if (quizResponse?.message == "스터디 퀴즈 오답"){
                            crewRetry(quizResponse.result.tryNum)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<GetCrewQuizResponse>, t: Throwable) {
                    Log.e("QuizMember", "Failure: ${t.message}", t)
                }
            })
    }

    private fun crewRetry(tryNum : Int) {
        Log.d("CrewRetryQuiz", "pass")

        val crewWrongQuizFragment = CrewWrongQuizFragment().apply {
            arguments = Bundle().apply {
                putInt("tryNum", tryNum)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, crewWrongQuizFragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun crewSuccess() {
        Log.d("CrewCorrectQuiz", "pass")

        parentFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, CrewCorrectQuizFragment())
            .commitAllowingStateLoss()
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initContent() {
        binding.questionFromHostTv.text = question
    }
}
