package com.example.spoteam_android.ui.mypage

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentParticipatingStudyBinding
import com.example.spoteam_android.databinding.FragmentPermissionWaitBinding
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberOnStudiesResponse
import com.example.spoteam_android.ui.community.MyRecruitingStudyDetail
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
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
    private lateinit var studyApiService: StudyApiService
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
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)


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
            if (currentPage > 0) {
                currentPage--
                fetchInProgressStudy()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPage - 1) {
                currentPage++
                fetchInProgressStudy()
            }
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
                        if (inProgressResponse?.isSuccess == "true") {
                            val studyInfo = inProgressResponse.result.content
                            Log.d("StudyInfo","${inProgressResponse.result.content}")

                            if(studyInfo.isNotEmpty()) {
                                binding.emptyWaiting.visibility = View.GONE
                                binding.participatingStudyReyclerview.visibility = View.VISIBLE

                                totalPage = inProgressResponse.result.totalPages

                                itemList.clear()
                                itemList.addAll(studyInfo)

                                initRecyclerView()
                                updatePageUI()
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

                val fragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        interestBoardAdapter.notifyDataSetChanged()
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val memberId = getMemberId(requireContext())

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId).enqueue(object : Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    if (response.isSuccessful) {
                        val newStatus = response.body()?.result?.status
                        studyItem.liked = newStatus == "LIKE"
                        val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                        likeButton.setImageResource(newIcon)

                        // heartCount 즉시 증가 또는 감소
                        fetchInProgressStudy()

                        // 최신 데이터 동기화를 위해 fetchDataAnyWhere와 fetchRecommendStudy를 다시 호출
                    } else {
                        Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getMemberId(context: Context): Int {

        var memberId: Int = -1

        val sharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt(
            "${currentEmail}_memberId",
            -1
        ) else -1

        return memberId // 저장된 memberId 없을 시 기본값 -1 반환
    }

    private fun updatePageUI() {
        // ✅ 추가된 부분
        startPage = if (currentPage <= 2) {
            0
        } else {
            maxOf(totalPage - 5, maxOf(0, currentPage - 2))
        }

        Log.d("AllFragment", "totalPages : ${totalPage}, currentPage : ${currentPage}")
        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPage) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(
                    if (pageNum == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.isEnabled = true
                textView.alpha = 1.0f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(0)
                textView.isEnabled = false // 클릭 안 되게
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPage - 1
    }
}


