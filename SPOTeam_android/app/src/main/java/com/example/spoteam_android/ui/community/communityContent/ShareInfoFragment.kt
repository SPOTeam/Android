package com.example.spoteam_android.ui.community.communityContent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.CommunityData
import com.example.spoteam_android.databinding.FragmentCommunityCategoryContentBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.CategoryPagesResponse
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShareInfoFragment : Fragment() {

    lateinit var binding: FragmentCommunityCategoryContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryContentBinding.inflate(inflater, container, false)
        fetchPages("INFORMATION_SHARING", 0)

        return binding.root
    }

    private fun fetchPages(type: String, pageNum: Int) {
        CommunityRetrofitClient.instance.getCategoryPagesContent(type, pageNum)
            .enqueue(object : Callback<CategoryPagesResponse> {
                override fun onResponse(
                    call: Call<CategoryPagesResponse>,
                    response: Response<CategoryPagesResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result?.postResponses
                            Log.d("PASS_EXPERIENCE", "items: $pagesResponseList")
                            if (pagesResponseList != null) {
                                initRecyclerview(pagesResponseList)
                            }
                        } else {
                            showError(pagesResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<CategoryPagesResponse>, t: Throwable) {
                    Log.e("PASS_EXPERIENCE", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(pageContent: List<CategoryPagesDetail>) {
        binding.communityCategoryContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CommunityCategoryContentRVAdapter(pageContent)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object :
            CommunityCategoryContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryPagesDetail) {
                startActivity(Intent(requireContext(), CommunityContentActivity::class.java))
            }

        })
    }
}