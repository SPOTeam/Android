package com.example.spoteam_android

import RetrofitClient.getAuthToken
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentHouseBinding
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.calendar.CalendarFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestFragment
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class HouseFragment : Fragment() {

    lateinit var binding: FragmentHouseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHouseBinding.inflate(inflater, container, false)

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


        binding.imgbtnUnion.setOnClickListener{
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, RecruitingStudyFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.imgbtnJjim.setOnClickListener{
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
                .replace(R.id.main_frm, CalendarFragment())
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

        Log.d("HouseFragment","fetchDataAnyWhere()실행")

        val interest_area_board = binding.rvBoard
        val boardItems = arrayListOf<BoardItem>()

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
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
                        Log.d("HouseFragment","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val studyId = study.studyId
                                val boardItem = BoardItem(
                                    studyId = study.studyId,
                                    studyName = study.title,  // title을 studyName으로 사용
                                    studyObject = study.introduction,  // introduction을 studyObject로 사용
                                    studyTO = study.maxPeople,
                                    studyPO = study.memberCount,
                                    like = study.heartCount,
                                    watch = study.hitNum,
                                )
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems)
                                boardAdapter.notifyDataSetChanged()
                                interest_area_board.visibility = View.VISIBLE
                                interest_area_board.adapter = boardAdapter
                                interest_area_board.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            }
                        }
                        else{
                            interest_area_board.visibility = View.GONE
                            Toast.makeText(requireContext() ,"1. 조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("HouseFragment","1. isSuccess == False")
                        }
                    }
                    else{
                        Log.d("HouseFragment","1. 연결 실패")
                        Log.d("HouseFragment", "{$response}")
                        Log.e("HouseFragment", "Error body: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("HouseFragment","API 호출 실패")
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

    private fun fetchRecommendStudy(memberId: Int) {

        Log.d("HouseFragment","fetchRecommendStudy()실행")

        val recommend_study_board = binding.rvBoard2
        val boardItems = arrayListOf<BoardItem>()

        RetrofitClient.GetRSService.GetRecommendStudy(
            authToken = getAuthToken(),
            memberId = memberId,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
                        Log.d("HouseFragment","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
                                    studyId = study.studyId,
                                    studyName = study.title,  // title을 studyName으로 사용
                                    studyObject = study.introduction,  // introduction을 studyObject로 사용
                                    studyTO = study.maxPeople,
                                    studyPO = study.memberCount,
                                    like = study.heartCount,
                                    watch = study.hitNum,
                                )
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems)
                                boardAdapter.notifyDataSetChanged()
                                recommend_study_board.visibility = View.VISIBLE
                                recommend_study_board.adapter = boardAdapter
                                recommend_study_board.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            }
                        }
                        else{
                            recommend_study_board.visibility = View.GONE
                            Toast.makeText(requireContext() ,"1. 조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                            Log.d("HouseFragment","1. isSuccess == False")
                        }
                    }
                    else{
                        Log.d("HouseFragment","1. 연결 실패")
                        Log.d("HouseFragment", "{$response}")
                        Log.e("HouseFragment", "Error body: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("HouseFragment","API 호출 실패")
                }
            })
    }

}
