package com.example.spoteam_android.ui.interestarea

import StudyApiService
import StudyViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
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
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.FixedRoundedSpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class InterestFragment : Fragment() {

    lateinit var binding: FragmentInterestBinding
    private lateinit var tabLayout: TabLayout
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var studyApiService: StudyApiService
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private var gender: String? = null
    private var minAge: String? = null
    private var maxAge: String? = null
    private var activityFee: String? = null
    private var selectedStudyTheme: String? = null
    private var activityFeeAmount: String? = null
    private var source: String? = null
    private var selectedItem: String = "ALL"
    private var selectedRegion: String? = "0000000000"
    private val viewModel: InterestFilterViewModel by activityViewModels()
    private var isFirstSpinnerCall = true
    private var currentPage = 0
    private val size = 5 // 페이지당 항목 수
    private var totalPages = 0
    private var startPage = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInterestBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        selectedItem = when (viewModel.isRecruiting) {
            "있음" -> "RECRUITING"
            "없음" -> "COMPLETED"
            else -> "ALL"
        }

        setupRecyclerView()

        binding.spotLogo.setOnClickListener {
            replaceFragment(HouseFragment())
        }

        binding.icFindInterest.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.icAlarmInterest.setOnClickListener {
            replaceFragment(AlertFragment())
        }


        gender = viewModel.gender
        minAge = viewModel.minAge.toString()
        maxAge = viewModel.maxAge.toString()
        activityFee = viewModel.activityFee
        selectedStudyTheme = null
        activityFeeAmount = viewModel.activityFeeAmount
        source = arguments?.getString("source") // source만 그대로 Bundle에서 받음

        setupBottomSheet()

        Log.d("InterestFragment","$gender, $minAge, $maxAge,$activityFee,$selectedStudyTheme,$activityFeeAmount,$source")

        tabLayout = binding.tabs

        when (source) {
            "HouseFragment" -> {
                binding.icFilter.visibility = View.VISIBLE
                binding.icFilterActive.visibility = View.GONE
                fetchData(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    activityFee = activityFee,
                    activityFeeAmount = activityFeeAmount,
                    selectedStudyTheme = selectedStudyTheme,
                    currentPage = currentPage,
                )
                fetchDataGetInterestArea() { regions ->
                    setupTabs(regions)
                }
            }
            "InterestFilterFragment" -> {
                binding.icFilterActive.visibility = View.VISIBLE
                binding.icFilter.visibility = View.GONE
                fetchData(
                    selectedItem,
                    gender = gender,
                    minAge = minAge,
                    maxAge = maxAge,
                    activityFee = activityFee,
                    activityFeeAmount = activityFeeAmount,
                    selectedStudyTheme = selectedStudyTheme,
                    currentPage = currentPage,
                )
                fetchDataGetInterestArea() { regions ->
                    setupTabs(regions)
                }
            }
        }


        binding.icFilter.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }

        binding.icFilterActive.setOnClickListener {
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        }, studyViewModel = studyViewModel)

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

        binding.interestAreaStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }
    }

    private fun setupTabs(
        regions: List<Region>?,
    ) {
        // 로그 태그 정의
        val TAG = "SetupTabs"

        tabLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                // 스크롤 동작 무시
                true
            } else {
                // 다른 이벤트는 정상 처리
                false
            }
        }

        regions?.let {
            // "전체" 탭 로그
            Log.d(TAG, "Adding '전체' tab")
            val allTab = tabLayout.newTab().apply {
                customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                    val textView = findViewById<TextView>(R.id.tabText)
                    textView.text = "전체"
                    textView.setTypeface(null, Typeface.BOLD) // 최초 "전체" Bold 처리
                }
                tag = "0000000000"
                selectedRegion = "0000000000"
            }


            tabLayout.addTab(allTab)

            // 지역별 탭 로그
            regions.forEach { region ->
                Log.d(TAG, "Adding tab for region: ${region.neighborhood}, code: ${region.code}")
                val tab = tabLayout.newTab().apply {
                    customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                        findViewById<TextView>(R.id.tabText).text = region.neighborhood
                    }
                    tag = region.code
                }
                tabLayout.addTab(tab)
            }

            // 탭 선택 리스너 로그
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val textView = tab.view?.findViewById<TextView>(R.id.tabText)
                    textView?.setTypeface(null, Typeface.BOLD)

                    val selectedRegionCode = tab.tag as String
                    Log.d(TAG, "Tab selected: ${tab.tag}, region code: $selectedRegionCode")
                    if (selectedRegionCode == "0000000000") {
                        Log.d(TAG, "Fetching data for '전체'")
                        selectedRegion = "0000000000"
                        fetchData(
                            selectedItem,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = activityFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme,
                            currentPage = currentPage
                        )
                    } else {
                        Log.d(TAG, "Fetching data for region code: $selectedRegionCode")
                        selectedRegion = selectedRegionCode
                        fetchData(
                            selectedItem,
                            regionCode = selectedRegionCode,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = activityFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme,
                            currentPage = currentPage
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "Tab unselected: ${tab?.tag}")
                    val textView = tab?.view?.findViewById<TextView>(R.id.tabText)
                    textView?.setTypeface(null, Typeface.NORMAL)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "Tab reselected: ${tab?.tag}")
                }
            })
        } ?: run {
            Log.w(TAG, "Regions list is null or empty")
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }



    private fun setupBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_interest_spinner, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.InterestBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)

        val recentlyLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_recently)
        val viewLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_view)
        val hotLayout = dialogView.findViewById<FrameLayout>(R.id.framelayout_hot)

        recentlyLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "최신 순"
            fetchFilteredStudy("ALL")  // 최신 순
        }

        viewLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "조회 수 높은 순"
            fetchFilteredStudy("HIT")
        }

        hotLayout.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.filterToggle.text = "관심 많은 순"
            fetchFilteredStudy("LIKED")  // 관심 많은 순
        }

        binding.filterToggleContainer.setOnClickListener {
            bottomSheetDialog.show()
        }
    }

    private fun fetchFilteredStudy(selectedItem: String) {
        this.selectedItem = selectedItem
        fetchData(
            selectedItem = selectedItem,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            activityFee = activityFee,
            activityFeeAmount = activityFeeAmount,
            selectedStudyTheme = selectedStudyTheme,
            currentPage = currentPage
        )
    }



//    private fun setupSpinner() {
//        val genderList = listOf("최신 순", "조회수 높은 순", "관심 많은 순")
//        val genderAdapter = FixedRoundedSpinnerAdapter(requireContext(), genderList)
//        binding.filterToggle.adapter = genderAdapter
//        binding.filterToggle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (isFirstSpinnerCall) {
//                    isFirstSpinnerCall = false
//                    return
//                }
//
//                selectedItem = when (position) {
//                    0 -> "ALL"   // 최신 순
//                    1 -> "HIT"      // 조회수 높은 순
//                    2 -> "LIKED"    // 관심 많은 순
//                    else -> "ALL"
//                }
//                fetchData(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    activityFee = activityFee,
//                    activityFeeAmount = activityFeeAmount,
//                    selectedStudyTheme = selectedStudyTheme,
//                    currentPage = currentPage
//                )
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//        }
//    }


    private fun fetchData(
        selectedItem: String,
        regionCode: String? = null,
        gender: String? = null,
        minAge: String? = null,
        maxAge: String? = null,
        activityFee: String? = null,
        activityFeeAmount: String? = null,
        selectedStudyTheme: String? = null,
        currentPage: Int?= null
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val activityFeeAmountInt = activityFeeAmount?.toIntOrNull()
        val interestAreaBoard = binding.interestAreaStudyReyclerview
        val checkcount: TextView = binding.checkAmount

        val hasFee = when (activityFee) {
            "있음" -> true
            "없음" -> false
            else -> false
        }

        val call: Call<ApiResponse> = if (regionCode != null) {
            val service = RetrofitInstance.retrofit.create(InterestSpecificAreaApiService::class.java)
            service.InterestSpecificArea(
                regionCode = regionCode,
                gender = gender ?: "MALE",
                minAge = minAge?.toIntOrNull() ?: 18,
                maxAge = maxAge?.toIntOrNull() ?: 60,
                isOnline = false,
                hasFee = hasFee,
                fee = activityFeeAmountInt,
                page = currentPage ?: 0,
                size = 5,
                sortBy = selectedItem)
        } else {
            val service = RetrofitInstance.retrofit.create(InterestAreaApiService::class.java)
            service.InterestArea(
                gender = gender ?: "MALE",
                minAge = minAge?.toIntOrNull() ?: 18,
                maxAge = maxAge?.toIntOrNull() ?: 60,
                isOnline = false,
                hasFee = hasFee,
                fee = activityFeeAmountInt,
                page = currentPage ?: 0,
                size = 5,
                sortBy = selectedItem
            )
        }
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Log.d("InterestFragment","${response.body()}")
                    boardItems.clear()
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        apiResponse.result?.content?.forEach { study ->
                            totalPages = apiResponse.result.totalPages
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
                                liked = study.liked,
                                isHost = false
                            )
                            boardItems.add(boardItem)

                            binding.pageNumberLayout.visibility = View.VISIBLE
                            startPage = calculateStartPage()
                            updatePageNumberUI()
                            updatePageUI()
                        }
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                        interestAreaBoard.visibility = View.VISIBLE
                        updateRecyclerView(boardItems)
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        checkcount.text = "0 건"
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


    private fun updatePageNumberUI() {
        startPage = if (currentPage <= 2) {
            0
        } else {
            maxOf(totalPages - 5, maxOf(0, currentPage - 2))
        }

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    if (selectedRegion == "0000000000"){
                        fetchData(
                            selectedItem,
                            currentPage = currentPage
                        )
                    } else{
                        fetchData(
                            selectedItem,
                            regionCode = selectedRegion,
                            gender = gender,
                            minAge = minAge,
                            maxAge = maxAge,
                            activityFee = activityFee,
                            activityFeeAmount = activityFeeAmount,
                            selectedStudyTheme = selectedStudyTheme,
                            currentPage = currentPage
                        )
                    }
                }
            }
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                if (selectedRegion == "0000000000"){
                    fetchData(
                        selectedItem,
                        currentPage = currentPage
                    )
                } else{
                    fetchData(
                        selectedItem,
                        regionCode = selectedRegion,
                        gender = gender,
                        minAge = minAge,
                        maxAge = maxAge,
                        activityFee = activityFee,
                        activityFeeAmount = activityFeeAmount,
                        selectedStudyTheme = selectedStudyTheme,
                        currentPage = currentPage
                    )
                }
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                if (selectedRegion == "0000000000"){
                    fetchData(
                        selectedItem,
                        currentPage = currentPage
                    )
                } else{
                    fetchData(
                        selectedItem,
                        regionCode = selectedRegion,
                        gender = gender,
                        minAge = minAge,
                        maxAge = maxAge,
                        activityFee = activityFee,
                        activityFeeAmount = activityFeeAmount,
                        selectedStudyTheme = selectedStudyTheme,
                        currentPage = currentPage
                    )
                }
            }
        }
    }

    private fun calculateStartPage(): Int {
        return if (currentPage <= 2) {
            0
        } else {
            maxOf(totalPages - 5, maxOf(0, currentPage - 2))
        }
    }

    private fun getTotalPages(): Int {
        return totalPages // 올바른 페이지 수 계산
    }

    private fun updatePageUI() {
        startPage = if (currentPage <= 2) {
            0
        } else {
            maxOf(totalPages - 5, maxOf(0, currentPage - 2))
        }

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum < totalPages) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(
                    if (pageNum == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.isEnabled = true
                textView.alpha = 1.0f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(0)
                textView.isEnabled = false // 클릭 안 되게
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPages - 1
    }


    private fun fetchDataGetInterestArea( callback: (List<Region>?) -> Unit) {
        val service = RetrofitInstance.retrofit.create(GetMemberInterestAreaApiService::class.java)
        service.GetInterestArea(
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val regions = response.body()?.result?.regions
                    Log.d("InterestFragment","$regions")
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
            studyApiService.toggleStudyLike(studyItem.studyId)
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
