package com.example.spoteam_android


import RetrofitClient
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.example.spoteam_android.search.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchPages()




        val itemList = ArrayList<BoardItem>()
        itemList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
        itemList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
        itemList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))

        boardAdapter = BoardAdapter(itemList)
        binding.rvBoard2.apply {
            adapter = boardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }




        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchChip(it)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
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

        recentSearches.add(query)
        binding.txRecentlySearchedWord.visibility = View.VISIBLE

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.chipGroup.removeView(this)
                recentSearches.remove(query)
//                if (recentSearches.isEmpty()) {
//                    binding.txRecentlySearchedWord.visibility = View.GONE
//                }
            }
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(), null, 0, R.style.find_ChipStyle
                )
            )
        }

        binding.chipGroup.addView(chip)

    }

    private fun fetchPages() {
        RetrofitClient.apiService.searchStudies("Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MiwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMjczODE0LCJleHAiOjE3MjMyNzc0MTR9.-LB5qbiMrMH5f3gtCTlmVAKnpEOLUMBcff2LYS5CHNk","Kotlin",1,1,"ALL")
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("SearchFragment","pass")
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == false) {
                            val pagesResponseList = pagesResponse.result?.content
                            Log.d("SearchFragment", "items: $pagesResponse")
                            if (pagesResponseList != null) {
                                Log.d("SearchFragment", "items: $pagesResponseList")
                            }
                        }
                    }
//                    else {
//                        Log.d("SearchFragment","{$response}")
//                        // 실패한 경우 서버로부터의 응답 코드를 로그로 출력
//                        Log.e("SearchActivity", "Failed to fetch studies: ${response.code()} - ${response.message()}")
//
//                        // 응답 본문을 문자열로 변환하여 로그로 출력
//                        val errorBody = response.errorBody()?.string()
//                        if (errorBody != null) {
//                            Log.e("SearchActivity", "Error body: $errorBody")
//                        } else {
//                            Log.e("SearchActivity", "Error body is null")
//                        }
//
//                        // 추가 디버깅 정보 출력
//                        Log.e("SearchActivity", "Request URL: ${response.raw().request.url}")
//                        Log.e("SearchActivity", "Request Method: ${response.raw().request.method}")
//                        Log.e("SearchActivity", "Request Headers: ${response.raw().request.headers}")
//                    }

                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e("ALL", "Failure: ${t.message}", t)
                }
            })
    }
}
