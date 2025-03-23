package com.example.spoteam_android

import StudyApiService
import StudyViewModel
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.FragmentHouseBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.CommunityResponse
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestFragment
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyHomeFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.example.spoteam_android.weather.WeatherViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class HouseFragment : Fragment() {

    lateinit var binding: FragmentHouseBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private lateinit var recommendBoardAdapter: InterestVPAdapter
    private var popularContentId : Int = -1
    private lateinit var studyApiService: StudyApiService
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var presentTemperature: TextView


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.let {
            it.findViewById<FloatingActionButton>(R.id.add_study_btn)?.visibility = View.VISIBLE
            it.findViewById<FloatingActionButton>(R.id.add_study_btn)?.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)))
            it.findViewById<FloatingActionButton>(R.id.add_study_btn)?.setOnClickListener {
                val fragment = RegisterStudyFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.let {
            it.findViewById<FloatingActionButton>(R.id.add_study_btn)?.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHouseBinding.inflate(inflater, container, false)
        val activity = requireActivity() as? MainActivity

        //날씨 배경 정의
        val backgroundRes = activity?.getWeatherBackground()

        // 🟢 기본값은 ic_weather_background로 설정되어 있으므로 밤일 경우에만 업데이트
        if (backgroundRes == R.drawable.ic_weather_night_background) {
            binding.icWeatherBackground.setImageResource(backgroundRes)
        }




        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        presentTemperature = binding.txTemperature
        fetchLivePopularContent()

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


        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        },studyViewModel = studyViewModel)

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



        recommendBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
        toggleLikeStatus(selectedItem, likeButton)
        },studyViewModel = studyViewModel)

        recommendBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val fragment = DetailStudyFragment()
                fetchStudyMembers(data.studyId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        binding.rvBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }

        binding.rvBoard2.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        fetchDataAnyWhere() //관심 지역 스터디
        fetchRecommendStudy() //추천 스터디

        binding.icFind.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, SearchFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(HomeFragment())
            }
        }


        binding.icAlarm.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, AlertFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(HomeFragment())
            }
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


        binding.houseLocationCl.setOnClickListener {
            bundle.putString("source", "HouseFragment")
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, interestFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(interestFragment)
            }
        }

        binding.houseRecruitCl.setOnClickListener {
            bundle.putString("source", "HouseFragment")
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, recruitingStudyFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(recruitingStudyFragment)
            }
        }

        binding.houseInterestCl.setOnClickListener {
            bundle2.putString("source", "HouseFragment")
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, myInterestStudyFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(myInterestStudyFragment)
            }
        }

        binding.icGoInterest.setOnClickListener {
            val bundle4 = Bundle().apply {
                putInt("categoryType", 0)
            }
            val categoryFragment = CategoryFragment().apply {
                arguments = bundle4
            }
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, categoryFragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(categoryFragment)
            }
        }

        binding.houseCommunityCl.setOnClickListener {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CommunityHomeFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()

                it.isOnCommunityHome(CommunityHomeFragment())
            }
        }

//        binding.icRecommendationRefresh.setOnClickListener {
//            fetchRecommendStudy()
//        }

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        weatherViewModel.weatherResponse.observe(viewLifecycleOwner) { response ->
            Log.d("HouseFragment", "Observer triggered")

            if (response.isSuccessful) {
                val responseBody = response.body()?.response
                Log.d("HouseFragment", "Response: ${response.body()}")
                if (responseBody?.body != null) {
                    val items = responseBody.body.items
                    if (!items?.item.isNullOrEmpty()) {
                        Log.d("HouseFragment", "Weather data received: ${items.item.size} items")

                        val tmpItem = items.item.find { it.category.trim() == "TMP" }
                        val tmnItem = items.item.find { it.category.trim() == "TMN" }
                        val pcpItem = items.item.find { it.category.trim() == "PCP" }
                        val snoItem = items.item.find { it.category.trim() == "SNO" }
                        val wsdItem = items.item.find { it.category.trim() == "WSD" }

                        if (tmpItem != null || pcpItem != null || snoItem != null || wsdItem != null || tmnItem != null) {
                            val temperature = tmpItem?.fcstValue?.toDoubleOrNull() ?: 0.0
                            val minTemperature = tmnItem?.fcstValue?.toDoubleOrNull() ?: 0.0
                            val precipitation = parseDouble(pcpItem?.fcstValue ?: "-")
                            val snowfall = parseDouble(snoItem?.fcstValue ?: "-")
                            val windSpeed = wsdItem?.fcstValue?.toDoubleOrNull() ?: 0.0

                            presentTemperature.text = String.format("%.1f °C", temperature)

                            // 날씨에 맞는 메시지 & 이미지 가져오기
                            val (weatherMessage, weatherImage) = getWeatherInfo(precipitation, snowfall, temperature, minTemperature, windSpeed)

                            // UI 업데이트
                            binding.txExplainWeather.text = weatherMessage
                            binding.icSun.setImageResource(weatherImage)
                        } else {
                            Log.e("HouseFragment", "TMP, TMN, PCP, SNO, WSD 데이터 중 일부가 누락됨")
                        }
                    } else {
                        Log.e("HouseFragment", "날씨 데이터가 비어 있음")
                    }
                } else {
                    Log.e("HouseFragment", "응답 body가 null")
                }
            } else {
                Log.e("HouseFragment", "Weather API 응답 실패: ${response.errorBody()} ")
            }
        }


        return binding.root
    }

    // 날씨 상태에 따른 메시지와 이미지 설정 함수
    fun getWeatherInfo(pcp: Double, sno: Double, tmp: Double, tmn: Double, wsd: Double): Pair<String, Int> {
        return when {
            pcp >= 10 -> "실내에서 집중! 목표는 선명히!" to R.drawable.ic_rainy // 강한 비
            sno >= 5 -> "눈길 조심! 한 걸음씩 나아가요!" to R.drawable.ic_snow // 폭설
            wsd >= 9 -> "바람 조심! 흔들려도 전진!" to R.drawable.ic_gale // 강풍
            tmn <= 0 || (tmp in 0.0..10.0) -> "따뜻하게! 오늘도 열정 가득!" to R.drawable.ic_cold // 추운 날씨
            tmp >= 25 -> "수분 보충! 더위도 이겨내요!" to R.drawable.ic_hot // 더운 날씨
            pcp in 1.0..4.0 -> "우산 챙기고 오늘도 파이팅!" to R.drawable.ic_light_rainy // 약한 비
            pcp == 0.0 && tmp in 10.0..25.0 -> "좋은 날! 목표 향해 달려요!" to R.drawable.ic_sun // 맑고 쾌적한 날씨
            else -> "오늘도 힘내세요!" to R.drawable.ic_sun // 기본 메시지
        }
    }


    fun parseDouble(value: String): Double {
        return when (value) {
            "-", "강수없음", "적설없음" -> 0.0
            else -> value.toDoubleOrNull() ?: 0.0
        }
    }


    private fun fetchDataAnyWhere() {
        val service = RetrofitInstance.retrofit.create(InterestAreaApiService::class.java)
        service.getInterestedBestStudies().enqueue(object : Callback<ApiResponse> {
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
                            liked = study.liked,
                            isHost = false
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
            studyApiService.toggleStudyLike(studyItem.studyId).enqueue(object : Callback<LikeResponse> {
                override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                    if (response.isSuccessful) {
                        val newStatus = response.body()?.result?.status
                        studyItem.liked = newStatus == "LIKE"
                        val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                        likeButton.setImageResource(newIcon)

                        // heartCount 즉시 증가 또는 감소
                        studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                        // 최신 데이터 동기화를 위해 fetchDataAnyWhere와 fetchRecommendStudy를 다시 호출
                        fetchDataAnyWhere()
                        fetchRecommendStudy()
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

    private fun fetchLivePopularContent() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getBestCommunityContent("REAL_TIME")
            .enqueue(object : Callback<CommunityResponse> {
                override fun onResponse(
                    call: Call<CommunityResponse>,
                    response: Response<CommunityResponse>
                ) {
                    if (response.isSuccessful) {
                        val popularResponse = response.body()
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


    private fun fetchRecommendStudy() {
        val service = RetrofitInstance.retrofit.create(RecommendStudyApiService::class.java)
        service.GetRecommendStudy().enqueue(object : Callback<ApiResponse> {
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
                            liked = study.liked,
                            isHost = false
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

    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentMemberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result?.members ?: emptyList()
                        members.forEach { member ->
                            Log.d("StudyMembers", "Member ID: ${member.memberId}, Nickname: ${member.nickname}, Profile Image: ${member.profileImage ?: "NULL"}")
                        }


                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

