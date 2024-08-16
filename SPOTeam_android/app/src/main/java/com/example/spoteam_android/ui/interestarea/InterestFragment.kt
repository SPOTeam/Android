package com.example.spoteam_android.ui.interestarea

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.search.ApiResponse
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterestFragment : Fragment() {

    lateinit var binding: FragmentInterestBinding
    lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gender = arguments?.getString("gender")
        val minAge = arguments?.getString("minAge")
        val maxAge = arguments?.getString("maxAge")
        val activityFee = arguments?.getString("activityFee")
        val selectedStudyTheme = arguments?.getString("selectedStudyTheme")

        val activityFeeinput = arguments?.getString("activityFeeAmount")
        val activityFeeinput2 = activityFeeinput =="있음" // activityFeeinput이 있으면 true, 없으면 false
        val activityFeeAmount: String? = if (activityFeeinput2) activityFeeinput else null


        fetchDataAnyWhere("ALL")

        tabLayout = binding.tabs
        // TabLayout 리스너 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                // 탭이 선택되었을 때 해당 지역의 데이터 가져오기
                val selectedRegion = tab.text.toString()

                when (selectedRegion) {
                    "전체" -> fetchDataAnyWhere("ALL")
                    "서울 종로 \n사직동" -> {
                        if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                            Log.d("sagic 1","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                            if (activityFeeAmount != null) {
                                Log.d("sagic 2","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                                fetchDataPlusArea(
                                    "ALL",
                                    "1111053000",
                                    gender,
                                    minAge,
                                    maxAge,
                                    activityFee,
                                    activityFeeAmount,
                                    selectedStudyTheme
                                )
                            } else {
                                Log.d("sagic 3","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                                fetchDataPlusArea(
                                    "ALL",
                                    "1111053000",
                                    gender,
                                    minAge,
                                    maxAge,
                                    activityFee,
                                    "",
                                    selectedStudyTheme
                                )
                            }
                        }
                    }

                    "서울 종로 \n삼청동" -> {


                        if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                            Log.d("samchung1","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                            if (activityFeeAmount != null) {
                                Log.d("samchung2","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                                fetchDataPlusArea(
                                    "ALL",
                                    "1111054000",
                                    gender,
                                    minAge,
                                    maxAge,
                                    activityFee,
                                    activityFeeAmount,
                                    selectedStudyTheme
                                )
                            } else {
                                // activityFeeAmount가 null일 때의 처리
                                Log.d("samchung3","$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount")
                                fetchDataPlusArea(
                                    "ALL",
                                    "1111054000",
                                    gender,
                                    minAge,
                                    maxAge,
                                    activityFee,
                                    "",
                                    selectedStudyTheme
                                )
                            }
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("InterestFragment","탭 재선택")
            }
        })



        val spinner: Spinner = binding.filterToggle


        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val source = arguments?.getString("source")
                when (source) {
                    "AnyWhere" -> {
                        // HouseFragment에서 접근했을 때의 API 처리
                        when (position) {
                            0 -> fetchDataAnyWhere("ALL")
                            1 -> fetchDataAnyWhere("RECRUITING")
                            2 -> fetchDataAnyWhere("COMPLETED")
                            3 -> fetchDataAnyWhere("HIT")
                            4 -> fetchDataAnyWhere("LIKED")
                        }
                    }

                    "InterestFilterFragment" -> {

                        when (position) {
                            0 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "ALL",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "0", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }
                            }

                            1 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "RECRUITING",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "0", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }

                            2 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "COMPLETED",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "0", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }
                            3 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "HIT",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "0", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }
                            4 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "LIKED",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "0", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
        }

        val filter: ImageView = binding.icFilter
        filter.setOnClickListener{
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }
    }

    private fun fetchDataAnyWhere(selectedItem: String) {
        Log.d("InterestFragment","fetchDataAnyWhere()실행")

        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        RetrofitClient.IaapiService.InterestArea(
            authToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MiwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMzkxNTA5LCJleHAiOjE3MjM0Nzc5MDl9.eOQiy5-na976YF3SXQoPfFPEDcreNQ8AT_dHTB8oYAA" ,
            memberId = 2,
            page = 0,
            size = 3,
            sortBy = selectedItem,
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = true,
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
                        Log.d("InterestFragment","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
                                    studyName = study.title,  // title을 studyName으로 사용
                                    studyObject = study.introduction,  // introduction을 studyObject로 사용
                                    studyTO = study.memberCount,
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
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            checkcount.text = "00 건"
                            interest_area_board.visibility = View.GONE
                            Toast.makeText(requireContext() ,"1. 조건에 맞는 항목이 없습니다.",Toast.LENGTH_SHORT).show()
                            Log.d("InterestFragment","1. isSuccess == False")
                        }
                    }
                    else{
                        Log.d("InterestFragment","1. 연결 실패")
                        Log.d("InterestingFragment", "{$response}")
                        Log.e("InterestingFragment", "Error body: ${response.errorBody()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d("InterestFragment","API 호출 실패")
                }
            })
    }

    private fun fetchDataAnyWhere2(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String) {

        Log.d("InterestFragment","fetchDataAnyWhere2()실행")
        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount

        Log.d("InterestFragment","fetchDataAnyWhere2()실행")

        RetrofitClient.IaapiService.InterestArea(
            authToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MiwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMzkxNTA5LCJleHAiOjE3MjM0Nzc5MDl9.eOQiy5-na976YF3SXQoPfFPEDcreNQ8AT_dHTB8oYAA" ,
            memberId = 2,
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = true,
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 3,
            sortBy = selectedItem,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
                        Log.d("InterestFragment","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
                                    studyName = study.title,  // title을 studyName으로 사용
                                    studyObject = study.introduction,  // introduction을 studyObject로 사용
                                    studyTO = study.memberCount,
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
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            checkcount.text = "00 건"
                            interest_area_board.visibility = View.GONE
                        }
                    }
                    else{
                        val errorBody = response.errorBody()?.string()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }

    private fun fetchDataPlusArea(selectedItem: String,regionCode: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String) {

        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount

        Log.d("InterestFragment","fetchDataPlusArea()실행")
        RetrofitClient.IaSapiService.InterestSpecificArea(
            authToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MiwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMzkxNTA5LCJleHAiOjE3MjM0Nzc5MDl9.eOQiy5-na976YF3SXQoPfFPEDcreNQ8AT_dHTB8oYAA" ,
            regionCode = regionCode,
            memberId = 2,
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = true,
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 3,
            sortBy = selectedItem,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
                        Log.d("InterestFragment","$response")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
                                    studyName = study.title,  // title을 studyName으로 사용
                                    studyObject = study.introduction,  // introduction을 studyObject로 사용
                                    studyTO = study.memberCount,
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
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            checkcount.text = "00 건"
                            interest_area_board.visibility = View.GONE
                            Toast.makeText(requireContext() ,"2. 조건에 맞는 항목이 없습니다.",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val errorBody = response.errorBody()?.string()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }
            })
    }
}

//                            val pagesResponse = response.body()
//                            Log.d("InterestingFragment", "$response")
//                            if (pagesResponse?.isSuccess == true) {
//                                val pagesResponseList = pagesResponse?.result?.content
//                                Log.d("InterestingFragment", "items: $pagesResponse")
//                                if (pagesResponseList != null) {
//                                    Log.d("InterestingFragment", "items: $pagesResponseList")
//                                }
//                                //}
//                                else {
//                                    Log.d("InterestingFragment", "이게 찍히나?")
//                                }
//                            } else {
//                                Log.d("InterestingFragment", "{$response}")
//                                // 실패한 경우 서버로부터의 응답 코드를 로그로 출력
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Failed to fetch studies: ${response.code()} - ${response.message()}"
//                                )
//
//                                // 응답 본문을 문자열로 변환하여 로그로 출력
//                                val errorBody = response.errorBody()?.string()
//                                if (errorBody != null) {
//                                    Log.e("InterestingFragment", "Error body: $errorBody")
//                                } else {
//                                    Log.e("InterestingFragment", "Error body is null")
//                                }
//
//                                // 추가 디버깅 정보 출력
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request URL: ${response.raw().request.url}"
//                                )
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request Method: ${response.raw().request.method}"
//                                )
//                                Log.e(
//                                    "InterestingFragment",
//                                    "Request Headers: ${response.raw().request.headers}"
//                                )
//                            }
//
//                        }
//                    }
//                }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//        })


    // memberId를 가져오는 메서드
//    fun getMemberId(context: Context): Int {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        return sharedPreferences.getInt("memberId", -1) // 저장된 memberId 없을 시 기본값 -1 반환
//    }



