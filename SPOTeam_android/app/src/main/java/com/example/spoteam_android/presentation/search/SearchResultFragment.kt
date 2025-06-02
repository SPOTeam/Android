package com.example.spoteam_android.presentation.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.presentation.home.HomeFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentSearchResultBinding
import com.example.spoteam_android.domain.study.entity.BoardItem
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.presentation.alert.AlertFragment
import com.example.spoteam_android.presentation.interestarea.ApiResponse
import com.example.spoteam_android.presentation.interestarea.InterestVPAdapter
import com.example.spoteam_android.presentation.study.StudyViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchResultFragment : Fragment() {

    private lateinit var interestBoardAdapter: InterestVPAdapter
    private lateinit var binding: FragmentSearchResultBinding
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false)

        interestBoardAdapter = InterestVPAdapter(ArrayList(), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        },studyViewModel = studyViewModel)

        binding.spotLogo.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        // 어댑터 초기화
        interestBoardAdapter.setItemClickListener(object : InterestVPAdapter.OnItemClickListeners {
            override fun onItemClick(data: BoardItem) {
                studyViewModel.setStudyData(
                    data.studyId,
                    data.imageUrl,
                    data.introduction
                )

                val detailStudyFragment =
                    com.example.spoteam_android.presentation.study.DetailStudyFragment()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })

        binding.icFindSearchResult.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, SearchFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.icAlarmSearchResult.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        binding.searchResultStudyReyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = interestBoardAdapter
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

        val checkcount: TextView = binding.checkAmount
        val service = RetrofitInstance.retrofit.create(SearchApiService::class.java)

        service.PostSearchApi(
            keyword = keyword,
            page = 0,
            size = 3,
            sortBy = "ALL"
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.isSuccess == true) {
                        val boardItems = apiResponse.result?.content?.map { study ->
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
                        updateRecyclerView(boardItems)
                        interestBoardAdapter.notifyDataSetChanged()
                        binding.searchResultStudyReyclerview.visibility = View.VISIBLE
                        val itemcount= boardItems.size
                        checkcount.text = String.format("%02d 건", itemcount)
                    } else {
                        checkcount.text = "00 건"
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
            }
        })
    }
    private fun updateRecyclerView(boardItems: List<BoardItem>) {
        interestBoardAdapter.updateList(boardItems)
    }

    private fun toggleLikeStatus(studyItem: BoardItem, likeButton: ImageView) {
        studyViewModel.toggleLikeStatus(studyItem.studyId) { result ->
            requireActivity().runOnUiThread {
                result.onSuccess { liked ->
                    studyItem.liked = liked
                    studyItem.heartCount += if (liked) 1 else -1
                    val icon = if (liked) R.drawable.ic_heart_filled else R.drawable.study_like
                    likeButton.setImageResource(icon)

                    // RecyclerView 갱신
                    interestBoardAdapter.notifyItemChanged(
                        interestBoardAdapter.dataList.indexOf(studyItem)
                    )
                }.onFailure {
                    Toast.makeText(requireContext(), "찜 실패: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
