package com.example.spoteam_android

import RetrofitClient.getAuthToken
import StudyViewModel
import android.content.Context
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
import com.example.spoteam_android.search.SearchAdapter
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestFragment
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HouseFragment : Fragment() {

    lateinit var binding: FragmentHouseBinding
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: BoardAdapter
    private lateinit var recommendBoardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHouseBinding.inflate(inflater, container, false)

        interestBoardAdapter = BoardAdapter(ArrayList()) { selectedItem ->
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
        }

        recommendBoardAdapter = BoardAdapter(ArrayList()) { selectedItem ->
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
        }

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


        val icFindButton: ImageView = binding.root.findViewById(R.id.ic_find)
        icFindButton.setOnClickListener {
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(SearchFragment())
        }

        binding.icAlarm.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        val bundle = Bundle()
        val interestFragment = InterestFragment()
        interestFragment.arguments = bundle


        binding.imgbtnUnion.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RecruitingStudyFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.imgbtnJjim.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, MyInterestStudyFragment())
                .addToBackStack(null)
                .commit()
        }

        val txintereststudy: TextView = binding.root.findViewById(R.id.tx_interest_study)
        txintereststudy.setOnClickListener {
            bundle.putString("source", "HouseFragment")
            //스터디 참여하기 팝업으로 이동

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commit()
        }

        val icgointerest: ImageView = binding.root.findViewById(R.id.ic_go_interest)
        icgointerest.setOnClickListener {
            bundle.putString("source", "HouseFragment")
            //스터디 참여하기 팝업으로 이동
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
                .addToBackStack(null)
                .commit()
        }


        binding.imgbtnBoard.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityHomeFragment())
                .commitAllowingStateLoss()
            (activity as? MainActivity)?.isOnCommunityHome(CommunityHomeFragment())
        }

        return binding.root
    }

    private fun fetchDataAnyWhere(memberId: Int) {
        Log.d("HouseFragment", "fetchDataAnyWhere() 실행")

        RetrofitClient.IaapiService.InterestArea(
            authToken = getAuthToken(),
            memberId = memberId,
            page = 0,
            size = 3,
            sortBy = "ALL",
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = false,
            hasFee = false,
            fee = null
        ).enqueue(object : Callback<ApiResponse> {
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
                            imageUrl = study.imageUrl
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

    private fun fetchRecommendStudy(memberId: Int) {
        Log.d("HouseFragment", "fetchRecommendStudy() 실행")

        RetrofitClient.GetRSService.GetRecommendStudy(
            authToken = getAuthToken(),
            memberId = memberId,
        ).enqueue(object : Callback<ApiResponse> {
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
                            imageUrl = study.imageUrl
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


