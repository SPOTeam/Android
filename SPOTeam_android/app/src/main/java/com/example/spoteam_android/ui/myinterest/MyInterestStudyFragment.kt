package com.example.spoteam_android.ui.myinterest

import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
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
import com.example.spoteam_android.databinding.FragmentMyInterestStudyBinding
import com.example.spoteam_android.search.SearchFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.GetInterestCategoryApiService
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.interestarea.MyInterestStudyAllApiService
import com.example.spoteam_android.ui.interestarea.MyInterestStudySpecificApiService
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.FixedRoundedSpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query
class MyInterestStudyFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var interestBoardAdapter: InterestVPAdapter
    private var gender: String? = "MALE"
    private var minAge: String? = "18"
    private var maxAge: String? = "60"
    private var activityFee01: String? = "false" //온 오프라인 변수
    private var activityFee02: String? = "false" // 활동비 유무 변수
    private var activityFeeAmount: String? = null // 활동비 구체적인 금액
    private var source: String? = null
    private var selectedItem: String = "ALL"
    private var selectedStudyCategory: String? = "전체"
    private var isFirstSpinnerCall = true
    private val chipViewModel: ChipViewModel by activityViewModels()
    private var currentPage = 0
    private val size = 5 // 페이지당 항목 수
    private var totalPages = 0
    private var startPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        initArguments()
        setupRecyclerView()
        setupTabs()
        setupBottomSheet()
        setupFilterIcon()
        setupPageNavigationButtons()
        setupNavigationClickListeners()

        initialFetch()

        return binding.root
    }

    private fun initArguments() {
        source = arguments?.getString("source") // source만 그대로 Bundle에서 받음
        gender = chipViewModel.gender ?: gender
        minAge = chipViewModel.minAge ?: minAge
        maxAge = chipViewModel.maxAge ?: maxAge
        activityFee01 = chipViewModel.activityFee01 ?: activityFee01
        activityFee02 = chipViewModel.activityFee02 ?: activityFee02
        activityFeeAmount = chipViewModel.activityFeeAmount ?: activityFeeAmount
    }

    private fun initialFetch() {
        when (source) {
            "HouseFragment" -> {
                binding.icFilter.visibility = View.VISIBLE
                binding.icFilterActive.visibility = View.GONE
                fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
            }
            "MyInterestStudyFilterFragment" -> {
                binding.icFilter.visibility = View.GONE
                binding.icFilterActive.visibility = View.VISIBLE
                fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
            }
        }
    }

    private fun setupTabs() {
        tabLayout = binding.tabs
        tabLayout.setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }

        fetchDataGetInterestCategory { themes ->
            themes?.let {
                setupTabsWithThemes(it)
            }
        }
    }

    private fun setupTabsWithThemes(themes: List<String>) {
        tabLayout.addTab(createTab("전체", isBold = true))

        themes.forEach { theme ->
            tabLayout.addTab(createTab(theme))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                handleTabSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.findViewById<TextView>(R.id.tabText)?.apply {
                    setTypeface(null, Typeface.NORMAL)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray04))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun createTab(text: String, isBold: Boolean = false): TabLayout.Tab {
        return tabLayout.newTab().apply {
            customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
                val textView = findViewById<TextView>(R.id.tabText)
                textView.text = text
                if (isBold) textView.setTypeface(null, Typeface.BOLD)
            }
            tag = text
        }
    }

    private fun handleTabSelected(tab: TabLayout.Tab) {
        val textView = tab.view?.findViewById<TextView>(R.id.tabText)
        textView?.apply {
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.MainColor_01))
        }

        selectedStudyCategory = tab.tag as String

        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
        } else {
            fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
        }
    }


    private fun setupRecyclerView() {
        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { item, btn ->
            toggleLikeStatus(item, btn)
        }, studyViewModel)

        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)
                replaceFragment(DetailStudyFragment())
            }
        })

        binding.myInterestStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
        }
    }

    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
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
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
        } else {
            fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
        }
    }

    private fun setupFilterIcon() {
        binding.icFilter.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
            val filterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }

        binding.icFilterActive.setOnClickListener {
            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
            val filterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }
            replaceFragment(filterFragment)
        }


    }

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                requestPageUpdate()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                requestPageUpdate()
            }
        }
    }

    private fun requestPageUpdate() {
        if (selectedStudyCategory == "전체") {
            fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
        } else {
            fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
        }
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
                    if (selectedStudyCategory == "전체") {
                        fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
                    } else {
                        fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
                    }
                }
            }
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                if (selectedStudyCategory == "전체") {
                    fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
                } else {
                    fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
                }
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < getTotalPages() - 1) {
                currentPage++
                if (selectedStudyCategory == "전체") {
                    fetchMyInterestAll(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, activityFee01, currentPage)
                } else {
                    fetchMyInterestSpecific(selectedItem, gender, minAge, maxAge, activityFee02, activityFeeAmount, selectedStudyCategory, activityFee01, currentPage)
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

    private fun fetchMyInterestAll(
                                    selectedItem: String?,
                                    gender: String?,
                                    minAge: String?,
                                    maxAge: String?,
                                    activityFee: String?,
                                    activityFeeAmount: String?,
                                    isOnline: String?,
                                    currentPage: Int?= null)
    {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(MyInterestStudyAllApiService::class.java)

        Log.d("API_REQUEST", "fetchMyInterestAll() called with params: " +
                "selectedItem=$selectedItem, gender=$gender, minAge=$minAge, maxAge=$maxAge, " +
                "isOnline=$isOnline, activityFee=$activityFee, activityFeeAmount=$activityFeeAmount")
        service.GetMyInterestStudy(
            gender = gender,
            minAge = minAge?.toInt(),
            maxAge = maxAge?.toInt(),
            isOnline = isOnline?.toBoolean(),
            hasFee = activityFee?.toBoolean(),
            fee = activityFeeAmount?.toIntOrNull(),
            page = currentPage ?: 0 ,
            size = 5,
            sortBy = selectedItem ?: "ALL"
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        Log.d("API_RESPONSE", "fetchMyInterestALL()22 response: ${response.body()}")
                        boardItems.clear()
                        apiResponse.result.content.forEach { study ->
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
                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
                        val totalElements = apiResponse.result.totalElements
                        binding.checkAmount.text = totalElements.toString()+"건"
                        updateRecyclerView(boardItems)
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        binding.checkAmount.text = "0건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "API111 호출 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchMyInterestSpecific(
        selectedItem: String,
        gender: String?,
        minAge: String?,
        maxAge: String?,
        activityFee: String?,
        activityFeeAmount: String?,
        selectedStudyTheme: String?,
        isOnline: String?,
        currentPage: Int? = null
    ) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(MyInterestStudySpecificApiService::class.java)

        Log.d("API_REQUEST", """
        fetchMyInterestSpecific() called
        selectedItem = $selectedItem
        gender = $gender
        minAge = $minAge
        maxAge = $maxAge
        isOnline = $isOnline
        activityFee = $activityFee
        activityFeeAmount = $activityFeeAmount
        selectedStudyTheme = $selectedStudyTheme
        currentPage = $currentPage
    """.trimIndent())

        service.GetMyInterestStudys(
            gender = gender,
            minAge = minAge?.toIntOrNull(),
            maxAge = maxAge?.toIntOrNull(),
            isOnline = isOnline?.toBooleanStrictOrNull(),
            hasFee = activityFee?.toBooleanStrictOrNull(),
            fee = activityFeeAmount?.toIntOrNull(),
            page = currentPage ?: 0,
            size = 5,
            sortBy = selectedItem,
            theme = selectedStudyTheme
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Log.d("API_RESPONSE", "HTTP Code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE_BODY", apiResponse.toString())

                    if (apiResponse?.isSuccess == true) {
                        Log.d("API_SUCCESS", "스터디 목록 수: ${apiResponse.result?.content?.size}")
                        boardItems.clear()
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
                        }
                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
                        binding.checkAmount.text = "${apiResponse.result.totalElements}건"
                        updateRecyclerView(boardItems)

                        binding.pageNumberLayout.visibility = View.VISIBLE
                        startPage = calculateStartPage()
                        updatePageNumberUI()
                        updatePageUI()
                    } else {
                        binding.pageNumberLayout.visibility = View.GONE
                        binding.checkAmount.text = "0건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                        binding.myInterestStudyReyclerview.visibility = View.GONE
                    }
                } else {
                    Log.e("API_ERROR", """
                    API 호출 실패
                    HTTP code: ${response.code()}
                    errorBody: ${response.errorBody()?.string()}
                    message: ${response.message()}
                """.trimIndent())
                    showErrorToast()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                when (t) {
                    is java.net.SocketTimeoutException -> {
                        Log.e("API_EXCEPTION", "타임아웃 발생")
                    }
                    is java.net.UnknownHostException -> {
                        Log.e("API_EXCEPTION", "서버 주소가 잘못되었거나 인터넷 연결 없음")
                    }
                    is com.google.gson.JsonSyntaxException -> {
                        Log.e("API_EXCEPTION", "JSON 파싱 실패: ${t.message}")
                    }
                    else -> {
                        Log.e("API_EXCEPTION", "기타 예외: ${t.javaClass.simpleName}")
                    }
                }
                showErrorToast()
            }
        })
    }

    private fun showEmptyState() {
        binding.checkAmount.text = "0건"
        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
        binding.myInterestStudyReyclerview.visibility = View.GONE
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
    }


    private fun fetchDataGetInterestCategory(callback: (List<String>?) -> Unit) {
        val service = RetrofitInstance.retrofit.create(GetInterestCategoryApiService::class.java)
        service.GetMyInterestStudy(
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

    private fun replaceFragment(fragment: Fragment) {
    (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
        .replace(R.id.main_frm, fragment)
        .addToBackStack(null)
        .commitAllowingStateLoss()
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
    }

    private fun setupNavigationClickListeners() {
        binding.icFindMyInterest.setOnClickListener {
            replaceFragment(SearchFragment())
        }

        binding.spotLogo.setOnClickListener {
            replaceFragment(HouseFragment())
        }

        binding.icAlarmMyInterest.setOnClickListener {
            replaceFragment(AlertFragment())
        }
    }



}


//class MyInterestStudyFragment : Fragment() {
//
//    lateinit var binding: FragmentMyInterestStudyBinding
//    private lateinit var tabLayout: TabLayout
//    private lateinit var studyApiService: StudyApiService
//    private val studyViewModel: StudyViewModel by activityViewModels()
//    private lateinit var interestBoardAdapter: InterestVPAdapter
//    private var currentPage: Int = 0
//    private var totalPages: Int = 0
//    private var gender: String? = "MALE"
//    private var minAge: String? = "18"
//    private var maxAge: String? = "60"
//    private var activityFee01: String? = "false" //온 오프라인 변수
//    private var activityFee02: String? = "false" // 활동비 유무 변수
//    private var activityFeeAmount: String? = "" // 활동비 구체적인 금액
//    private var source: String? = null
//    private var selectedItem: String = "ALL"
//    private var selectedStudyCategory: String? = "전체"
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentMyInterestStudyBinding.inflate(inflater, container, false)
//        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
//
//        source = arguments?.getString("source")
//        gender = arguments?.getString("gender3") ?: gender
//        minAge = arguments?.getString("minAge3") ?: minAge
//        maxAge = arguments?.getString("maxAge3") ?: maxAge
//        activityFee01 = arguments?.getString("activityFee01") ?: activityFee01
//        activityFee02 = arguments?.getString("activityFee02") ?: activityFee02
//        activityFeeAmount = arguments?.getString("activityFeeAmount3") ?: activityFeeAmount
//
//        fetchMyInterestAll(
//            selectedItem,
//            gender = gender,
//            minAge = minAge,
//            maxAge = maxAge,
//            activityFee = activityFee02,
//            activityFeeAmount = activityFeeAmount,
//            isOnline = activityFee01,
//            currentPage = currentPage  // currentPage 유지
//        )
//
//
//        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
//            toggleLikeStatus(selectedItem, likeButton)
//        },studyViewModel = studyViewModel)
//
//        binding.icFindMyInterest.setOnClickListener {
//            replaceFragment(SearchFragment())
//        }
//
//        binding.spotLogo.setOnClickListener {
//            replaceFragment(HouseFragment())
//        }
//
//        binding.icAlarmMyInterest.setOnClickListener {
//            replaceFragment(AlertFragment())
//        }
//
//        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
//            override fun onItemClick(data: BoardItem) {
//                studyViewModel.setStudyData(
//                    data.studyId,
//                    data.imageUrl,
//                    data.introduction
//                )
//
//                val detailStudyFragment = DetailStudyFragment()
//                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
//                    ?.replace(R.id.main_frm, detailStudyFragment)
//                    ?.addToBackStack(null)
//                    ?.commit()
//            }
//        })
//
//        binding.myInterestStudyReyclerview.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = interestBoardAdapter
//        }
//
//        when (source) {
//            "HouseFragment" -> {
//                fetchMyInterestAll(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    activityFee = activityFee02,
//                    activityFeeAmount = activityFeeAmount,
//                    isOnline = activityFee01,
//                    currentPage = currentPage  // currentPage 유지
//                )
//            }
//            "MyInterestStudyFilterFragment" -> {
//                binding.icFilterActive.visibility = View.VISIBLE
//                Log.d("MyInterestStudyFragment","1")
//                fetchMyInterestSpecific(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    activityFee = activityFee02,
//                    activityFeeAmount = activityFeeAmount,
//                    isOnline = activityFee01,
//                    currentPage = currentPage,
//                    selectedStudyTheme = selectedStudyCategory// currentPage 유지
//                )
//            }
//        }
//
//        setupTabs()
//        setupSpinner()
//        setupFilterIcon()
//        setupPageNavigationButtons()
//
//        return binding.root
//    }
//
//    private fun setupTabs() {
//        tabLayout = binding.tabs
//
//        tabLayout.setOnTouchListener { _, event ->
//            if (event.action == MotionEvent.ACTION_MOVE) {
//                // 스크롤 동작 무시
//                true
//            } else {
//                // 다른 이벤트는 정상 처리
//                false
//            }
//        }
//
//        fetchDataGetInterestCategory { themes ->
//            if (themes != null) {
//                val allTab = tabLayout.newTab().apply {
//                    customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
//                        val textView = findViewById<TextView>(R.id.tabText)
//                        textView.text = "전체"
//                        textView.setTypeface(null, Typeface.BOLD) // 최초 "전체" Bold 처리
//                    }
//                    tag = "전체"
//                }
//                tabLayout.addTab(allTab)
//
//                themes.forEach { theme ->
//                    val tab = tabLayout.newTab().apply {
//                        customView = LayoutInflater.from(context).inflate(R.layout.custom_tab_text, null).apply {
//                            findViewById<TextView>(R.id.tabText).text = theme
//                        }
//                        tag = theme
//                    }
//                    tabLayout.addTab(tab)
//                }
//
//                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                    override fun onTabSelected(tab: TabLayout.Tab) {
//                        val textView = tab.view?.findViewById<TextView>(R.id.tabText)
//                        textView?.apply {
//                            setTypeface(null, Typeface.BOLD) // Bold 설정
//                            setTextColor(ContextCompat.getColor(requireContext(), R.color.MainColor_01)) // 텍스트 색상 변경
//                        }
//
//                        val selectedCategory = tab.tag as String
//                        if (selectedCategory == "전체") {
//                            selectedStudyCategory = selectedCategory
//                            fetchMyInterestAll(
//                                selectedItem,
//                                gender = gender,
//                                minAge = minAge,
//                                maxAge = maxAge,
//                                activityFee = activityFee02,
//                                activityFeeAmount = activityFeeAmount,
//                                isOnline = activityFee01,
//                                currentPage = currentPage  // currentPage 유지
//                            )
//                        } else {
//                            selectedStudyCategory = selectedCategory
//                            Log.d("MyInterestStudyFragment","2")
//                            fetchMyInterestSpecific(
//                                selectedItem = selectedItem,
//                                gender = gender,
//                                minAge = minAge,
//                                maxAge = maxAge,
//                                isOnline = activityFee01,
//                                activityFee = activityFee02,
//                                activityFeeAmount = activityFeeAmount,
//                                selectedStudyTheme = selectedStudyCategory,
//                                currentPage = currentPage
//                            )
//                        }
//                    }
//
//                    override fun onTabUnselected(tab: TabLayout.Tab?) {
//                        val textView = tab?.view?.findViewById<TextView>(R.id.tabText)
//                        textView?.setTypeface(null, Typeface.NORMAL)
//                        textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray04))
//                    }
//
//                    override fun onTabReselected(tab: TabLayout.Tab?) {}
//                })
//            }
//        }
//    }
//
//    private fun setupSpinner() {
//        val spinner: Spinner = binding.filterToggle
//
//        val adapter = ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.category_study,
//            R.layout.spinner_item
//        )
//        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
//        spinner.adapter = adapter
//
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                selectedItem = when (position) {
//                    1 -> "RECRUITING"
//                    2 -> "COMPLETED"
//                    3 -> "HIT"
//                    4 -> "LIKED"
//                    else -> "ALL"
//                }
//                fetchMyInterestAll(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    activityFee = activityFee02,
//                    activityFeeAmount = activityFeeAmount,
//                    isOnline = activityFee01,
//                    currentPage = currentPage  // currentPage 유지
//                )
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }
//
//    private fun setupFilterIcon() {
//        val filtericon: ImageView = binding.icFilter
//        filtericon.setOnClickListener {
//            val bundle = Bundle().apply { putString("source", "MyInterestStudyFragment") }
//            val myInterestStudyFilterFragment = MyInterestStudyFilterFragment().apply { arguments = bundle }
//
//            (activity as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm, myInterestStudyFilterFragment)
//                .addToBackStack(null)
//                .commit()
//        }
//    }
//
////
////    @Query("page") page: Int,
////    @Query("size") size: Int,
////    @Query("sortBy") sortBy: String,
////    @Query("gender") gender: String?,
////    @Query("minAge") minAge: Int?,
////    @Query("maxAge") maxAge: Int?,
////    @Query("isOnline") isOnline: Boolean?,
////    @Query("hasFee") hasFee: Boolean?,
////    @Query("fee") fee: Int?
//
//    private fun fetchMyInterestAll(
//                                    selectedItem: String?,
//                                    gender: String?,
//                                    minAge: String?,
//                                    maxAge: String?,
//                                    activityFee: String?,
//                                    activityFeeAmount: String?,
//                                    isOnline: String?,
//                                    currentPage: Int?= null)
//    {
//        val boardItems = arrayListOf<BoardItem>()
//        val service = RetrofitInstance.retrofit.create(MyInterestStudyAllApiService::class.java)
//
//        Log.d("API_REQUEST", "fetchMyInterestAll() called with params: " +
//                "selectedItem=$selectedItem, gender=$gender, minAge=$minAge, maxAge=$maxAge, " +
//                "isOnline=$isOnline, activityFee=$activityFee, activityFeeAmount=$activityFeeAmount")
//        service.GetMyInterestStudy(
//            gender = gender,
//            minAge = minAge?.toInt(),
//            maxAge = maxAge?.toInt(),
//            isOnline = isOnline?.toBoolean(),
//            hasFee = activityFee?.toBoolean(),
//            fee = activityFeeAmount?.toIntOrNull(),
//            page = currentPage ?: 0 ,
//            size = 5,
//            sortBy = selectedItem ?: "ALL"
//        ).enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//
//                if (response.isSuccessful) {
//                    val apiResponse = response.body()
//                    if (apiResponse?.isSuccess == true) {
//                        Log.d("API_RESPONSE", "fetchMyInterestALL()22 response: ${response.body()}")
//                        boardItems.clear()
//                        apiResponse.result.content.forEach { study ->
//                            totalPages = apiResponse.result.totalPages
//                            val boardItem = BoardItem(
//                                studyId = study.studyId,
//                                title = study.title,
//                                goal = study.goal,
//                                introduction = study.introduction,
//                                memberCount = study.memberCount,
//                                heartCount = study.heartCount,
//                                hitNum = study.hitNum,
//                                maxPeople = study.maxPeople,
//                                studyState = study.studyState,
//                                themeTypes = study.themeTypes,
//                                regions = study.regions,
//                                imageUrl = study.imageUrl,
//                                liked = study.liked,
//                                isHost = false
//                            )
//                            boardItems.add(boardItem)
//                        }
//                        updatePageNumberUI()
//                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
//                        val totalElements = apiResponse.result.totalElements
//                        binding.checkAmount.text = totalElements.toString()+"건"
//                        updateRecyclerView(boardItems)
//                    } else {
//                        binding.checkAmount.text = "0건"
//                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
//                        binding.myInterestStudyReyclerview.visibility = View.GONE
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun fetchMyInterestSpecific(selectedItem: String,
//                                        gender: String?,
//                                        minAge: String?,
//                                        maxAge: String?,
//                                        activityFee: String?,
//                                        activityFeeAmount: String?,
//                                        selectedStudyTheme: String?,
//                                        isOnline: String?,
//                                        currentPage: Int?= null)
//    {
//        val boardItems = arrayListOf<BoardItem>()
//        val service = RetrofitInstance.retrofit.create(MyInterestStudySpecificApiService::class.java)
//        Log.d("API_REQUEST", "fetchMyInterestSpecific() called with params: " +
//                "selectedItem=$selectedItem, gender=$gender, minAge=$minAge, maxAge=$maxAge, " +
//                "isOnline=$isOnline, activityFee=$activityFee, activityFeeAmount=$activityFeeAmount"+"theme=$selectedStudyTheme"  )
//        service.GetMyInterestStudys(
//            gender = gender,
//            minAge = minAge?.toInt(),
//            maxAge = maxAge?.toInt(),
//            isOnline = isOnline?.toBoolean(),
//            hasFee = activityFee?.toBoolean(),
//            fee = activityFeeAmount?.toIntOrNull(),
//            page = currentPage ?: 0,
//            size = 5,
//            sortBy = selectedItem,
//            theme = selectedStudyTheme
//        ).enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                if (response.isSuccessful) {
//
//                    val apiResponse = response.body()
//                    if (apiResponse?.isSuccess == true) {
//                        Log.d("API_RESPONSE", "fetchMyInterestSpecific()22 response: ${response.body()}")
//                        boardItems.clear()
//                        apiResponse.result?.content?.forEach { study ->
//                            totalPages = apiResponse.result.totalPages
//                            val boardItem = BoardItem(
//                                studyId = study.studyId,
//                                title = study.title,
//                                goal = study.goal,
//                                introduction = study.introduction,
//                                memberCount = study.memberCount,
//                                heartCount = study.heartCount,
//                                hitNum = study.hitNum,
//                                maxPeople = study.maxPeople,
//                                studyState = study.studyState,
//                                themeTypes = study.themeTypes,
//                                regions = study.regions,
//                                imageUrl = study.imageUrl,
//                                liked = study.liked,
//                                isHost = false
//                            )
//                            boardItems.add(boardItem)
//                        }
//                        updatePageNumberUI()
//                        binding.myInterestStudyReyclerview.visibility = View.VISIBLE
//                        val totalElements = apiResponse.result.totalElements
//                        binding.checkAmount.text = totalElements.toString()+"건"
//                        updateRecyclerView(boardItems)
//                    } else {
//                        binding.checkAmount.text = "0건"
//                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
//                        binding.myInterestStudyReyclerview.visibility = View.GONE
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                Toast.makeText(requireContext(), "API 호출 실패", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun fetchDataGetInterestCategory(callback: (List<String>?) -> Unit) {
//        val service = RetrofitInstance.retrofit.create(GetInterestCategoryApiService::class.java)
//        service.GetMyInterestStudy(
//        ).enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                if (response.isSuccessful) {
//                    val apiResponse = response.body()
//                    apiResponse?.let {
//                        if (it.isSuccess) {
//                            callback(it.result.themes)
//                        } else {
//                            callback(null)
//                        }
//                    }
//                } else {
//                    callback(null)
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                callback(null)
//            }
//        })
//    }
//
//    private fun updateRecyclerView(boardItems: List<BoardItem>) {
//        interestBoardAdapter.updateList(boardItems)
//    }
//
//    private fun replaceFragment(fragment: Fragment) {
//        (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, fragment)
//            .addToBackStack(null)
//            .commitAllowingStateLoss()
//    }
//
//    private fun getMemberId(context: Context): Int {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val currentEmail = sharedPreferences.getString("currentEmail", null)
//        return if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
//    }
//
//    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
//        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)
//
//        if (memberId != -1) {
//            studyApiService.toggleStudyLike(studyItem.studyId)
//                .enqueue(object : Callback<LikeResponse> {
//                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
//                        if (response.isSuccessful) {
//                            response.body()?.let { likeResponse ->
//                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 BoardItem의 liked 상태 업데이트
//                                val newStatus = likeResponse.result.status
//                                studyItem.liked = newStatus == "LIKE"
//                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
//                                likeButton.setImageResource(newIcon)
//
//                                // heartCount 즉시 증가 또는 감소
//                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1
//
//                                // 변경된 항목을 어댑터에 알림
//                                val adapter = binding.myInterestStudyReyclerview.adapter as InterestVPAdapter
//                                val position = adapter.dataList.indexOf(studyItem)
//                                if (position != -1) {
//                                    adapter.notifyItemChanged(position)
//                                }
//                            }
//                        } else {
//                            Toast.makeText(requireContext(), "찜 상태 업데이트 실패", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//                    override fun onFailure(call: Call<LikeResponse>, t: Throwable) {
//                        Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//                    }
//                })
//        } else {
//            Toast.makeText(requireContext(), "회원 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun updateLikeButtonUI(likeButton: ImageView, isLiked: Boolean) {
//        val newIcon = if (isLiked) R.drawable.ic_heart_filled else R.drawable.study_like
//        likeButton.setImageResource(newIcon)
//    }
//
//    private fun setupPageNavigationButtons() {
//        binding.previousPage.setOnClickListener {
//            if (currentPage > 0) {
//                currentPage--
//                requestPageUpdate()
//            }
//        }
//
//        binding.nextPage.setOnClickListener {
//            if (currentPage < totalPages - 1) {
//                currentPage++
//                requestPageUpdate()
//            }
//        }
//    }
//    private fun requestPageUpdate(){
//        when (selectedStudyCategory) {
//
//            "전체" -> {
//                Log.d("MyInterestStudyFragment","$selectedStudyCategory")
//                fetchMyInterestAll(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    activityFee = activityFee02,
//                    activityFeeAmount = activityFeeAmount,
//                    isOnline = activityFee01,
//                    currentPage = currentPage  // currentPage 유지
//                )
//            }
//            else -> {
//                Log.d("MyInterestStudyFragment","$selectedStudyCategory")
//                fetchMyInterestSpecific(
//                    selectedItem,
//                    gender = gender,
//                    minAge = minAge,
//                    maxAge = maxAge,
//                    isOnline = activityFee01,
//                    activityFee = activityFee02,
//                    activityFeeAmount = activityFeeAmount,
//                    selectedStudyTheme = selectedStudyCategory,
//                    currentPage = currentPage
//                )
//            }
//        }
//    }
//
//    private fun updatePageNumberUI() {
//        binding.currentPage.text = (currentPage + 1).toString()
//
//        binding.previousPage.isEnabled = currentPage > 0
//        binding.previousPage.setTextColor(resources.getColor(
//            if (currentPage > 0) R.color.active_color else R.color.disabled_color,
//            null
//        ))
//
//        binding.nextPage.isEnabled = currentPage < totalPages - 1
//        binding.nextPage.setTextColor(resources.getColor(
//            if (currentPage < totalPages - 1) R.color.active_color else R.color.disabled_color,
//            null
//        ))
//    }
//}
