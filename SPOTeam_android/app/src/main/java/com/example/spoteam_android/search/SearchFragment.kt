package com.example.spoteam_android.search

import RetrofitClient.getAuthToken
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var studyViewModel: StudyViewModel
    private lateinit var recommendBoardAdapter: BoardAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        searchAdapter = SearchAdapter(ArrayList()) { selectedItem ->
            studyViewModel.setStudyData(
                selectedItem.studyId,
                selectedItem.imageUrl,
                selectedItem.introduction
            )
            val fragment = DetailStudyFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null)
                .commit()
        }

        recommendBoardAdapter = BoardAdapter(
            ArrayList(),
            onItemClick = { selectedItem ->
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
            },
            onLikeClick = { selectedItem, likeButton ->
//                toggleLikeStatus(selectedItem, likeButton)
            }
        )

        binding.searchBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        binding.recommendationBoard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recommendBoardAdapter
        }

        val memberId = getMemberId(requireContext())
        fetchRecommendStudy(memberId)

        // SearchView의 입력이 완료되면 SearchResultFragment로 이동
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchChip(it)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()

                    // 키워드를 번들로 전달
                    val bundle = Bundle().apply {
                        putString("search_keyword", it)
                    }

                    // SearchResultFragment로 이동
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
                // 사용자가 입력할 때마다 호출되는 부분
                return true
            }
        })

        return binding.root
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
        chip.isCheckable = false
        binding.chipGroup.addView(chip, 0)
    }

    private fun fetchRecommendStudy(memberId: Int) {
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
                    Log.d("SearchFragment", "연결 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("SearchFragment", "API 호출 실패: ${t.message}")
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

}