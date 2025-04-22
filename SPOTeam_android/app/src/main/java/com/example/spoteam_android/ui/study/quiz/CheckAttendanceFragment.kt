package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentAttendanceDefaultBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.GetQuizResponse
import com.example.spoteam_android.ui.community.GetScheduleResponse
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.StudyMemberResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale
import java.util.TimeZone


class CheckAttendanceFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAttendanceDefaultBinding
    val studyViewModel: StudyViewModel by activityViewModels()
    private var scheduleId = -1;
    private var currentMemberId = -1;
    private var studyId = -1;
    private var quiz : String = ""
    private var createdAt : String = ""


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
        Log.d("CurrentMemberId",  currentMemberId.toString())

        arguments?.let {
            scheduleId = it.getInt("scheduleId")
            Log.d("CheckAttendanceFragment", "Received scheduleId: $scheduleId")
        }

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("CheckAttendanceFragment", "Received studyId from ViewModel: $studyId and $scheduleId")
            if (studyId != null) {
                this.studyId = studyId
                fetchStudyMember()
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        binding.writeContentPrevIv.setOnClickListener{
            dismiss()
        }
        return binding.root
    }

    private fun makeHostMakeQuizFragment() {
        Log.d("makeHostMakeQuizFragment", scheduleId.toString())
        val hostMakeQuizFirstFragment = HostMakeQuizFirstFragment().apply {
            arguments = Bundle().apply {
                putInt("scheduleId", scheduleId) // scheduleId 전달
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, hostMakeQuizFirstFragment)
            .commitAllowingStateLoss()
    }

    fun changeDescription() {
        binding.description.text = "퀴즈의 정답을 맞추면 출석됩니다."

    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun fetchQuiz(members: List<MembersDetail>) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .getStudyScheduleQuiz(studyId, scheduleId, LocalDate.now().toString())
            .enqueue(object : Callback<GetQuizResponse> {
                override fun onResponse(
                    call: Call<GetQuizResponse>,
                    response: Response<GetQuizResponse>
                ) {
                    if (response.isSuccessful) {
                        val quizResponse = response.body()
                        if(quizResponse?.isSuccess == "true") {
                            quiz = quizResponse.result.question
                            createdAt = quizResponse.result.createdAt
                            fetchScheduleMember()
                        } else {
                            if(members[0].memberId == currentMemberId) {
                                Log.d("checkIng", "makeHostMakeQuizFragment")
                                makeHostMakeQuizFragment()
                            } else {
                                Log.d("checkIng", "disableCrew")
                                initCrewFragment()
                            }
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

    private fun initCrewFinishFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, CrewCorrectQuizFragment())
            .commitAllowingStateLoss()
    }

    private fun initHostAlreadyMakeQuiz() {
        val hostFinishMakeQuizFragment = HostFinishMakeQuizFragment().apply {
            arguments = Bundle().apply {
                putString("createdAt", createdAt)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, hostFinishMakeQuizFragment)
            .commitAllowingStateLoss()
    }

    private fun initCrewFragment() {
        val crewTakeAttendanceFragment = CrewTakeAttendanceFragment().apply {
            arguments = Bundle().apply {
                putString("createdAt", createdAt)
                putInt("scheduleId", scheduleId)
                putString("question", quiz)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment, crewTakeAttendanceFragment)
            .commitAllowingStateLoss()
    }

    private fun fetchStudyMember() {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .getStudyMembers(studyId)
            .enqueue(object : Callback<StudyMemberResponse> {
                override fun onResponse(
                    call: Call<StudyMemberResponse>,
                    response: Response<StudyMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val memberResponse = response.body()
                        if (memberResponse?.isSuccess == "true") {
                            initMemberRecyclerView(memberResponse.result.members)
                            fetchQuiz(memberResponse.result.members)
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

    private fun fetchScheduleMember() {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .getScheduleInfo(studyId, scheduleId, LocalDate.now().toString())
            .enqueue(object : Callback<GetScheduleResponse> {
                override fun onResponse(
                    call: Call<GetScheduleResponse>,
                    response: Response<GetScheduleResponse>
                ) {
                    if (response.isSuccessful) {
                        val scheduleResponse = response.body()
                        if (scheduleResponse?.isSuccess == "true") {
                            if(scheduleResponse.result.studyMembers[0].isOwned) { // ! 붙이면 만들기, 풀기 왔다갔다 가능
                                Log.d("checkIng", "initHostAlreadyMakeQuiz")
                                initHostAlreadyMakeQuiz()
                            } else {
                                Log.d("checkIng", "initCrewFragment")
                                val m = scheduleResponse.result.studyMembers.find { it.memberId == currentMemberId }
                                if(m?.isAttending == false) {
                                    initCrewFragment()
                                } else {
                                    initCrewFinishFragment()
                                }
                            }
                        } else {
                            showError(scheduleResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<GetScheduleResponse>, t: Throwable) {
                    Log.e("QuizMember", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initMemberRecyclerView(studyMembers: List<MembersDetail>) {
        binding.memberTl.layoutManager = GridLayoutManager(context, 5)

        val dataRVAdapter = HostMakeQuizMemberRVAdapter(studyMembers,currentMemberId)
        //리스너 객체 생성 및 전달

        binding.memberTl.adapter = dataRVAdapter
    }
}