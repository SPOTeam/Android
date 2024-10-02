package com.example.spoteam_android.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.databinding.FragmentConsiderAttendanceMemberBinding
import com.example.spoteam_android.ui.community.AttendanceMemberInfo
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.MemberAttendanceIntroResponse
import com.example.spoteam_android.ui.community.MyStudyAttendanceMemberResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsiderAttendanceMemberFragment : Fragment() {

    private lateinit var binding: FragmentConsiderAttendanceMemberBinding
    private var studyId : Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentConsiderAttendanceMemberBinding.inflate(inflater, container, false)

        studyId = arguments?.getInt("recruitingStudyId")!!
        Log.d("ConsiderAttendanceMemberFragment", "$studyId")

        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        fetchStudyAttendanceMembers()

        return binding.root
    }

    private fun fetchStudyAttendanceMembers() {
        CommunityRetrofitClient.instance.getMyStudyAttendanceMember(studyId)
            .enqueue(object : Callback<MyStudyAttendanceMemberResponse> {
                override fun onResponse(
                    call: Call<MyStudyAttendanceMemberResponse>,
                    response: Response<MyStudyAttendanceMemberResponse>
                ) {
                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val memberAttendanceResponse = response.body()
                        Log.d("MyStudyAttendance", "responseBody: ${memberAttendanceResponse?.isSuccess}")
                        if (memberAttendanceResponse?.isSuccess == "true") {
                            val membersInfo = memberAttendanceResponse.result.members
                            initRecyclerView(membersInfo)
                        } else {
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

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerView(membersInfo: List<AttendanceMemberInfo>) {
        binding.fragmentConsiderAttendanceMemberRv.layoutManager = GridLayoutManager(context, 2)

        val dataRVAdapter = ConsiderAttendanceMembersRVAdapter(membersInfo)
        //리스너 객체 생성 및 전달

        binding.fragmentConsiderAttendanceMemberRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : ConsiderAttendanceMembersRVAdapter.OnItemClickListener {
            override fun onItemClick(data: AttendanceMemberInfo) {
                fetchIntroduction(data.memberId)
            }

        })
    }

    private fun fetchIntroduction(memberId : Int) {
        CommunityRetrofitClient.instance.getAttendanceIntroduction(studyId, memberId)
            .enqueue(object : Callback<MemberAttendanceIntroResponse> {
                override fun onResponse(
                    call: Call<MemberAttendanceIntroResponse>,
                    response: Response<MemberAttendanceIntroResponse>
                ) {
                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val introResponse = response.body()
                        Log.d("MyStudyAttendance", "responseBody: ${introResponse?.isSuccess}")
                        if (introResponse?.isSuccess == "true") {
                            val dlg = AttendanceContentDialog(requireContext())
                            dlg.setInfo(introResponse.result)
                            dlg.start()
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
}
