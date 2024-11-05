package com.example.spoteam_android

import StudyApiService
import StudyViewModel
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentHouseBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.calendar.CalendarFragment
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.category_tabs.AllCategoryFragment
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.CommunityResponse
import com.example.spoteam_android.ui.community.StudyContentLikeResponse
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestFragment
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class HouseFragment : Fragment() {

    lateinit var binding: FragmentHouseBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: BoardAdapter
    private lateinit var recommendBoardAdapter: BoardAdapter
    private var popularContentId : Int = -1
    private lateinit var studyApiService: StudyApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHouseBinding.inflate(inflater, container, false)

        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

//        fetchLivePopularContent()

        binding.goPopularContentIv.setOnClickListener{
            val intent = Intent(requireContext(), CommunityContentActivity::class.java)
            intent.putExtra("postInfo", popularContentId.toString())
            startActivity(intent)
        }

        binding.popularContentTv.setOnClickListener {
            val intent = Intent(requireContext(), CommunityContentActivity::class.java)
            intent.putExtra("postInfo", popularContentId.toString())
            startActivity(intent)
        }

        interestBoardAdapter = BoardAdapter(
                        ArrayList(),
                onItemClick = { selectedItem ->
                    Log.d("HouseFragment", "이벤트 클릭: ${selectedItem.title}")
                    studyViewModel.setStudyData(
                        selectedItem.studyId,
                        selectedItem.imageUrl,
                        selectedItem.introduction
                    )

                    // Fragment 전환
                    val fragment = DetailStudyFragment()
                    parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onLikeClick = { selectedItem, likeButton ->
                toggleLikeStatus(selectedItem, likeButton)
            }
        )

        recommendBoardAdapter = BoardAdapter(
            ArrayList(),
            onItemClick = { selectedItem ->
                Log.d("HouseFragment", "이벤트 클릭: ${selectedItem.title}")
                studyViewModel.setStudyData(
                    selectedItem.studyId,
                    selectedItem.imageUrl,
                    selectedItem.introduction
                )

                // Fragment 전환
                val fragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onLikeClick = { selectedItem, likeButton ->
                toggleLikeStatus(selectedItem, likeButton)
            }
        )

        binding.rvBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }

        binding.rvBoard2.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        val memeberId = getMemberId(requireContext())

        fetchDataAnyWhere(memeberId) //관심 지역 스터디
        fetchRecommendStudy(memeberId) //추천 스터디



        binding.icFind.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }


        binding.icAlarm.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        val bundle = Bundle()
        val bundle2 = Bundle()
        val bundle3 = Bundle()
        val bundle4 = Bundle()

        val interestFragment = InterestFragment()
        val myInterestStudyFragment = MyInterestStudyFragment()
        val recruitingStudyFragment = RecruitingStudyFragment()
        val categoryFragment = CategoryFragment()
        interestFragment.arguments = bundle
        myInterestStudyFragment.arguments = bundle2
        recruitingStudyFragment.arguments = bundle3
        categoryFragment.arguments = bundle4

        binding.houseLocationCl.setOnClickListener{
            bundle.putString("source", "HouseFragment")
            //스터디 참여하기 팝업으로 이동

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.houseRecruitCl.setOnClickListener {
            bundle3.putString("source", "HouseFragment")

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, recruitingStudyFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.houseInterestCl.setOnClickListener {
            bundle2.putString("source", "HouseFragment")
            //스터디 참여하기 팝업으로 이동

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, myInterestStudyFragment)
                .addToBackStack(null)
                .commit()
        }


        binding.icGoInterest.setOnClickListener {
            val bundle4 = Bundle().apply {
                putInt("categoryType", 0)
            }
            val categoryFragment = CategoryFragment().apply {
                arguments = bundle4
            }
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, categoryFragment)
                .addToBackStack(null)
                .commit()
        }



        binding.houseCommunityCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityHomeFragment())
                .commitAllowingStateLoss()
            (activity as? MainActivity)?.isOnCommunityHome(CommunityHomeFragment())
        }

        return binding.root
    }

    private fun fetchDataAnyWhere(memberId: Int) {
        Log.d("HouseFragment", "fetchDataAnyWhere() 실행")
        val service = RetrofitInstance.retrofit.create(InterestAreaApiService::class.java)
        service.getInterestedBestStudies(memberId = memberId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map { study ->
                        BoardItem(
                            studyId = study.studyId,
                            title = study.title,
                            goal = study.goal,
                            introduction = study.introduction,
                            memberCount = study.memberCount,
                            heartCount = study.heartCount,
                            hitNum = study.hitNum,
                            maxPeople = study.maxPeople,
                            studyState = study.studyState,
                            themeTypes = study.themeTypes,
                            regions = study.regions,
                            imageUrl = study.imageUrl,
                            liked = study.liked
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        interestBoardAdapter.updateList(boardItems)
                        binding.rvBoard.visibility = View.VISIBLE
                    } else {
                        binding.rvBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("HouseFragment", "연결 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("HouseFragment", "API 호출 실패: ${t.message}")
            }
        })
    }


    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val memberId = getMemberId(requireContext())

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId).enqueue(object : Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    if (response.isSuccessful) {
                        val newStatus = response.body()?.result?.status
                        studyItem.liked = newStatus == "LIKE"
                        val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                        likeButton.setImageResource(newIcon)

                        // heartCount 즉시 증가 또는 감소
                        studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                        // 최신 데이터 동기화를 위해 fetchDataAnyWhere와 fetchRecommendStudy를 다시 호출
                        fetchDataAnyWhere(memberId)
                        fetchRecommendStudy(memberId)
                    } else {
                        Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
                        Log.d("studyid","${studyItem.studyId}")
                        Log.d("memberid", "${memberId}")
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

    private fun fetchLivePopularContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getBestCommunityContent("REAL_TIME")
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(
                    call: Call<CommunityResponse>,
                    response: Response<CommunityResponse>
                ) {
                    Log.d("LivePopularContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val popularResponse = response.body()
                        Log.d("LivePopularContent", "responseBody: ${popularResponse?.isSuccess}")
                        if (popularResponse?.isSuccess == "true") {
                            binding.popularContentTv.text = popularResponse.result.postBest5Responses[0].postTitle
                            popularContentId = popularResponse.result.postBest5Responses[0].postId
                        } else {
                            showError(popularResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
                    Log.e("LivePopularContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }


    private fun fetchRecommendStudy(memberId: Int) {
        Log.d("HouseFragment", "fetchRecommendStudy() 실행")
        val service = RetrofitInstance.retrofit.create(RecommendStudyApiService::class.java)
        service.GetRecommendStudy(memberId = memberId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map { study ->
                        BoardItem(
                            studyId = study.studyId,
                            title = study.title,
                            goal = study.goal,
                            introduction = study.introduction,
                            memberCount = study.memberCount,
                            heartCount = study.heartCount,
                            hitNum = study.hitNum,
                            maxPeople = study.maxPeople,
                            studyState = study.studyState,
                            themeTypes = study.themeTypes,
                            regions = study.regions,
                            imageUrl = study.imageUrl,
                            liked = study.liked
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.rvBoard2.visibility = View.VISIBLE
                    } else {
                        binding.rvBoard2.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("HouseFragment", "연결 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("HouseFragment", "API 호출 실패: ${t.message}")
            }
        })
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
}

