package com.example.spoteam_android.presentation.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentPermissionWaitBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.presentation.category.CategoryFragment
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.community.MemberOnStudiesResponse
import com.example.spoteam_android.presentation.community.MyRecruitingStudyDetail
import com.example.spoteam_android.presentation.interestarea.InterestVPAdapter
import com.example.spoteam_android.presentation.study.StudyViewModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PermissionWaitFragment : Fragment() {

    lateinit var binding: FragmentPermissionWaitBinding
    private var memberId : Int = -1
    private var currentPage : Int = 0
    private var size : Int = 5
    private var totalPage : Int = 0
    private var startPage : Int = 0
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private var itemList = ArrayList<MyRecruitingStudyDetail>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPermissionWaitBinding.inflate(inflater, container, false)

        // SharedPreferences 사용
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1


        fetchInProgressStudy()

        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.recruitStudyTv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CategoryFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.previousPage.setOnClickListener {
            currentPage = if (currentPage == 0) totalPage - 1 else currentPage - 1
            fetchInProgressStudy()
        }

        binding.nextPage.setOnClickListener {
            currentPage = if (currentPage == totalPage - 1) 0 else currentPage + 1
            fetchInProgressStudy()
        }

        return binding.root
    }


    private fun fetchInProgressStudy() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMemberAppliedStudies(currentPage, size)
            .enqueue(object : Callback<MemberOnStudiesResponse> {
                override fun onResponse(
                    call: Call<MemberOnStudiesResponse>,
                    response: Response<MemberOnStudiesResponse>
                ) {
                    Log.d("InProgress", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val inProgressResponse = response.body()
                        Log.d("InProgress", "responseBody: ${inProgressResponse?.isSuccess}")
                        if (inProgressResponse?.isSuccess == true) {
                            val studyInfo = inProgressResponse.result.content
                            Log.d("StudyInfo","${inProgressResponse.result.content}")
                            totalPage = inProgressResponse.result.totalPages

                            if(studyInfo.isNotEmpty()) {
                                updatePageUI()

                                binding.emptyWaiting.visibility = View.GONE
                                binding.participatingStudyReyclerview.visibility = View.VISIBLE


                                itemList.clear()
                                itemList.addAll(studyInfo)

                                initRecyclerView()
                                interestBoardAdapter.notifyDataSetChanged()
                            } else {
                                binding.emptyWaiting.visibility = View.VISIBLE
                                binding.participatingStudyReyclerview.visibility = View.GONE
                                interestBoardAdapter.notifyDataSetChanged()
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


    fun initRecyclerView() {
        val participatingboard = binding.participatingStudyReyclerview

        // MyRecruitingStudyDetail을 BoardItem으로 변환
        val itemList = ArrayList(itemList.map { detail ->
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
        interestBoardAdapter = InterestVPAdapter(itemList, onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        }, studyViewModel = studyViewModel)

        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        participatingboard.adapter = interestBoardAdapter
        participatingboard.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val fragment = com.example.spoteam_android.presentation.study.DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        interestBoardAdapter.notifyDataSetChanged()
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    studyItem.heartCount += if (liked) 1 else -1
                    val icon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(icon)

                    // RecyclerView 갱신
                    interestBoardAdapter.notifyItemChanged(
                        interestBoardAdapter.dataList.indexOf(studyItem)
                    )
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
            totalPage <= 5 -> 0
            currentPage >= totalPage - 3 -> totalPage - 5
            currentPage >= 2 -> currentPage - 2
            else -> 0
        }

        // 🔵 페이지 번호 버튼 처리
        pageButtons.forEachIndexed { index, button ->
            val pageNum = startPage + index
            if (pageNum < totalPage) {
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
        if (totalPage <= 1) {
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
                currentPage = if (currentPage == 0) totalPage - 1 else currentPage - 1
                fetchInProgressStudy()
            }

            next.setOnClickListener {
                currentPage = if (currentPage == totalPage - 1) 0 else currentPage + 1
                fetchInProgressStudy()
            }
        }
    }
}


