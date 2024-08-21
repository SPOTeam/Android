package com.example.spoteam_android.ui.category.category_tabs

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding
import com.example.spoteam_android.ui.community.CategoryStudyDetail
import com.example.spoteam_android.ui.community.CategoryStudyResponse
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MajorFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentCategoryStudyContentBinding
    private var selectedSortBy: String = "ALL"
    private lateinit var studyApiService: StudyApiService
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)
        studyApiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contentFilterSp.adapter = adapter
        }

        fetchBestCommunityContent("전공및진로학습", 0, 1, selectedSortBy)

        return binding.root
    }

    private fun fetchBestCommunityContent(theme: String, page: Int, size: Int, sortBy: String) {
        CommunityRetrofitClient.instance.getCategoryStudy(theme, page, size, sortBy)
            .enqueue(object : Callback<CategoryStudyResponse> {
                override fun onResponse(
                    call: Call<CategoryStudyResponse>,
                    response: Response<CategoryStudyResponse>
                ) {
                    if (response.isSuccessful) {
                        val categoryStudyResponse = response.body()
                        if (categoryStudyResponse?.isSuccess == "true") {
                            val contentList = categoryStudyResponse.result?.content
                            Log.d("MajorFragment", "items: $contentList")
                            if (contentList != null) {
                                binding.emptyTv.visibility = View.GONE
                                initRecyclerview(contentList)
                            }
                        } else {
                            binding.emptyTv.visibility = View.VISIBLE
                        }
                    } else {
                        showLog(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<CategoryStudyResponse>, t: Throwable) {
                    Log.e("MajorFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "MajorFragment: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(contentList: List<CategoryStudyDetail>) {
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // contentList를 ArrayList로 변환
        val dataRVAdapter = CategoryStudyContentRVAdapter(ArrayList(contentList), onLikeClick = { selectedItem, likeButton ->
            toggleLikeStatus(selectedItem, likeButton)
        })

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryStudyDetail) {
                // DetailStudyFragment로 이동
                studyViewModel.setStudyData(data.studyId, data.imageUrl, data.introduction)

                val detailStudyFragment = DetailStudyFragment()
                parentFragmentManager.beginTransaction()
                (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frm, detailStudyFragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        })
    }

    fun toggleLikeStatus(studyItem: CategoryStudyDetail, likeButton: ImageView) {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val memberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        if (memberId != -1) {
            studyApiService.toggleStudyLike(studyItem.studyId, memberId)
                .enqueue(object : Callback<LikeResponse> {
                    override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let { likeResponse ->
                                // 서버에서 반환된 상태에 따라 하트 아이콘 및 CategoryStudyDetail의 liked 상태 업데이트
                                val newStatus = likeResponse.result.status
                                studyItem.liked = newStatus == "LIKE"
                                val newIcon = if (studyItem.liked) R.drawable.ic_heart_filled else R.drawable.study_like
                                likeButton.setImageResource(newIcon)

                                // heartCount 즉시 증가 또는 감소
                                studyItem.heartCount = if (studyItem.liked) studyItem.heartCount + 1 else studyItem.heartCount - 1

                                // 변경된 항목을 어댑터에 알림
                                val adapter = binding.communityCategoryContentRv.adapter as CategoryStudyContentRVAdapter
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedItem = parent?.getItemAtPosition(position).toString()
        selectedSortBy = when (selectedItem) {
            "전체" -> "ALL"
            "모집중" -> "RECRUITING"
            "모집완료" -> "COMPLETED"
            "조회수순" -> "HIT"
            "관심순" -> "LIKED"
            else -> "ALL"
        }
        fetchBestCommunityContent("전공및진로학습", 0, 1, selectedSortBy)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedSortBy = "ALL"
    }

}
