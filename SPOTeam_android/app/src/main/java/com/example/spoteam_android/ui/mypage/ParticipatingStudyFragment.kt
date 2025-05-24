package com.example.spoteam_android.ui.mypage

import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentParticipatingStudyBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberOnStudiesResponse
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface fetchProgressStudy {
    fun fetchProgress()
}

class ParticipatingStudyFragment : Fragment(), fetchProgressStudy {

    lateinit var binding: FragmentParticipatingStudyBinding
    val studyViewModel: StudyViewModel by activityViewModels()

    var memberId : Int = -1

    private var currentPage = 0
    private val size = 5
    private var totalPages = 0
    private var startPage = 0


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
            fetchInProgressStudy()
        }

        parentFragmentManager.setFragmentResultListener(
            "study_withdraw_success",
            viewLifecycleOwner
        ) { _, _ ->
            fetchInProgressStudy()
        }




        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.previousPage.setOnClickListener {
            currentPage = if (currentPage == 0) totalPages - 1 else currentPage - 1
            fetchInProgressStudy()
        }

        binding.nextPage.setOnClickListener {
            currentPage = if (currentPage == totalPages - 1) 0 else currentPage + 1
            fetchInProgressStudy()
        }
        fetchInProgressStudy()
    }



    private fun fetchInProgressStudy() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMemberOnStudies(currentPage, size)
            .enqueue(object : Callback<MemberOnStudiesResponse> {
                override fun onResponse(
                    call: Call<MemberOnStudiesResponse>,
                    response: Response<MemberOnStudiesResponse>
                ) {
                    if (response.isSuccessful) {
                        val inProgressResponse = response.body()
                        if (inProgressResponse?.isSuccess == "true") {
                            val studyInfo = inProgressResponse.result.content
                            totalPages = inProgressResponse.result.totalPages

                            if (studyInfo.isNotEmpty()) {
                                updatePageUI() // 페이지 번호 UI 갱신
                                binding.emptyWaiting.visibility = View.GONE
                                binding.participatingStudyReyclerview.visibility = View.VISIBLE
                                initRecyclerView(studyInfo)
                            } else {
                                binding.emptyWaiting.visibility = View.VISIBLE
                                binding.participatingStudyReyclerview.visibility = View.GONE
                            }

                        } else {
                            showError(inProgressResponse?.message)
                        }
                    } else {
                        showError("서버 응답 실패: ${response.code()}")
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
                liked = detail.liked,
                isHost = false
            )
        })

        // 어댑터 초기화
        val boardAdapter = BoardAdapter(
            itemList,
            onItemClick = {
                selectedItem ->

            },
            onLikeClick = {
                  _, _ ->
            },
            listener = this // 🔹 fetchProgressStudy 구현체 전달
        )

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

    private fun updatePageUI() {
        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        val prev = binding.previousPage
        val next = binding.nextPage
        val grayColor = ContextCompat.getColor(requireContext(), R.color.g300)
        val blueColor = ContextCompat.getColor(requireContext(), R.color.b500)
        val g400Color = ContextCompat.getColor(requireContext(), R.color.g400)

        // 🔵 시작 페이지 계산
        startPage = when {
            totalPages <= 5 -> 0
            currentPage >= totalPages - 3 -> totalPages - 5
            currentPage >= 2 -> currentPage - 2
            else -> 0
        }

        // 🔵 페이지 번호 버튼 처리
        pageButtons.forEachIndexed { index, button ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                button.visibility = View.VISIBLE
                button.text = (pageNum + 1).toString()
                button.setTextColor(
                    if (pageNum == currentPage) blueColor else g400Color
                )
                button.setBackgroundResource(0)
                button.isEnabled = true
                button.setOnClickListener {
                    if (pageNum != currentPage) {
                        currentPage = pageNum
                        fetchInProgressStudy() // 또는 fetch함수명 맞게 호출
                    }
                }
            } else {
                button.visibility = View.GONE
            }
        }

        // 🔵 이전/다음 버튼 처리
        if (totalPages <= 1) {
            prev.isEnabled = false
            next.isEnabled = false
            prev.setColorFilter(grayColor, android.graphics.PorterDuff.Mode.SRC_IN)
            next.setColorFilter(grayColor, android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            prev.isEnabled = true
            next.isEnabled = true
            prev.setColorFilter(blueColor, android.graphics.PorterDuff.Mode.SRC_IN)
            next.setColorFilter(blueColor, android.graphics.PorterDuff.Mode.SRC_IN)

            prev.setOnClickListener {
                currentPage = if (currentPage == 0) totalPages - 1 else currentPage - 1
                fetchInProgressStudy()
            }

            next.setOnClickListener {
                currentPage = if (currentPage == totalPages - 1) 0 else currentPage + 1
                fetchInProgressStudy()
            }
        }
    }

    override fun fetchProgress() {
        fetchInProgressStudy()
    }
}


