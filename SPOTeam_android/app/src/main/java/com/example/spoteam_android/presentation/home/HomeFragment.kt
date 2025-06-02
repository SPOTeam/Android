package com.example.spoteam_android.presentation.home
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentHomeBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.presentation.alert.AlertFragment
import com.example.spoteam_android.presentation.category.CategoryFragment_1
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.community.CommunityHomeFragment
import com.example.spoteam_android.presentation.community.CommunityResponse
import com.example.spoteam_android.presentation.interestarea.ApiResponse
import com.example.spoteam_android.presentation.interestarea.InterestAreaApiService
import com.example.spoteam_android.presentation.interestarea.InterestFilterViewModel
import com.example.spoteam_android.presentation.interestarea.InterestFragment
import com.example.spoteam_android.presentation.interestarea.InterestVPAdapter
import com.example.spoteam_android.presentation.interestarea.RecommendStudyApiService
import com.example.spoteam_android.presentation.myinterest.MyInterestStudyFragment
import com.example.spoteam_android.presentation.recruiting.RecruitingStudyFragment
import com.example.spoteam_android.presentation.search.SearchFragment
import com.example.spoteam_android.presentation.study.DetailStudyFragment
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.example.spoteam_android.presentation.study.register.theme.ThemeStudyFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject lateinit var interestAreaApiService: InterestAreaApiService
    @Inject lateinit var recommendStudyApiService: RecommendStudyApiService
    @Inject lateinit var communityAPIService: CommunityAPIService

    private lateinit var binding: FragmentHomeBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private val categoryViewModel: com.example.spoteam_android.presentation.category.CategoryInterestViewModel by activityViewModels()
    private val viewModel: InterestFilterViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var interestBoardAdapter: InterestVPAdapter
    private lateinit var recommendBoardAdapter: InterestVPAdapter

    private var popularContentId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val activity = requireActivity() as MainActivity

        // 날씨 배경 설정
        val backgroundRes = activity.getWeatherBackground()
        if (backgroundRes == R.drawable.ic_weather_night_background) {
            binding.icWeatherBackground.setImageResource(backgroundRes)
        }

        setupFab()
        fetchLivePopularContent()
        setupBoardAdapters()
        setupObservers()

        fetchDataMostPopular()
        fetchRecommendStudy()

        setupClickListeners(activity)

        return binding.root
    }

    private fun setupFab() {
        (activity as? MainActivity)?.let {
            it.findViewById<FloatingActionButton>(R.id.add_study_btn)?.apply {
                visibility = View.VISIBLE
                setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
                setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, ThemeStudyFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun setupBoardAdapters() {
        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { item, likeButton ->
            homeViewModel.toggleLike(item.studyId)
        }, studyViewModel)

        recommendBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { item, likeButton ->
            homeViewModel.toggleLike(item.studyId)
        }, studyViewModel)

        recommendBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                homeViewModel.fetchStudyMembers(data.studyId)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, DetailStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.rvBoard.layoutManager = LinearLayoutManager(context)
        binding.rvBoard.adapter = interestBoardAdapter

        binding.rvBoard2.layoutManager = LinearLayoutManager(context)
        binding.rvBoard2.adapter = recommendBoardAdapter
    }

    private fun setupObservers() {
        homeViewModel.likeToggleResult.observe(viewLifecycleOwner) { (studyId, isLiked) ->
            val list =
                interestBoardAdapter.getCurrentList() + recommendBoardAdapter.getCurrentList()
            list.find { it.studyId == studyId }?.let {
                it.liked = isLiked
                it.heartCount += if (isLiked) 1 else -1
                fetchDataMostPopular()
                fetchRecommendStudy()
            }
        }

        homeViewModel.studyMembers.observe(viewLifecycleOwner) { members ->
            members.forEach {
                Log.d("StudyMembers", "ID: ${it.memberId}, Nickname: ${it.nickname}")
            }
        }
    }

    private fun fetchDataMostPopular() {
        interestAreaApiService.getInterestedBestStudies().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map {
                        BoardItem(
                            studyId = it.studyId,
                            title = it.title,
                            goal = it.goal,
                            introduction = it.introduction,
                            memberCount = it.memberCount,
                            heartCount = it.heartCount,
                            hitNum = it.hitNum,
                            maxPeople = it.maxPeople,
                            studyState = it.studyState,
                            themeTypes = it.themeTypes,
                            regions = it.regions,
                            imageUrl = it.imageUrl,
                            liked = it.liked,
                            isHost = false
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        interestBoardAdapter.updateList(boardItems)
                        binding.rvBoard.visibility = View.VISIBLE
                    } else {
                        binding.rvBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }

    private fun fetchRecommendStudy() {
        recommendStudyApiService.GetRecommendStudy().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val boardItems = response.body()?.result?.content?.map {
                        BoardItem(
                            studyId = it.studyId,
                            title = it.title,
                            goal = it.goal,
                            introduction = it.introduction,
                            memberCount = it.memberCount,
                            heartCount = it.heartCount,
                            hitNum = it.hitNum,
                            maxPeople = it.maxPeople,
                            studyState = it.studyState,
                            themeTypes = it.themeTypes,
                            regions = it.regions,
                            imageUrl = it.imageUrl,
                            liked = it.liked,
                            isHost = false
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.rvBoard2.visibility = View.VISIBLE
                    } else {
                        binding.rvBoard2.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {}
        })
    }

    private fun fetchLivePopularContent() {
        communityAPIService.getBestCommunityContent("REAL_TIME")
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(call: Call<CommunityResponse>, response: Response<CommunityResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.result?.postBest5Responses?.firstOrNull()?.let {
                            binding.popularContentTv.text = it.postTitle
                            popularContentId = it.postId
                        } ?: run {
                            binding.popularContentTv.text = "인기 게시글이 없습니다"
                            popularContentId = -1
                        }
                    }
                }

                override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {}
            })
    }

    private fun setupClickListeners(activity: MainActivity) {
        binding.icFind.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarm.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.houseLocationCl.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "HouseFragment") }
            val interestFragment = InterestFragment().apply { arguments = bundle }
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.houseRecruitCl.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "HouseFragment") }
            val recruitingFragment = RecruitingStudyFragment().apply { arguments = bundle }
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, recruitingFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.houseInterestCl.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "HouseFragment") }
            val myInterestFragment = MyInterestStudyFragment().apply { arguments = bundle }
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, myInterestFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icGoInterest.setOnClickListener {
            val categoryFragment = CategoryFragment_1().apply {
                arguments = Bundle().apply { putInt("categoryType", 0) }
            }
            categoryViewModel.reset()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, categoryFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.houseCommunityCl.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityHomeFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icRecommendationRefresh.setOnClickListener {
            fetchRecommendStudy()
        }
    }
}
