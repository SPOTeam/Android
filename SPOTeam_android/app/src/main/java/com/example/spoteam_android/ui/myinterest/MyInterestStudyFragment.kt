package com.example.spoteam_android.ui.myinterest

import RetrofitClient.getAuthToken
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.databinding.FragmentMyInterestStudyBinding
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFilterFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInterestStudyFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyBinding
    private lateinit var tabLayout: TabLayout
    lateinit var filterIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val source = arguments?.getString("source")

        val gender = arguments?.getString("gender3")
        val minAge = arguments?.getString("minAge3")
        val maxAge = arguments?.getString("maxAge3")
        val activityFee01 = arguments?.getString("activityFee01")
        val activityFee02 = arguments?.getString("activityFee02")
        val activityFeeinput = arguments?.getString("activityFeeAmount3")

        when (source) {
            "HouseFragment" -> {
                fetchMyInterestAll("ALL")
            }

            "MyInterestStudyFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                    if (activityFeeinput != null) {
                        fetchMyInterestAll2(
                            "ALL",
                            gender,
                            minAge,
                            maxAge,
                            activityFee02,
                            activityFeeinput,
                            activityFee01
                        )
                    } else {
                        fetchMyInterestAll2(
                            "ALL",
                            gender,
                            minAge,
                            maxAge,
                            activityFee02,
                            "",
                            activityFee01
                        )
                    }
                }
            }
        }

        tabLayout = binding.tabs

        fetchDataGetInterestCategory { themes ->
            if (themes != null) {
                val allTab = tabLayout.newTab()
                val customView =
                    LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                val allTextView = customView.findViewById<TextView>(R.id.tabText)
                allTextView.text = "전체"

                allTab.customView = customView
                allTab.tag = "전체" // "전체" 탭의 tag 설정
                tabLayout.addTab(allTab)

                themes.forEach { them ->
                    val tab = tabLayout.newTab()

                    val customView =
                        LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null)
                    val textView = customView.findViewById<TextView>(R.id.tabText)
                    val tabText = them
                    textView.text = tabText
                    tab.customView = customView

                    tab.tag = them // 각 탭에 해당 카테고리를 태그로 저장
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

                        val selectedCategory = tab.tag as String // 선택된 카테고리 가져오기

                        if (selectedCategory == "전체") {
                            if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                if (activityFeeinput != null) {
                                    fetchMyInterestAll2(
                                        "ALL",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee02,
                                        activityFeeinput,
                                        activityFee01
                                    )
                                } else {
                                    fetchMyInterestAll2(
                                        "ALL",
                                        gender,
                                        minAge,
                                        maxAge,
                                        activityFee02,
                                        "",
                                        activityFee01
                                    )
                                }
                            }
                        } else {
                            // 카테고리별로 API 호출

                            when (source) {
                                "MyInterestStudyFilterFragment" -> {
                                    binding.icFilterActive.visibility = View.VISIBLE

                                    if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                        if (activityFeeinput != null) {
                                            fetchMyInterestSpecific(
                                                "ALL",
                                                gender,
                                                minAge,
                                                maxAge,
                                                activityFee02,
                                                activityFeeinput,
                                                selectedCategory, // 선택된 카테고리로 API 호출
                                                activityFee01
                                            )
                                        } else {
                                            fetchMyInterestSpecific(
                                                "ALL",
                                                gender,
                                                minAge,
                                                maxAge,
                                                activityFee02,
                                                "",
                                                selectedCategory, // 선택된 카테고리로 API 호출
                                                activityFee01
                                            )
                                        }
                                    }
                                }

                                else -> {
                                    fetchMyInterestSpecific(
                                        "ALL",
                                        gender = "MALE", // 기본값으로 남성 성별과 나이 범위를 설정해줌
                                        minAge = "18",
                                        maxAge = "60",
                                        activityFee = "false",
                                        activityFeeAmount = "",
                                        selectedCategory,
                                        isOnline = "false"
                                    )
                                    binding.icFilterActive.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        val textView = tab?.customView?.findViewById<TextView>(R.id.tabText)
                        textView?.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }
                })
            }
        }

        val bundle = Bundle()

        val spinner: Spinner = binding.filterToggle
        val filtericon: ImageView = binding.icFilter



        filtericon.setOnClickListener {
            bundle.putString("source", "MyInterestStudyFragment")
            val myInterestStudyFilterFragment = MyInterestStudyFilterFragment()
            myInterestStudyFilterFragment.arguments = bundle // Bundle 전달

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, myInterestStudyFilterFragment)
                .addToBackStack(null)
                .commit()
        }


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
                            0 -> fetchMyInterestAll("ALL")
                            1 -> fetchMyInterestAll("RECRUITING")
                            2 -> fetchMyInterestAll("COMPLETED")
                            3 -> fetchMyInterestAll("HIT")
                            4 -> fetchMyInterestAll("LIKED")
                        }
                    }


                    "MyInterestStudyFilterFragment" -> {
                        binding.icFilterActive.visibility = View.VISIBLE
                        when (position) {
                            0 ->
                                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                    if (activityFeeinput != null) {
                                        fetchMyInterestAll2(
                                            "ALL",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            activityFee01
                                        )
                                    } else {
                                        fetchMyInterestAll2(
                                            "ALL",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            activityFee01
                                        )
                                    }
                                }

                            1 ->
                                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                    if (activityFeeinput != null) {
                                        fetchMyInterestAll2(
                                            "RECRUITING",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            activityFee01
                                        )
                                    } else {
                                        fetchMyInterestAll2(
                                            "RECRUITING",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            activityFee01
                                        )
                                    }
                                }
                            2 ->
                                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                    if (activityFeeinput != null) {
                                        fetchMyInterestAll2(
                                            "COMPLETED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            activityFee01
                                        )
                                    } else {
                                        fetchMyInterestAll2(
                                            "COMPLETED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            activityFee01
                                        )
                                    }
                                }
                            3 ->
                                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                    if (activityFeeinput != null) {
                                        fetchMyInterestAll2(
                                            "HIT",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            activityFee01
                                        )
                                    } else {
                                        fetchMyInterestAll2(
                                            "HIT",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            activityFee01
                                        )
                                    }
                                }

                            4 ->
                                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                                    if (activityFeeinput != null) {
                                        fetchMyInterestAll2(
                                            "LIKED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            activityFeeinput,
                                            activityFee01
                                        )
                                    } else {
                                        fetchMyInterestAll2(
                                            "LIKED",
                                            gender,
                                            minAge,
                                            maxAge,
                                            activityFee02,
                                            "",
                                            activityFee01
                                        )
                                    }
                                }
                        }
                    }
                    else -> {
                        fetchMyInterestAll2(
                            "ALL",
                            gender = "MALE", // 기본값으로 남성 성별과 나이 범위를 설정해줌
                            minAge = "18",
                            maxAge = "60",
                            activityFee = "false",
                            activityFeeAmount = "",
                            isOnline = "false"
                        )
                        binding.icFilterActive.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }




    private fun fetchMyInterestAll(SelectedItem: String) {

        val myinterestboard = binding.myInterestStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()


        RetrofitClient.MIAService.GetMyInterestStudy(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = false,
            hasFee = false,
            fee = null,
            page = 0,
            size = 5,
            sortBy = SelectedItem
        )
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
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
                                    imageUrl = study.imageUrl,
                                    liked = study.liked
                                )
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems, onItemClick = { /* No-op */ }, onLikeClick = { _, _ -> /* No-op */ })

                                boardAdapter.notifyDataSetChanged()
                                myinterestboard.visibility = View.VISIBLE
                                myinterestboard.adapter = boardAdapter
                                myinterestboard.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)

                            }
                        }
                        else{
                            Toast.makeText(requireContext() ,"조건에 맞는 항목이 없습니다.",Toast.LENGTH_SHORT).show()
                            checkcount.text = "00 건"
                            myinterestboard.visibility = View.GONE
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

    private fun fetchMyInterestAll2(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String,isOnline: String) {

        val myinterestboard = binding.myInterestStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount


        RetrofitClient.MIAService.GetMyInterestStudy(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 5,
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
                                    imageUrl = study.imageUrl,
                                    liked = study.liked
                                )
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems, onItemClick = { /* No-op */ }, onLikeClick = { _, _ -> /* No-op */ })

                                boardAdapter.notifyDataSetChanged()
                                myinterestboard.visibility = View.VISIBLE
                                myinterestboard.adapter = boardAdapter
                                myinterestboard.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)

                            }
                        }
                        else{
                            Toast.makeText(requireContext() ,"조건에 맞는 항목이 없습니다.",Toast.LENGTH_SHORT).show()
                            checkcount.text = "00 건"
                            myinterestboard.visibility = View.GONE
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

    private fun fetchDataGetInterestCategory(callback: (List<String>?) -> Unit) {


        RetrofitClient.GICService.GetMyInterestStudy(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()

                    apiResponse?.let {
                        if (it.isSuccess) {
                            val themes = it.result.themes
                            themes.forEach { mytheme ->
                            }
                            callback(themes)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun fetchMyInterestSpecific(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme:String, isOnline: String) {

        val myinterestboard = binding.myInterestStudyReyclerview
        val checkcount: TextView = binding.checkAmount
        val boardItems = arrayListOf<BoardItem>()

        val activityFeeAmount: String ? = if (activityFeeAmount=="") null else activityFeeAmount



        RetrofitClient.MISSerivice.GetMyInterestStudys(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount?.toInt(),
            page = 0,
            size = 5,
            sortBy = selectedItem,
            theme = selectedStudyTheme

        ).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        boardItems.clear()
                        val apiResponse = response.body()
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
                                    imageUrl = study.imageUrl,
                                    liked = study.liked
                                )
                                boardItems.add(boardItem)
                                val boardAdapter = BoardAdapter(boardItems, onItemClick = { /* No-op */ }, onLikeClick = { _, _ -> /* No-op */ })

                                boardAdapter.notifyDataSetChanged()
                                myinterestboard.visibility = View.VISIBLE
                                myinterestboard.adapter = boardAdapter
                                myinterestboard.layoutManager = LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                val itemcount= boardItems.size
                                checkcount.text = String.format("%02d 건", itemcount)
                            }
                        }
                        else{
                            Toast.makeText(requireContext() ,"조건에 맞는 항목이 없습니다.",Toast.LENGTH_SHORT).show()
                            checkcount.text = "00 건"
                            myinterestboard.visibility = View.GONE
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
