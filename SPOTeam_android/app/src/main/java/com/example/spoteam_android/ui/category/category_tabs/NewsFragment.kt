package com.example.spoteam_android.ui.category.category_tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding
import com.example.spoteam_android.ui.community.CategoryStudyDetail
import com.example.spoteam_android.ui.community.CategoryStudyResponse
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    lateinit var binding: FragmentCategoryStudyContentBinding
    private var selectedCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contentFilterSp.adapter = adapter
        }

        fetchBestCommunityContent("시사뉴스", 0, 1, selectedCategory)

        return binding.root
    }

    private fun fetchBestCommunityContent(theme : String, page : Int, size : Int, sortBy : String) {
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
                            Log.d("NewsFragment", "items: $contentList")
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
                    Log.e("NewsFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showLog(message: String?) {
        Toast.makeText(requireContext(), "NewsFragment: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(contentList: List<CategoryStudyDetail>) {
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CategoryStudyContentRVAdapter(contentList)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : CategoryStudyContentRVAdapter.OnItemClickListener{
            override fun onItemClick(data: CategoryStudyDetail) {
                //detailFragmentPreview로 전송
            }

        })
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Spinner에서 선택된 항목의 텍스트를 가져옴
        val selectedItem = parent?.getItemAtPosition(position).toString()

        // 선택된 항목에 따라 카테고리 설정
        selectedCategory = when (selectedItem) {
            "전체" -> "ALL"
            "모집중" -> "RECRUITING"
            "모집완료" -> "COMPLETED"
            "조회수순" -> "HIT"
            "관심순" -> "LIKED"
            else -> "ALL"
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedCategory = "ALL"
    }
}