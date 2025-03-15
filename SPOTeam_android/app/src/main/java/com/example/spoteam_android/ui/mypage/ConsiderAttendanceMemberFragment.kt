package com.example.spoteam_android.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.attendanceMemberData
import com.example.spoteam_android.databinding.FragmentConsiderAttendanceMemberBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberAttendanceIntroResponse
import com.example.spoteam_android.ui.community.MyStudyAttendanceMemberResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsiderAttendanceMemberFragment : Fragment() {

    private lateinit var binding: FragmentConsiderAttendanceMemberBinding
    private var studyId: Int = -1
    private lateinit var attendanceMemberList: ArrayList<attendanceMemberData>
    private lateinit var dataRVAdapter: ConsiderAttendanceMembersRVAdapter
    private var pendingRequests = 0 // 🔥 비동기 요청 수 추적

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentConsiderAttendanceMemberBinding.inflate(inflater, container, false)
        studyId = arguments?.getInt("recruitingStudyId")!!
        Log.d("ConsiderAttendanceMemberFragment", "$studyId")

        attendanceMemberList = ArrayList() // ✅ 리스트 초기화

        binding.prevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fetchStudyAttendanceMembers() // ✅ 지원한 멤버 가져오기

        return binding.root
    }

    private fun fetchStudyAttendanceMembers() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMyStudyAttendanceMember(studyId)
            .enqueue(object : Callback<MyStudyAttendanceMemberResponse> {
                override fun onResponse(
                    call: Call<MyStudyAttendanceMemberResponse>,
                    response: Response<MyStudyAttendanceMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val memberAttendanceResponse = response.body()
                        if (memberAttendanceResponse?.isSuccess == "true") {
                            val membersInfo = memberAttendanceResponse.result.members

                            attendanceMemberList.clear() // ✅ 기존 데이터 초기화

                            if (membersInfo.isNotEmpty()) {
                                binding.emptyRecruitingMember.visibility = View.GONE
                                binding.fragmentConsiderAttendanceMemberRv.visibility = View.VISIBLE

                                pendingRequests = membersInfo.size // 🔥 비동기 요청 개수 설정

                                membersInfo.forEach { member ->
                                    // ✅ memberId, name, profileImage만 저장
                                    val newMember = attendanceMemberData(
                                        memberId = member.memberId,
                                        name = member.name, // ✅ name 받아오기
                                        profileImage = member.profileImage,
                                        nickname = "", // ✅ 이후에 추가
                                        introduction = "" // ✅ 이후에 추가
                                    )

                                    attendanceMemberList.add(newMember)
                                    fetchIntroduction(member.memberId) // ✅ 지원 동기 가져오기
                                }

                            } else {
                                binding.emptyRecruitingMember.visibility = View.VISIBLE
                                binding.fragmentConsiderAttendanceMemberRv.visibility = View.GONE
                            }
                        } else {
                            binding.emptyRecruitingMember.visibility = View.VISIBLE
                            binding.fragmentConsiderAttendanceMemberRv.visibility = View.GONE
                            showError(memberAttendanceResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MyStudyAttendanceMemberResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchIntroduction(memberId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getAttendanceIntroduction(studyId, memberId)
            .enqueue(object : Callback<MemberAttendanceIntroResponse> {
                override fun onResponse(
                    call: Call<MemberAttendanceIntroResponse>,
                    response: Response<MemberAttendanceIntroResponse>
                ) {
                    if (response.isSuccessful) {
                        val introResponse = response.body()
                        if (introResponse?.isSuccess == "true") {
                            val memberIntro = introResponse.result

                            // ✅ 기존 리스트에서 해당 memberId를 찾아 nickname, introduction 추가
                            attendanceMemberList.find { it.memberId == memberIntro.memberId }?.let {
                                it.nickname = memberIntro.nickname
                                it.introduction = memberIntro.introduction
                            }

                            pendingRequests-- // 🔥 요청 하나 완료될 때마다 감소

                            if (pendingRequests == 0) {
                                // ✅ 모든 요청 완료 후 RecyclerView 업데이트
                                initRecyclerView()
                            }

                        } else {
                            showError(introResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MemberAttendanceIntroResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initRecyclerView() {
        binding.fragmentConsiderAttendanceMemberRv.layoutManager = GridLayoutManager(context, 2)

        dataRVAdapter = ConsiderAttendanceMembersRVAdapter(attendanceMemberList) // ✅ 최종 리스트 적용
        binding.fragmentConsiderAttendanceMemberRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : ConsiderAttendanceMembersRVAdapter.OnItemClickListener {
            override fun onRejectClick(data: attendanceMemberData) {
                val dlg = AttendanceDenyDialog(requireContext())
                dlg.setMemberId(data.memberId)
                dlg.setStudyId(studyId)
                dlg.start()
            }

            override fun onAcceptClick(data: attendanceMemberData) {
                acceptMemberAttendance(data.memberId)
            }
        })
    }

    private fun acceptMemberAttendance(memberId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postAttendanceMember(studyId, memberId,true)
            .enqueue(object : Callback<MemberAcceptResponse> {
                override fun onResponse(
                    call: Call<MemberAcceptResponse>,
                    response: Response<MemberAcceptResponse>
                ) {
                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val memberAttendanceResponse = response.body()
                        Log.d("MyStudyAttendance", "responseBody: ${memberAttendanceResponse?.isSuccess}")
                        if (memberAttendanceResponse?.isSuccess == "true") {
                            val dlg = AttendanceContentDialog(requireContext())
                            dlg.start()
                        } else {
                            showError(memberAttendanceResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }
                override fun onFailure(call: Call<MemberAcceptResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
