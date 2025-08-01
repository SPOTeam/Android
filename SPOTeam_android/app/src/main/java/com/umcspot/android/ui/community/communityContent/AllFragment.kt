package com.umcspot.android.ui.community.communityContent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentCommunityCategoryContentBinding
import com.umcspot.android.ui.community.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFragment : Fragment() {

    private lateinit var binding: FragmentCommunityCategoryContentBinding
    private var memberId: Int = -1
    private var itemList = ArrayList<CategoryPagesDetail>()
    private var currentPage = 0
    private val size = 5
    private var totalPages = 0

    private val type = "ALL"
    private lateinit var adapter: CommunityCategoryContentRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryContentBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        memberId = currentEmail?.let { sharedPreferences.getInt("${it}_memberId", -1) } ?: -1

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            if (bundle.getString("resultKey") == "SUCCESS") fetchPages()
        }

        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context)

        adapter = CommunityCategoryContentRVAdapter(itemList,
            onItemClick = { data ->
                val intent = Intent(requireContext(), CommunityContentActivity::class.java)
                intent.putExtra("postInfo", data.postId)
                startActivity(intent)
            },
            onLikeClick = { data -> postLike(data.postId) },
            onUnLikeClick = { data -> deleteLike(data.postId) },
            onBookmarkClick = { data -> deleteContentScrap(data.postId) },
            onUnBookmarkClick = { data -> postContentScrap(data.postId) },
            onPageSelected = { page ->
                currentPage = page
                fetchPages()
            },
            onNextPrevClicked = { isNext ->
                currentPage = when {
                    isNext && currentPage < totalPages - 1 -> currentPage + 1
                    !isNext && currentPage > 0 -> currentPage - 1
                    isNext -> 0
                    else -> totalPages - 1
                }
                fetchPages()
            },
            getCurrentPage = { currentPage },
            getTotalPages = { totalPages }
        )

        binding.communityCategoryContentRv.adapter = adapter

        fetchPages()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
    }

    private fun fetchPages() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getCategoryPagesContent(type, currentPage, size).enqueue(object : Callback<CategoryPagesResponse> {
            override fun onResponse(call: Call<CategoryPagesResponse>, response: Response<CategoryPagesResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.isSuccess == "true") {
                        val content = body.result.postResponses
                        totalPages = body.result.totalPage

                        if (content.isNotEmpty()) {
                            binding.emptyWaiting.visibility = View.GONE
                            itemList.clear()
                            itemList.addAll(content)
                            adapter.updateList(itemList)
                        } else {
                            // 데이터가 없으면 어댑터도 갱신하고 footer 숨기기 위해 빈 리스트 전달
                            binding.emptyWaiting.visibility = View.VISIBLE
                            itemList.clear()
                            adapter.updateList(emptyList())
                        }
                    } else {
                        showError(body?.message)
                    }
                } else {
                    showError(response.code().toString())
                }
            }

            override fun onFailure(call: Call<CategoryPagesResponse>, t: Throwable) {
                Log.e("ALL", "Failure: ${t.message}", t)
            }
        })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun postLike(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .postContentLike(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(call: Call<ContentLikeResponse>, response: Response<ContentLikeResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        fetchPages()
                    } else {
                        showError(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<ContentLikeResponse>, t: Throwable) {
                    Log.e("LikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteLike(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .deleteContentLike(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(call: Call<ContentUnLikeResponse>, response: Response<ContentUnLikeResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        fetchPages()
                    } else {
                        showError(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<ContentUnLikeResponse>, t: Throwable) {
                    Log.e("UnLikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun postContentScrap(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .postContentScrap(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(call: Call<ContentLikeResponse>, response: Response<ContentLikeResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        fetchPages()
                    } else {
                        showError(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<ContentLikeResponse>, t: Throwable) {
                    Log.e("ScrapContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteContentScrap(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .deleteContentScrap(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(call: Call<ContentUnLikeResponse>, response: Response<ContentUnLikeResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        fetchPages()
                    } else {
                        showError(response.body()?.message)
                    }
                }

                override fun onFailure(call: Call<ContentUnLikeResponse>, t: Throwable) {
                    Log.e("UnScrapContent", "Failure: ${t.message}", t)
                }
            })
    }
}
