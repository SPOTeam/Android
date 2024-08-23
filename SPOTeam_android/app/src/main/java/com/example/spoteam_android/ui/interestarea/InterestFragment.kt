package com.example.spoteam_android.ui.interestarea

import RetrofitClient.getAuthToken
import StudyApiService
import StudyViewModel
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InterestFragment : Fragment() {

    lateinit var binding: FragmentInterestBinding
    private lateinit var tabLayout: TabLayout
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService
    private lateinit var interestBoardAdapter: InterestVPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInterestBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

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

        binding.spotLogo.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HouseFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icFindInterest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarmInterest.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.interestAreaStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }

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
        val activityFeeAmount = arguments?.getString("activityFeeAmount")

        tabLayout = binding.tabs
        val memberId = getMemberId(requireContext())
        val source = arguments?.getString("source")

        when (source) {
            "HouseFragment" -> {
                binding.icFilterActive.visibility = View.GONE
                fetchData("ALL", memberId)
                fetchDataGetInterestArea(memberId) { regions ->
                    setupTabs(regions, memberId, gender, minAge, maxAge, activityFee, activityFeeAmount, selectedStudyTheme)
                }
            }
            "InterestFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                fetchData(
                    "ALL",
                    memberId,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    activityFee = activityFee,
                    activityFeeAmount = activityFeeAmount,
                    selectedStudyTheme = selectedStudyTheme
                )
                fetchDataGetInterestArea(memberId) { regions ->
                    setupTabs(regions, memberId, gender, minAge, maxAge, activityFee, activityFeeAmount, selectedStudyTheme)
                }
            }
        }

        setupSpinner()
        binding.icFilter.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }
    }

    private fun setupTabs(
        regions: List<Region>?,
        memberId: Int,
        gender: String?,
        minAge: String?,
        maxAge: String?,
        activityFee: String?,
        activityFeeAmount: String?,
        selectedStudyTheme: String?
    ) {
        regions?.let {
            val allTab = tabLayout.newTab().apply {
                customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                    findViewById<TextView>(R.id.tabText).text = "전체"
                }
                tag = "0000000000"
            }
            tabLayout.addTab(allTab)

            regions.forEach { region ->
                val tab = tabLayout.newTab().apply {
                    customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                        findViewById<TextView>(R.id.tabText).text = region.neighborhood
                    }
                    tag = region.code
                }
                tabLayout.addTab(tab)
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val selectedRegionCode = tab.tag as String
                    if (selectedRegionCode == "0000000000") {
                        fetchData(
                            "ALL",
                            memberId,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = activityFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme
                        )
                    } else {
                        fetchData(
                            "ALL",
                            memberId,
                            regionCode = selectedRegionCode,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = activityFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme
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

    private fun setupSpinner() {
        val spinner: Spinner = binding.filterToggle
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val memberId = getMemberId(requireContext())
                val selectedItem = when (position) {
                    1 -> "RECRUITING"
                    2 -> "COMPLETED"
                    3 -> "HIT"
                    4 -> "LIKED"
                    else -> "ALL"
                }
                fetchData(selectedItem, memberId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchData(
        selectedItem: String,
        memberId: Int,
        regionCode: String? = null,
        gender: String? = null,
        minAge: String? = null,
        maxAge: String? = null,
        activityFee: String? = null,
        activityFeeAmount: String? = null,
        selectedStudyTheme: String? = null
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val activityFeeAmountInt = activityFeeAmount?.toIntOrNull()
        val interestAreaBoard = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount

        val call: Call<ApiResponse> = if (regionCode != null) {
            RetrofitClient.IaSapiService.InterestSpecificArea(
                authToken = getAuthToken(),
                regionCode = regionCode,
                memberId = memberId,
                gender = gender ?: "MALE",
                minAge = minAge?.toIntOrNull() ?: 18,
                maxAge = maxAge?.toIntOrNull() ?: 60,
                isOnline = false,
                hasFee = activityFee?.toBoolean() ?: false,
                fee = activityFeeAmountInt,
                page = 0,
                size = 5,
                sortBy = selectedItem
            )
        } else {
            RetrofitClient.IaapiService.InterestArea(
                authToken = getAuthToken(),
                memberId = memberId,
                gender = gender ?: "MALE",
                minAge = minAge?.toIntOrNull() ?: 18,
                maxAge = maxAge?.toIntOrNull() ?: 60,
                isOnline = false,
                hasFee = activityFee?.toBoolean() ?: false,
                fee = activityFeeAmountInt,
                page = 0,
                size = 5,
                sortBy = selectedItem
            )
        }

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    boardItems.clear()
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
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
                        checkcount.text = String.format("%02d 건", boardItems.size)
                    } else {
                        checkcount.text = "00 건"
                        interestAreaBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("InterestFragment", "Response failed: $errorBody")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("InterestFragment", "API 호출 실패", t)
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

    private fun fetchDataGetInterestArea(memberId: Int, callback: (List<Region>?) -> Unit) {
        RetrofitClient.GetIaService.GetInterestArea(
            authToken = getAuthToken(),
            memberId = memberId
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val regions = response.body()?.result?.regions
                    callback(regions)
                } else {
                    Log.e("InterestFragment", "fetchDataGetInterestArea: Response failed")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("InterestFragment", "fetchDataGetInterestArea: API 호출 실패", t)
                callback(null)
            }
        })
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
                                val adapter = binding.interestAreaStudyReyclerview.adapter as InterestVPAdapter
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

}
