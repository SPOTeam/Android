package com.example.spoteam_android.search

import RetrofitClient.getAuthToken
import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.key
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.SearchItem
import com.example.spoteam_android.databinding.FragmentInterestBinding
import com.example.spoteam_android.databinding.FragmentSearchResultBinding
import com.example.spoteam_android.ui.interestarea.InterestFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchResultFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var binding: FragmentSearchResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        // 어댑터 초기화
        searchAdapter = SearchAdapter(ArrayList()) { selectedItem ->
            Log.d("SearchResultFragment", "이벤트 클릭: ${selectedItem.title}")
            // 클릭 시 처리 로직
        }

        // RecyclerView 설정
        binding.searchResultStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        // 전달된 키워드를 통해 API 호출
        val keyword = arguments?.getString("search_keyword")
        keyword?.let {
            fetchGetSearchStudy(it)
        }

        val spinner: Spinner = binding.filterToggle

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        return binding.root
    }

    private fun fetchGetSearchStudy(keyword: String) {
        Log.d("SearchFragement", "fetchGetSearchStudy() 실행")

        val checkcount: TextView = binding.checkAmount
        val searchItems = arrayListOf<SearchItem>()

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
                        binding.searchResultStudyReyclerview.visibility = View.VISIBLE
                        val itemcount= searchItems.size
                        checkcount.text = String.format("%02d 건", itemcount)
                    } else {
                        checkcount.text = "00 건"
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
}
