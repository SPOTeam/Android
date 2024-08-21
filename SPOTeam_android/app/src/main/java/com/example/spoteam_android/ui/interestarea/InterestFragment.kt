package com.example.spoteam_android.ui.interestarea

import RetrofitClient.getAuthToken
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestBinding
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

    @SuppressLint("ResourceType")
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

        tabLayout = binding.tabs

        val memberId = getMemberId(requireContext())

        val source = arguments?.getString("source")
        Log.d("My","$source")
        when (source) {
            "HouseFragment" -> {
                binding.icFilterActive.visibility = View.GONE

                Log.d("My","HouseFragment에서 진입")

                fetchDataAnyWhere("ALL",memberId)

                fetchDataGetInterestArea(memberId) { regions ->
                    if (regions != null) {
                        val allTab = tabLayout.newTab()
                        val customView =
                            LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                        val allTextView = customView.findViewById<TextView>(R.id.tabText)
                        allTextView.text = "전체"
                        allTab.customView = customView
                        allTab.tag = "0000000000" // "전체" 탭의 tag 설정
                        tabLayout.addTab(allTab)

                        regions.forEach { region ->
                            val tab = tabLayout.newTab()

                            val customView =
                                LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                            val textView = customView.findViewById<TextView>(R.id.tabText)
                            val tabText =
                                "${region.province}\n${region.district}\n${region.neighborhood}"

                            textView.text = tabText
                            tab.customView = customView
                            tab.tag = region.code // 각 탭에 해당 region.code를 태그로 저장
                            tabLayout.addTab(tab)
                        }

                        tabLayout.addOnTabSelectedListener(object :
                            TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                // 선택된 탭의 텍스트 색상을 파란색으로 설정
                                val textView = tab.customView?.findViewById<TextView>(R.id.tabText)
                                textView?.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.button_enabled_text
                                    )
                                ) // 파란색으로 변경

                                // 선택된 탭의 region.code를 가져옴
                                val selectedRegionCode = tab.tag as String

                                Log.d("selectedRegionCode", selectedRegionCode)

                                // API 호출
                                if (selectedRegionCode == "0000000000") {
                                    fetchDataAnyWhere("ALL",memberId)
                                } else {
                                    fetchDataPlusArea(
                                        "ALL",
                                        memberId,
                                        selectedRegionCode,
                                        "MALE",
                                        "18",
                                        "60",
                                        "false",
                                        "",
                                        "어학"
                                    )
                                }
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {
                                val textView = tab?.customView?.findViewById<TextView>(R.id.tabText)
                                textView?.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black))
                            }

                            override fun onTabReselected(tab: TabLayout.Tab?) {
                            }
                        })
                    }
                }
            }


            "InterestFilterFragment" -> {

                binding.icFilterActive.visibility = View.VISIBLE
                Log.d("My","InterestFilterFragment에서 진입")

                if (gender != null  && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                    if (activityFeeAmount != null){
                        fetchDataAnyWhere2(
                            "ALL",
                            memberId,
                            gender,
                            minAge,
                            maxAge,
                            activityFee,
                            activityFeeAmount,
                            selectedStudyTheme)
                        }else{
                        fetchDataAnyWhere2(
                            "ALL",
                            memberId,
                            gender,
                            minAge,
                            maxAge,
                            activityFee,
                            "",
                            selectedStudyTheme)
                    }
                }


                fetchDataGetInterestArea(memberId) { regions ->
                    if (regions != null) {
                        val allTab = tabLayout.newTab()
                        val customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                        val allTextView = customView.findViewById<TextView>(R.id.tabText)
                        allTextView.text = "전체"
                        allTab.customView = customView
                        allTab.tag = "0000000000" // "전체" 탭의 tag 설정
                        tabLayout.addTab(allTab)

                        regions.forEach { region ->
                            val tab = tabLayout.newTab()

                            val customView =
                                LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                            val textView = customView.findViewById<TextView>(R.id.tabText)
                            val tabText = "${region.province}\n${region.district}\n${region.neighborhood}"

                            textView.text = tabText
                            tab.customView = customView
                            tab.tag = region.code // 각 탭에 해당 region.code를 태그로 저장
                            tabLayout.addTab(tab)
                        }

                        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab) {
                                // 선택된 탭의 텍스트 색상을 파란색으로 설정
                                val textView = tab.customView?.findViewById<TextView>(R.id.tabText)
                                textView?.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.button_enabled_text
                                    )
                                ) // 파란색으로 변경

                                // 선택된 탭의 region.code를 가져옴
                                val selectedRegionCode = tab.tag as String

                                Log.d("selectedRegionCode",selectedRegionCode)

                                // API 호출
                                if (selectedRegionCode == "0000000000") {
                                    if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                        if (activityFeeAmount != null){
                                            fetchDataAnyWhere2(
                                                "ALL",
                                                memberId,
                                                gender,
                                                minAge,
                                                maxAge,
                                                activityFee,
                                                activityFeeAmount,
                                                selectedStudyTheme
                                            )
                                        }else{
                                            fetchDataAnyWhere2(
                                                "ALL",
                                                memberId,
                                                gender,
                                                minAge,
                                                maxAge,
                                                activityFee,
                                                "",
                                                selectedStudyTheme
                                            )
                                        }
                                    }
                                } else {
                                    if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                        Log.d(
                                            "sagic 1",
                                            "$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount"
                                        )
                                        if (activityFeeAmount != null) {
                                            Log.d("Selected Region Number", selectedRegionCode)
                                            Log.d("Selected Region", textView?.text.toString())
                                            fetchDataPlusArea(
                                                "ALL",
                                                memberId,
                                                selectedRegionCode,
                                                gender,
                                                minAge,
                                                maxAge,
                                                activityFee,
                                                activityFeeAmount,
                                                selectedStudyTheme
                                            )
                                        } else {
                                            Log.d(
                                                "sagic 3",
                                                "$gender $minAge $maxAge $activityFee $selectedStudyTheme $activityFeeAmount"
                                            )
                                            Log.d("Selected Region Number", selectedRegionCode)
                                            Log.d("Selected Region", textView?.text.toString())
                                            fetchDataPlusArea(
                                                "ALL",
                                                memberId,
                                                selectedRegionCode,
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

                            override fun onTabUnselected(tab: TabLayout.Tab) {
                                // 선택되지 않은 탭의 텍스트 색상을 검정색으로 설정
                                val textView = tab.customView?.findViewById<TextView>(R.id.tabText)
                                textView?.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black
                                    )
                                ) // 검정색으로 변경
                            }

                            override fun onTabReselected(tab: TabLayout.Tab) {
                                // 이미 선택된 탭을 다시 선택할 때 수행할 작업이 있으면 여기에 작성
                            }
                        })
                    }
                }
            }
        }



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
                    "HouseFragment" -> {
                        // HouseFragment에서 접근했을 때의 API 처리
                        when (position) {
                            0 -> fetchDataAnyWhere("ALL",memberId)
                            1 -> fetchDataAnyWhere("RECRUITING",memberId)
                            2 -> fetchDataAnyWhere("COMPLETED",memberId)
                            3 -> fetchDataAnyWhere("HIT",memberId)
                            4 -> fetchDataAnyWhere("LIKED",memberId)
                        }
                    }

                    "InterestFilterFragment" -> {

                        when (position) {
                            0 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "ALL",
                                        memberId,
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }
                            }

                            1 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "RECRUITING",
                                        memberId,
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }

                            2 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "COMPLETED",
                                        memberId,
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
                                        memberId,
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "", // null이면 기본값 "0"으로 대체
                                        selectedStudyTheme
                                    )
                                }

                            }
                            4 -> {
                                if (gender != null && minAge != null && maxAge != null && activityFee != null && selectedStudyTheme != null) {
                                    fetchDataAnyWhere2(
                                        "LIKED",
                                        memberId,
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee,
                                        activityFeeAmount ?: "", // null이면 기본값 "0"으로 대체
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

    private fun fetchDataAnyWhere(selectedItem: String,memberId: Int,) {

        Log.d("InterestFragment","fetchDataAnyWhere()실행")

        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        RetrofitClient.IaapiService.InterestArea(
            authToken = getAuthToken(),
            memberId = memberId,
            page = 0,
            size = 10,
            sortBy = selectedItem,
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
                        Log.d("InterestFragment","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
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
                                    imageUrl = study.imageUrl)
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems){selectedItem ->}
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

    private fun fetchDataAnyWhere2(selectedItem: String, memberId: Int, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String) {

        Log.d("InterestFragment","fetchDataAnyWhere2()실행")
        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount

        Log.d("InterestFragment","fetchDataAnyWhere2()실행")
        Log.d("InterestFragment",selectedItem)

        RetrofitClient.IaapiService.InterestArea(
            authToken = getAuthToken(),
            memberId = memberId,
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = false,
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 10,
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
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems){selectedItem ->}
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

    private fun fetchDataPlusArea(selectedItem: String, memberId: Int, regionCode: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String) {

        val interest_area_board = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount

        Log.d("InterestFragment","fetchDataPlusArea()실행")
        Log.d("Me",regionCode)

        RetrofitClient.IaSapiService.InterestSpecificArea(
            authToken = getAuthToken(),
            regionCode = regionCode,
            memberId = 10,
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = false,
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 10,
            sortBy = selectedItem,
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("My","$response")
                        boardItems.clear()
                        val apiResponse = response.body()
                        Log.d("My","$apiResponse")
                        if (apiResponse?.isSuccess == true) {
                            apiResponse?.result?.content?.forEach { study ->
                                val boardItem = BoardItem(
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
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems){selectedItem ->}
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

    private fun fetchDataGetInterestArea(memberId: Int, callback: (List<Region>?) -> Unit) {

        Log.d("InterestFragment", "fetchDataGetInterestArea() 실행")

        RetrofitClient.GetIaService.GetInterestArea(
            authToken = getAuthToken(),
            memberId = memberId,
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("InterestFragment", "응답 성공: $response")

                    apiResponse?.let {
                        if (it.isSuccess) {
                            val regions = it.result?.regions
                            Log.d("InterestFragment1", "회원 ID: ${it.result?.memberId}")
                            regions?.forEach { region ->
                                Log.d("InterestFragment", "지역 정보: ${region.province} ${region.district} ${region.neighborhood} ${region.code}")
                            }
                            // 콜백을 통해 regions 반환
                            callback(regions)
                        } else {
                            Log.e("InterestFragment", "API 응답 실패: ${it.message}")
                            callback(null)
                        }
                    } ?: run {
                        Log.e("InterestFragment2", "응답 본문이 null입니다.")
                        callback(null)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("InterestFragment3", "응답 오류: $errorBody")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("InterestFragment", "네트워크 오류", t)
                callback(null)
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

        Log.d("memberId3", "$memberId")
        return memberId // 저장된 memberId 없을 시 기본값 -1 반환
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



