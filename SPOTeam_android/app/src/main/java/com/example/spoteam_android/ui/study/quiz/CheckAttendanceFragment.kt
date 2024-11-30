package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentAttendanceDefaultBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.GetQuizResponse
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.StudyMemberResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import kotlin.properties.Delegates


class CheckAttendanceFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAttendanceDefaultBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    private var scheduleId = -1;
    private var currentMemberId = -1;
    private var studyId = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAttendanceDefaultBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        // memberId 가져오기
        currentMemberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1
        Log.d("CurrnentMemberId",  currentMemberId.toString())

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("DetailStudyHomeFragment", "Received studyId from ViewModel: $studyId")
            if (studyId != null) {
                this.studyId = studyId
                fetchStudyMember(studyId)
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    private fun contentToCrew() {
        binding.description.text = "퀴즈의 정답을 맞추면 출석됩니다."
        binding.timerTv.visibility = View.VISIBLE
        fetchQuiz()
    }


    private fun makeHostMakeQuizFragment() {
        val hostMakeQuizFirstFragment = HostMakeQuizFirstFragment().apply {
            arguments = Bundle().apply {
                putInt("scheduleId", scheduleId) // scheduleId 전달
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, hostMakeQuizFirstFragment)
            .commitAllowingStateLoss()
    }


    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun fetchQuiz() {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .getStudyScheduleQuiz(studyId, scheduleId.toInt(), LocalDate.of(2024,11,7))
            .enqueue(object : Callback<GetQuizResponse> {
                override fun onResponse(
                    call: Call<GetQuizResponse>,
                    response: Response<GetQuizResponse>
                ) {
                    if (response.isSuccessful) {
                        val quizResponse = response.body()
                        if (quizResponse?.isSuccess == "true") {
                            initCrewFragment(quizResponse.result.question)
                            Log.d("MAKEDQUIZ", quizResponse.result.question)
                        } else {
                            Log.d("MAKEDQUIZ", quizResponse?.message.toString())
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<GetQuizResponse>, t: Throwable) {
                    Log.e("QuizContentError", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initCrewFragment(quiz : String) {
        val crewTakeAttendanceFragment = CrewTakeAttendanceFragment().apply {
            arguments = Bundle().apply {
                putInt("scheduleId", scheduleId)
                putString("question", quiz) // scheduleId 전달
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, crewTakeAttendanceFragment)
            .commitAllowingStateLoss()
    }


    private fun fetchStudyMember(studyId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .getStudyMembers(studyId)
            .enqueue(object : Callback<StudyMemberResponse> {
                override fun onResponse(
                    call: Call<StudyMemberResponse>,
                    response: Response<StudyMemberResponse>
                ) {
//                    Log.d("LikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val memberResponse = response.body()
//                        Log.d("LikeContent", "responseBody: ${likeResponse?.isSuccess}")
                        if (memberResponse?.isSuccess == "true") {

                            if(memberResponse.result.members[0].memberId == currentMemberId) {
//                                makeHostMakeQuizFragment()
                                contentToCrew()
                            } else {
//                                contentToCrew()
                            }
                            initMemberRecyclerView(memberResponse.result.members)
                        } else {
                            showError(memberResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyMemberResponse>, t: Throwable) {
                    Log.e("QuizMember", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initMemberRecyclerView(members : List<MembersDetail> ) {
        binding.memberTl.layoutManager = GridLayoutManager(context, 5)

        val dataRVAdapter = HostMakeQuizMemberRVAdapter(members)
        //리스너 객체 생성 및 전달

        binding.memberTl.adapter = dataRVAdapter
    }
}