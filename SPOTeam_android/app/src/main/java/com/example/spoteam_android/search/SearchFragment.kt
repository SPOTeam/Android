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

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val gson = Gson()
    private val preferencesName = "RecentSearches"
    private val recentSearchKey = "recent_search_list"
    private val maxRecentSearches = 10
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var recommendBoardAdapter: InterestVPAdapter
    private lateinit var studyApiService: StudyApiService
    private lateinit var recruitingStudyAdapter: InterestVPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        loadRecentSearches()

        studyViewModel.recentStudyId.observe(viewLifecycleOwner) { recentId ->
            if (recentId != null) {
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
        fetchAllRecruiting("ALL")

        fetchPopularKeywords()

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    saveSearchQuery(it)
                    addSearchChip(it)

                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()

                    val bundle = Bundle().apply {
                        putString("search_keyword", it)
                    }

                    val fragment = SearchResultFragment().apply {
                        arguments = bundle
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return binding.root
    }

    private fun saveSearchQuery(query: String) {
        val sharedPreferences = requireContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val currentList = loadRecentSearches()
        val updatedList = (listOf(query) + currentList.filter { it != query }).take(maxRecentSearches)

        val json = gson.toJson(updatedList)
        editor.putString(recentSearchKey, json)
        editor.apply()

        loadRecentSearches() // UI 업데이트
    }

    private fun loadRecentSearches(): List<String> {
        val sharedPreferences = requireContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(recentSearchKey, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(json, type).reversed().also { updateChips(it) }
        } catch (e: Exception) {
            emptyList() // JSON 파싱 실패 시 빈 리스트 반환
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

            setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_chip_text))
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(), null, 0, R.style.find_ChipStyle
                )
            )

            setOnCloseIconClickListener {
                removeSearchQuery(query)
            }

            setOnClickListener {
                binding.searchView.setQuery(query, true)
            }
        }
        binding.chipGroup.addView(chip, 0)
    }

    private fun removeSearchQuery(query: String) {
        val sharedPreferences = requireContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val currentList = loadRecentSearches().filter { it != query }
        val json = gson.toJson(currentList)

        editor.putString(recentSearchKey, json)
        editor.apply()

        loadRecentSearches() // UI 업데이트
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
                            liked = study.liked
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.recommendationBoard.visibility = View.VISIBLE
                    } else {
                        binding.recommendationBoard.visibility = View.GONE
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

    private fun fetchAllRecruiting(selectedItem: String) {
        val boardItems = arrayListOf<BoardItem>()
        val service = RetrofitInstance.retrofit.create(RecruitingStudyApiService::class.java)
        service.GetRecruitingStudy(
            gender = "MALE",
            minAge = 18,
            maxAge = 60,
            isOnline = false,
            hasFee = false,
            fee = null,
            page = 0,
            size = 1,
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
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }
    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        recruitingStudyAdapter.updateList(boardItems)
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

                setTextColor(ContextCompat.getColor(requireContext(), R.color.custom_chip_text))
                setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        requireContext(), null, 0, R.style.find_ChipStyle
                    )
                )

                // 클릭 이벤트
                setOnClickListener {
                    binding.searchView.setQuery(keyword.keyword, true)
                }
            }
            chipGroup.addView(chip)
        }
    }

}
