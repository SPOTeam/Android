package com.example.spoteam_android.search

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
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
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.interestarea.InterestVPAdapter
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import com.example.spoteam_android.ui.interestarea.RecruitingStudyApiService
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.calendar.CalendarApiService
import com.example.spoteam_android.ui.study.calendar.Event
import com.example.spoteam_android.ui.study.calendar.ScheduleResponse
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val maxRecentSearches = 10
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var recommendBoardAdapter: InterestVPAdapter
    private lateinit var studyApiService: StudyApiService
    private lateinit var recruitingStudyAdapter: InterestVPAdapter
    private lateinit var searchQueryDao: SearchQueryDao
    private val coroutineScope = MainScope()
    private var searchJob: Job? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container,    false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        binding.editTextSearch.requestFocus()
        binding.editTextSearch.post {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editTextSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        val database = AppDatabase.getDatabase(requireContext())
        searchQueryDao = database.searchQueryDao()

        loadRecentSearches()

        studyViewModel.recentStudyId.observe(viewLifecycleOwner) { recentId ->
            if (recentId != null) {
                fetchRecruitingByRecentId(recentId)
                Log.d("SearchFragment", "최근 조회한 스터디 ID: $recentId")
            } else {
                Log.d("SearchFragment", "최근 조회한 스터디가 없습니다.")
            }
        }

        val chipGroup = binding.chipGroup

        // ChipGroup의 모든 자식 Chip의 클릭 이벤트를 비활성화
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isClickable = false
            chip.isFocusable = false
            chip.isCheckable = false
        }


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

                val detailStudyFragment = DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.recommendationBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        recruitingStudyAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        },studyViewModel = studyViewModel)

        recruitingStudyAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
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
        binding.searchBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recruitingStudyAdapter
        }



        val memberId = getMemberId(requireContext())
        fetchRecommendStudy()

        fetchPopularKeywords()
        updateKeywordTimestamp()

        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.imgSearch.setOnClickListener {
            val query = binding.editTextSearch.text.toString()
            if (query.isNotBlank()) {
                performSearch(query)
            }
        }

        binding.editTextSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                if (query.isNotBlank()) {
                    debouncePerformSearch(query)
                }
                true
            } else {
                false
            }
        }

        binding.icRecommendationRefresh.setOnClickListener {
            fetchRecommendStudy()
        }


        return binding.root
    }

    private fun saveSearchQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            searchQueryDao.insertQuery(SearchQuery(query = query))
            searchQueryDao.deleteOldQueries(maxRecentSearches)

            // UI 업데이트는 메인 스레드에서 수행
            CoroutineScope(Dispatchers.Main).launch {
                loadRecentSearches()
            }
        }
    }

    private fun debouncePerformSearch(query: String) {
        searchJob?.cancel() // 기존 작업 취소
        searchJob = coroutineScope.launch {
            delay(300) // 300ms 대기 (debounce)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        saveSearchQuery(query)
        addSearchChip(query)

        binding.editTextSearch.setText("")
        binding.editTextSearch.clearFocus()

        val bundle = Bundle().apply {
            putString("search_keyword", query)
        }

        val fragment = SearchResultFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadRecentSearches() {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearches = searchQueryDao.getRecentQueries(maxRecentSearches)

            CoroutineScope(Dispatchers.Main).launch {
                if (recentSearches.isEmpty()) {
                    binding.txRecentlySearchedWord.visibility = View.GONE
                } else {
                    binding.txRecentlySearchedWord.visibility = View.VISIBLE
                }
                updateChips(recentSearches.map { it.query })
            }
        }
    }



    private fun updateChips(recentSearches: List<String>) {
        binding.chipGroup.removeAllViews()
        recentSearches.forEach { addSearchChip(it) }
    }

    private fun addSearchChip(query: String) {
        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = true

            setTextColor(ContextCompat.getColor(requireContext(), R.color.search_chip_text))
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(), null, 0, R.style.find_ChipStyle
                )
            )

            setOnCloseIconClickListener {
                removeSearchQuery(query)
            }

            val chipHeight = dpToPx(40)
            val chipMinWidth = dpToPx(20)
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, chipHeight
            )
            minWidth = chipMinWidth

            setOnClickListener {
                binding.editTextSearch.setText(query)
                binding.editTextSearch.setSelection(query.length)  // 커서를 맨 뒤로
                performSearch(query)  // 바로 검색 실행
            }
        }
        binding.chipGroup.addView(chip, 0)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    private fun removeSearchQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            searchQueryDao.deleteQuery(query)

            // UI 업데이트
            CoroutineScope(Dispatchers.Main).launch {
                loadRecentSearches()
            }
        }
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
                        binding.recommendationBoard.visibility = View.VISIBLE
                        binding.txRecommendationStudy.visibility= View.VISIBLE
                        binding.icRecommendationRefresh.visibility= View.VISIBLE

                    } else {
                        binding.recommendationBoard.visibility = View.GONE
                        binding.txRecommendationStudy.visibility= View.GONE
                        binding.icRecommendationRefresh.visibility= View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()

                    }
                } else {
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }

    private fun getMemberId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        return if (currentEmail != null) {
            sharedPreferences.getInt("${currentEmail}_memberId", -1)
        } else {
            -1
        }
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
                                val adapter = binding.recommendationBoard.adapter as InterestVPAdapter
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

    private fun fetchRecruitingByRecentId(recentId: Int) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(GetStudyApiService::class.java)

        service.GetStudyApi(recentId).enqueue(object : Callback<StudyDetailResponse> {
            override fun onResponse(call: Call<StudyDetailResponse>, response: Response<StudyDetailResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        boardItems.clear()
                        val result = apiResponse.result
                        val boardItem = BoardItem(
                            studyId = result.studyId,
                            title = result.studyName,
                            goal = result.goal,
                            introduction = result.introduction,
                            memberCount = result.memberCount,
                            heartCount = result.heartCount,
                            hitNum = result.hitNum,
                            maxPeople = result.maxPeople,
                            studyState = "", // Study state 관련 데이터 추가 필요
                            themeTypes = result.themes,
                            regions = listOf(), // 지역 정보가 없다면 빈 리스트 사용
                            imageUrl = "",
                            liked = false, // Like 정보가 없으면 기본값 사용
                            isHost = false
                        )
                        boardItems.add(boardItem)
                        updateSearchBoard(boardItems)
                    } else {
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("SearchFragment", "API 호출 실패: ${response.code()}")
                    Toast.makeText(requireContext(), "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudyDetailResponse>, t: Throwable) {
                Log.e("SearchFragment", "네트워크 오류: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateKeywordTimestamp() {
        val now = java.util.Calendar.getInstance()
        val baseTime = now.clone() as java.util.Calendar

        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)

        when {
            hour < 13 -> {
                // 어제 18시
                baseTime.add(java.util.Calendar.DATE, -1)
                baseTime.set(java.util.Calendar.HOUR_OF_DAY, 18)
            }
            hour < 18 -> {
                // 오늘 13시
                baseTime.set(java.util.Calendar.HOUR_OF_DAY, 13)
            }
            else -> {
                // 오늘 18시
                baseTime.set(java.util.Calendar.HOUR_OF_DAY, 18)
            }
        }

        baseTime.set(java.util.Calendar.MINUTE, 0)
        baseTime.set(java.util.Calendar.SECOND, 0)
        baseTime.set(java.util.Calendar.MILLISECOND, 0)

        val dateFormat = java.text.SimpleDateFormat("MM.dd", java.util.Locale.getDefault())
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())

        binding.txDate.text = dateFormat.format(baseTime.time)
        binding.txTime.text = timeFormat.format(baseTime.time)
        binding.txDescript.text = "기준"
    }


    private fun fetchPopularKeywords() {
        val keywordItems = arrayListOf<KeywordItem>()
        val service = RetrofitInstance.retrofit.create(PopularKeywordApiService::class.java)

        service.getPopularKeywords().enqueue(object : Callback<PopularKeywordResponse> {
            override fun onResponse(
                call: Call<PopularKeywordResponse>,
                response: Response<PopularKeywordResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("SearchFragment", "$apiResponse")

                    if (apiResponse?.isSuccess == true) {
                        apiResponse.result.keyword.forEach { keywordData ->
                            val keywordItem = KeywordItem(
                                keyword = keywordData.keyword,
                                point = keywordData.point
                            )
                            keywordItems.add(keywordItem)
                        }
                        updateChipGroup(keywordItems)
                    } else {
                        Toast.makeText(requireContext(), "인기 검색어를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("SearchFragment", "API 호출 실패: ${response.code()}")
                    Toast.makeText(requireContext(), "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PopularKeywordResponse>, t: Throwable) {
                Log.e("SearchFragment", "네트워크 오류: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateChipGroup(keywordItems: List<KeywordItem>) {
        val chipGroup = binding.chipGroup2
        chipGroup.removeAllViews()

        for (keyword in keywordItems) {
            val chip = Chip(requireContext()).apply {
                text = "${keyword.keyword}"
                isCloseIconVisible = false // 인기 검색어에서는 닫기 버튼 비활성화
                isClickable = true

                setTextColor(ContextCompat.getColor(requireContext(), R.color.search_chip_text))
                setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        requireContext(), null, 0, R.style.find_ChipStyle
                    )
                )

                val chipHeight = dpToPx(40)
                val chipMinWidth = dpToPx(20)
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, chipHeight
                )
                minWidth = chipMinWidth

                // 클릭 이벤트
                setOnClickListener {
                    binding.editTextSearch.setText(keyword.keyword)
                    binding.editTextSearch.setSelection(keyword.keyword.length)
                    performSearch(keyword.keyword)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun updateSearchBoard(boardItems: List<BoardItem>) {
        recruitingStudyAdapter.updateList(boardItems)

        val hasData = boardItems.isNotEmpty()
        binding.searchBoard.visibility = if (hasData) View.VISIBLE else View.GONE
        binding.txRecentlyViewedStudy.visibility = if (hasData) View.VISIBLE else View.GONE
    }
}
