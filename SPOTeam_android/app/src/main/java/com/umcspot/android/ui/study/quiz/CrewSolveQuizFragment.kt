package com.umcspot.android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentCrewSolveQuizBinding
import com.umcspot.android.ui.community.CommunityAPIService
import com.umcspot.android.ui.community.CrewAnswerRequest
import com.umcspot.android.ui.community.GetCrewQuizResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant

class CrewSolveQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewSolveQuizBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val timerViewModel: TimerViewModel by activityViewModels()

    private var studyId = -1
    private var scheduleId = -1
    private var question = ""

    private lateinit var answerEt: EditText
    private lateinit var sendButton: TextView
    private lateinit var des: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewSolveQuizBinding.inflate(inflater, container, false)

        studyViewModel.studyId.observe(viewLifecycleOwner) { id ->
            if (id != null) {
                studyId = id
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
        }

        initContent()
        initTextWatcher()
        initTimerObserve()

        binding.sendCrewAnswer.setOnClickListener {
            val answer = binding.answerFromCrewEt.text.toString()
            checkCrewAnswer(answer)
        }

        return binding.root
    }

    private fun initContent() {
        binding.quizTv.text = timerViewModel.quiz
    }

    private fun initTextWatcher() {
        answerEt = binding.answerFromCrewEt
        sendButton = binding.sendCrewAnswer
        des = binding.quizDesTv

        answerEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
                if ((s?.length ?: 0) > 10) {
                    answerEt.setText(s?.subSequence(0, 10))
                    answerEt.setSelection(10)
                }
            }
        })
    }

    private fun initTimerObserve() {
        binding.timerTv.visibility = View.VISIBLE

        timerViewModel.remainingMillis.observe(viewLifecycleOwner) { remainingMillis ->
            if (remainingMillis > 0) {
                val minutes = (remainingMillis / 1000) / 60
                val seconds = (remainingMillis / 1000) % 60
                binding.timerTv.text = String.format("%02d : %02d", minutes, seconds)
                binding.answerFromCrewEt.isEnabled = true
                binding.sendCrewAnswer.isEnabled = binding.answerFromCrewEt.text.isNotBlank()
            } else {
                binding.timerTv.text = "00 : 00"
                binding.answerFromCrewEt.isEnabled = false
                binding.sendCrewAnswer.isEnabled = false

                parentFragmentManager.beginTransaction()
                    .replace(R.id.child_fragment, CrewTimeOutFragment())
                    .commitAllowingStateLoss()
            }
        }
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
                        when (quizResponse?.message) {
                            "스터디 출석 완료" -> crewSuccess()
                            "스터디 퀴즈 오답" -> crewRetry(quizResponse.result.tryNum)
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

    private fun crewRetry(tryNum: Int) {
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
}
