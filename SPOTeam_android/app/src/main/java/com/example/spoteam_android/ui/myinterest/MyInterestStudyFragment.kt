package com.example.spoteam_android.ui.myinterest

import RetrofitClient
import RetrofitClient.getAuthToken
import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMyInterestStudyBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyInterestStudyFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: InterestVPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
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

        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        binding.icFindMyInterest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarmMyInterest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.myInterestStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }

        when (source) {
            "HouseFragment" -> {
                fetchMyInterestAll("ALL")
            }
            "MyInterestStudyFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                if (gender != null && minAge != null && maxAge != null && activityFee02 != null && activityFee01 != null) {
                    fetchMyInterestAll2(
                        "ALL",
                        gender,
                        minAge,
                        maxAge,
                        activityFee02,
                        activityFeeinput ?: "",
                        activityFee01
                    )
                }
            }
        }

        setupTabs()
        setupSpinner()
        setupFilterIcon()
    }

    private fun setupTabs() {
        tabLayout = binding.tabs

        fetchDataGetInterestCategory { themes ->
            if (themes != null) {
                val allTab = tabLayout.newTab().apply {
                    customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                        findViewById<TextView>(R.id.tabText).text = "전체"
                    }
                    tag = "전체"
                }
                tabLayout.addTab(allTab)

                themes.forEach { theme ->
                    val tab = tabLayout.newTab().apply {
                        customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                            findViewById<TextView>(R.id.tabText).text = theme
                        }
                        tag = theme
                    }
                    tabLayout.addTab(tab)
                }

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab) {
                        val selectedCategory = tab.tag as String
                        if (selectedCategory == "전체") {
                            fetchMyInterestAll("ALL")
                        } else {
                            fetchMyInterestSpecific(
                                "ALL",
                                gender = "MALE",
                                minAge = "18",
                                maxAge = "60",
                                activityFee = "false",
                                activityFeeAmount = "",
                                selectedStudyTheme = selectedCategory,
                                isOnline = "false"
                            )
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                        tab?.customView?.findViewById<TextView>(R.id.tabText)
                            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
            }
        }
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.filterToggle

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = when (position) {
                    1 -> "RECRUITING"
                    2 -> "COMPLETED"
                    3 -> "HIT"
                    4 -> "LIKED"
                    else -> "ALL"
                }
                fetchMyInterestAll(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupFilterIcon() {
        val filtericon: ImageView = binding.icFilter
        filtericon.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
            val myInterestStudyFilterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, myInterestStudyFilterFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchMyInterestAll(selectedItem: String) {
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
            sortBy = selectedItem
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
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
                        }
                        updateRecyclerView(boardItems)
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                    } else {
                        binding.checkAmount.text = "0건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMyInterestAll2(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, isOnline: String) {
        val boardItems = arrayListOf<BoardItem>()
        RetrofitClient.MIAService.GetMyInterestStudy(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount.toIntOrNull(),
            page = 0,
            size = 5,
            sortBy = selectedItem
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
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
                        }
                        updateRecyclerView(boardItems)
                    } else {
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMyInterestSpecific(selectedItem: String, gender: String, minAge: String, maxAge: String, activityFee: String, activityFeeAmount: String, selectedStudyTheme: String, isOnline: String) {
        val boardItems = arrayListOf<BoardItem>()
        RetrofitClient.MISSerivice.GetMyInterestStudys(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext()),
            gender = gender,
            minAge = minAge.toInt(),
            maxAge = maxAge.toInt(),
            isOnline = isOnline.toBoolean(),
            hasFee = activityFee.toBoolean(),
            fee = activityFeeAmount.toIntOrNull(),
            page = 0,
            size = 5,
            sortBy = selectedItem,
            theme = selectedStudyTheme
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        apiResponse.result?.content?.forEach { study ->
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
                        }
                        updateRecyclerView(boardItems)
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                    } else {
                        binding.checkAmount.text = "0건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDataGetInterestCategory(callback: (List<String>?) -> Unit) {
        RetrofitClient.GICService.GetMyInterestStudy(
            authToken = getAuthToken(),
            memberId = getMemberId(requireContext())
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        if (it.isSuccess) {
                            callback(it.result.themes)
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

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 BoardItem의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.myInterestStudyReyclerview.adapter as InterestVPAdapter
                                val position = adapter.dataList.indexOf(studyItem)
                                if (position != -1) {
                                    adapter.notifyItemChanged(position)
                                }
                            }
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

    private fun updateLikeButtonUI(likeButton: ImageView, isLiked: Boolean) {
        val newIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.study_like
        likeButton.setImageResource(newIcon)
    }
}
