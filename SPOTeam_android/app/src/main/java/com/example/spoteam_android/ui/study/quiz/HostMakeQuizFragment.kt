package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    var studyId : Int = -1
    private lateinit var scheduleId : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHostMakeQuizBinding.inflate(inflater, container, false)

        val parent = parentFragment as? CheckAttendanceFragment
        scheduleId = parent?.arguments?.getString("scheduleId").toString()
        Log.d("HostMakeQuizFragment", "Received scheduleId from parentFragment: $scheduleId")

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
//            Log.d("MakeQuiz", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                this.studyId = studyId
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.startAttendanceTv.setOnClickListener{
            val input1 = binding.makeQuestionEt.text.toString()
            val input2 = binding.makeAnswerEt.text.toString()

            val requestBody = QuizContentRequest(
                createdAt = Instant.now().toString(),
                question = input1,
                answer = input2
            )

            postAttendanceQuiz(requestBody)

            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, HostFinishMakeQuizFragment())
                .commitAllowingStateLoss()
        }
        return binding.root
    }

    private fun postAttendanceQuiz(requestBody : QuizContentRequest) {
        Log.d("CreatedAt", requestBody.createdAt)
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java).makeQuiz(studyId, scheduleId.toInt(), requestBody)
            .enqueue(object : Callback<QuizContentResponse> {
                override fun onResponse(
                    call: Call<QuizContentResponse>,
                    response: Response<QuizContentResponse>
                ) {
//                    Log.d("LikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("LikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            Log.e("MakeQuiz", "성공적으로 생성하였습니다.")
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
}