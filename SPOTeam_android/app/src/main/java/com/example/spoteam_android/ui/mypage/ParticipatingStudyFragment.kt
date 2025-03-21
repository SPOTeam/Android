package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.HostApiResponse
import com.example.spoteam_android.HostResult
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentParticipatingStudyBinding
import com.example.spoteam_android.ui.alert.CheckAppliedStudyFragment
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberOnStudiesResponse
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.GetHostInterface
import com.example.spoteam_android.ui.study.StudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParticipatingStudyFragment : Fragment() {

    lateinit var binding: FragmentParticipatingStudyBinding
    var memberId : Int = -1
    var page : Int = 0
    var size : Int = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipatingStudyBinding.inflate(inflater, container, false)

        // SharedPreferences 사용
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1


        fetchInProgressStudy()

        binding.fragmentConsiderAttendanceTitleTv.text = "참여 중인 스터디"
        
        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        parentFragmentManager.setFragmentResultListener(
            "host_withdraw_success",
            viewLifecycleOwner
        ) { _, _ ->
            Log.d("ParticipatingStudy", "✅ 위임 성공 이벤트 수신 → 목록 새로고침")
            fetchInProgressStudy()
        }

        parentFragmentManager.setFragmentResultListener(
            "study_withdraw_success",
            viewLifecycleOwner
        ) { _, _ ->
            Log.d("ParticipatingStudy", "✅ 탈퇴 이벤트 수신 → 목록 새로고침")
            fetchInProgressStudy()
        }




        return binding.root
    }


    private fun fetchInProgressStudy() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMemberOnStudies(page, size)
            .enqueue(object : Callback<MemberOnStudiesResponse> {
                override fun onResponse(
                    call: Call<MemberOnStudiesResponse>,
                    response: Response<MemberOnStudiesResponse>
                ) {
                    Log.d("InProgress", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val inProgressResponse = response.body()
                        Log.d("InProgress", "responseBody: ${inProgressResponse?.isSuccess}")
                        if (inProgressResponse?.isSuccess == "true") {
                            val studyInfo = inProgressResponse.result.content

                            if (studyInfo.isNotEmpty()) {
                                binding.emptyWaiting.visibility = View.GONE
                                binding.participatingStudyReyclerview.visibility = View.VISIBLE
                                initRecyclerView(studyInfo)

                            } else {
                                binding.emptyWaiting.visibility = View.VISIBLE
                                binding.participatingStudyReyclerview.visibility = View.GONE
                            }
                        } else {
                            binding.emptyWaiting.visibility = View.VISIBLE
                            binding.participatingStudyReyclerview.visibility = View.GONE
                            showError(inProgressResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MemberOnStudiesResponse>, t: Throwable) {
                    Log.e("InProgress", "Failure: ${t.message}", t)
                }
            })
    }


    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }


    fun initRecyclerView(studyInfo: List<MyRecruitingStudyDetail>) {
        val participatingboard = binding.participatingStudyReyclerview

        // MyRecruitingStudyDetail을 BoardItem으로 변환
        val itemList = ArrayList(studyInfo.map { detail ->
            BoardItem(
                studyId = detail.studyId,
                title = detail.title,
                goal = detail.goal,
                introduction = detail.introduction,
                memberCount = detail.memberCount,
                heartCount = detail.heartCount,
                hitNum = detail.hitNum,
                maxPeople = detail.maxPeople,
                studyState = detail.studyState,
                themeTypes = detail.themeTypes,
                regions = detail.regions,
                imageUrl = detail.imageUrl,
                liked = detail.liked
            )
        })

        // 어댑터 초기화
        val boardAdapter = BoardAdapter(itemList, onItemClick = { selectedItem -> }, onLikeClick = { _, _ -> })

        participatingboard.post {
            for (i in 0 until boardAdapter.itemCount) {
                val holder = participatingboard.findViewHolderForAdapterPosition(i) as? BoardAdapter.BoardViewHolder
                holder?.binding?.toggle?.visibility = View.VISIBLE
            }
        }

        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        participatingboard.adapter = boardAdapter
        participatingboard.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        boardAdapter.notifyDataSetChanged()
    }



}


