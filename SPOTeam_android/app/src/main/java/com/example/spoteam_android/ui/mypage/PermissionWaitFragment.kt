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
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentParticipatingStudyBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberOnStudiesResponse
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PermissionWaitFragment : Fragment() {

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

        binding.participatingStudyBold.text = "신청 대기 중인 스터디"

        binding.mypagePrevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }


    private fun fetchInProgressStudy() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMemberAppliedStudies(page, size)
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

                            initRecyclerView(studyInfo)
                        } else {
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

//        participatingboard.post {
//            for (i in 0 until boardAdapter.itemCount) {
//                val holder = participatingboard.findViewHolderForAdapterPosition(i) as? BoardAdapter.BoardViewHolder
//                holder?.binding?.toggle?.visibility = View.VISIBLE
//            }
//        }

        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        participatingboard.adapter = boardAdapter
        participatingboard.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        boardAdapter.notifyDataSetChanged()
    }

}


