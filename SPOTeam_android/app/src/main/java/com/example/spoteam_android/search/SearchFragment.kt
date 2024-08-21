package com.example.spoteam_android.search


import RetrofitClient.getAuthToken
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.SearchItem
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.example.spoteam_android.ui.interestarea.ApiResponse
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recommendBoardAdapter: BoardAdapter
    private val studyViewModel: StudyViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchAdapter = SearchAdapter(ArrayList()) { selectedItem ->
            Log.d("SearchFragment", "이벤트 클릭: ${selectedItem.title}")
            studyViewModel.setStudyData(
                selectedItem.studyId,
                selectedItem.imageUrl,
                selectedItem.introduction
            )

            // Fragment 전환
            val fragment = DetailStudyFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null)
                .commit()
        }

        recommendBoardAdapter = BoardAdapter(ArrayList()) { selectedItem ->
            Log.d("SearchFragment", "이벤트 클릭: ${selectedItem.title}")
            studyViewModel.setStudyData(
                selectedItem.studyId,
                selectedItem.imageUrl,
                selectedItem.introduction
            )

            // Fragment 전환
            val fragment = DetailStudyFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.searchBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        binding.recommendationBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        val memeberId = getMemberId(requireContext())

        fetchRecommendStudy(memeberId) //추천 스터디

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchChip(it)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    fetchGetSearchStudy(it)  // 검색어를 fetchGetSearchStudy 함수에 전달
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Do nothing for now
                return true
            }
        })
    }

    private fun addSearchChip(query: String) {
        if (query in recentSearches) return

        recentSearches.add(0, query)

        binding.txRecentlySearchedWord.visibility = View.VISIBLE

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = false
            isClickable = false // 클릭 비활성화
            isFocusable = false // 포커스 비활성화
            isChecked = true // 체크된 상태로 강제 설정
            setEnsureMinTouchTargetSize(false) // 최소 터치 크기 비활성화 (선택 사항)
        }

        chip.setChipDrawable(
            ChipDrawable.createFromAttributes(
                requireContext(), null, 0, R.style.find_ChipStyle
            )
        )

        chip.isCloseIconVisible = false
        // Chip의 체크박스 스타일을 유지하면서 클릭은 비활성화
        chip.isCheckable = false // 체크박스를 비활성화하여 클릭 시 상태 변경되지 않도록

        // ChipGroup의 첫 번째 위치에 Chip 추가
        binding.chipGroup.addView(chip, 0)
    }

    private fun fetchGetSearchStudy(keyword: String) {
        Log.d("SearchFragement", "fetchGetSearchStudy() 실행")

        RetrofitClient.SSService.PostSearchApi(
            authToken = getAuthToken(),
            keyword = keyword,
            page = 0,
            size = 3,
            sortBy = "ALL"
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        val searchItems = apiResponse.result?.content?.map { study ->
                            SearchItem(
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
                                createdAt = study.createdAt,
                                liked = study.liked,
                                imageUrl = study.imageUrl
                            )
                        } ?: emptyList()

                        searchAdapter.updateList(searchItems)
                        searchAdapter.notifyDataSetChanged()
                        binding.searchBoard.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun fetchRecommendStudy(memberId: Int) {
        Log.d("SearchFragment", "fetchRecommendStudy() 실행")

        RetrofitClient.GetRSService.GetRecommendStudy(
            authToken = getAuthToken(),
            memberId = memberId,
        ).enqueue(object : Callback<ApiResponse> {
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
                            imageUrl = study.imageUrl
                        )
                    } ?: emptyList()

                    if (boardItems.isNotEmpty()) {
                        recommendBoardAdapter.updateList(boardItems)
                        binding.recommendationBoard.visibility = View.VISIBLE
                    } else {
                        binding.recommendationBoard.visibility = View.GONE
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Log.d("HouseFragment", "연결 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("HouseFragment", "API 호출 실패: ${t.message}")
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
}
