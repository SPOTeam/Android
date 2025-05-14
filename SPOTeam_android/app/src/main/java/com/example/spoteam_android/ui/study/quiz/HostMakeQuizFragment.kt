package com.example.spoteam_android.ui.study.quiz

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentHostMakeQuizBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.QuizContentRequest
import com.example.spoteam_android.ui.community.QuizContentResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant


class HostMakeQuizFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentHostMakeQuizBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    val timerViewModel: TimerViewModel by activityViewModels()
    var studyId : Int = -1
    private var scheduleId : Int = -1
    private lateinit var quizEditText: EditText
    private lateinit var answerEditText: EditText
    private lateinit var charCountTextQuizView: TextView
    private lateinit var charCountTextAnswerView: TextView
    private lateinit var startButton : TextView
    private lateinit var description : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostMakeQuizBinding.inflate(inflater, container, false)

//        val parent = parentFragment as? CheckAttendanceFragment
        scheduleId = arguments?.getInt("scheduleId")!!
//        Log.d("HostMakeQuizFragment", "Received scheduleId from parentFragment: $scheduleId")

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
//            Log.d("MakeQuiz", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                this.studyId = studyId
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
        initTextWatcher()

        binding.startAttendanceTv.setOnClickListener{
            val input1 = binding.makeQuestionEt.text.toString()
            val input2 = binding.makeAnswerEt.text.toString()

            val now = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val createdAt = now.format(formatter)

            val requestBody = QuizContentRequest(
                createdAt = createdAt,
                question = input1,
                answer = input2
            )

            postAttendanceQuiz(requestBody)
        }
        return binding.root
    }

    private fun postAttendanceQuiz(requestBody: QuizContentRequest) {
//        Log.d("CreatedAt", requestBody.createdAt)
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .makeQuiz(studyId, scheduleId, requestBody)
            .enqueue(object : Callback<QuizContentResponse> {
                override fun onResponse(
                    call: Call<QuizContentResponse>,
                    response: Response<QuizContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        if (likeResponse?.isSuccess == "true") {
                            val result = likeResponse.result

                            // ✅ 여기 추가: TimerViewModel 세팅
                            timerViewModel.quiz = result.question
                            timerViewModel.startTimer(result.createdAt)

                            val hostFinishMakeQuizFragment = HostFinishMakeQuizFragment().apply {
                                arguments = Bundle().apply {
                                    putString("createdAt", result.createdAt)
                                }
                            }

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.child_fragment, hostFinishMakeQuizFragment)
                                .commitAllowingStateLoss()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<QuizContentResponse>, t: Throwable) {
                    Log.e("MakeQuiz", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initTextWatcher() {
        quizEditText = binding.makeQuestionEt
        answerEditText = binding.makeAnswerEt
        charCountTextQuizView = binding.etCount1Tv
        charCountTextAnswerView = binding.etCount2Tv
        startButton = binding.startAttendanceTv
        description = binding.quizDesTv
//        startButton.isEnabled = false

        var quiz = false
        var answer = false

        quizEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                charCountTextQuizView.text = textLength.toString()

                quiz = textLength > 0

                if(quiz) {
                    binding.makeQuestionEt.isSelected = true
                    binding.bracketLeft1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.etCount1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.maxChar1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.bracketRight1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                } else {
                    binding.makeQuestionEt.isSelected = false
                    binding.bracketLeft1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.etCount1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.maxChar1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.bracketRight1Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                }

                if(quiz && answer) {
                    startButton.isEnabled = true
                    description.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.b500))
                } else {
                    startButton.isEnabled = false
                    description.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 > 20) {
                    quizEditText.setText(s?.subSequence(0, 20)) // 30자 초과 방지
                    quizEditText.setSelection(20) // 커서를 맨 끝으로 이동
                }
            }
        })

        answerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                charCountTextAnswerView.text = textLength.toString()

                // 버튼 활성화/비활성화
                answer = textLength > 0

                if(answer) {
                    binding.makeAnswerEt.isSelected = true
                    binding.bracketLeft2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.etCount2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.maxChar2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                    binding.bracketRight2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))
                } else {
                    binding.makeAnswerEt.isSelected = false
                    binding.bracketLeft2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.etCount2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.maxChar2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                    binding.bracketRight2Tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g300))
                }


                if(quiz && answer) {
                    startButton.isEnabled = true
                    description.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.b500))
                } else {
                    startButton.isEnabled = false
                    description.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.g400))

                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 > 10) {
                    answerEditText.setText(s?.subSequence(0, 10)) // 20자 초과 방지
                    answerEditText.setSelection(10) // 커서를 맨 끝으로 이동
                }
            }
        })
    }
}