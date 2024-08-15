package com.example.spoteam_android.ui.category.category_tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCategoryStudyContentBinding

class AllCategoryFragment : Fragment() {

    lateinit var binding: FragmentCategoryStudyContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryStudyContentBinding.inflate(inflater, container, false)

        initRecyclerview()

        return binding.root
    }

//    private fun fetchBestCommunityContent() {
//        CommunityRetrofitClient.instance.getCategoryStudy()
//            .enqueue(object : Callback<CommunityResponse> {
//                override fun onResponse(
//                    call: Call<CommunityResponse>,
//                    response: Response<CommunityResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val communityResponse = response.body()
//                        if (communityResponse?.isSuccess == "true") {
//                            val contentList = communityResponse.result?.postBest5Responses
//                            Log.d("BestCommunity", "items: $contentList")
//                            if (contentList != null) {
//                                initRecyclerview(contentList)
//                            }
//
//                        } else {
//                            showError(communityResponse?.message)
//                        }
//                    } else {
//                        showError(response.code().toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<CommunityResponse>, t: Throwable) {
//                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
//                }
//            })
//    }

    private fun initRecyclerview(){
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val dataList :  ArrayList<BoardItem> = arrayListOf()


//        val dataRVAdapter = CategoryStudyContentRVAdapter(dataList)
        //리스너 객체 생성 및 전달

//        binding.communityCategoryContentRv.adapter = dataRVAdapter

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.contentFilterSp.adapter = adapter
        }
    }
}