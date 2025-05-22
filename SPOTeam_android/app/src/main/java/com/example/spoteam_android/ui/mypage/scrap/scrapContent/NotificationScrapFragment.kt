package com.example.spoteam_android.ui.community.communityContent

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
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentScrapContentBinding
import com.example.spoteam_android.ui.community.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationScrapFragment : Fragment() {

    private var _binding: FragmentScrapContentBinding? = null
    private val binding get() = _binding!!
    private var memberId: Int = -1
    private val type = "SPOT_ANNOUNCEMENT"
    private var currentPage = 0
    private val size = 5 // 페이지당 항목 수
    private var totalPages = 0
    private var scrapItemList = ArrayList<CategoryPagesDetail>()
    private lateinit var scrapRVAdapter: ScrapContentRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScrapContentBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1

        setupRecyclerView()
        setupFragmentResultListener()
        setupLookAroundButton()

        fetchPages()

        return binding.root
    }

    private fun setupRecyclerView() {
        scrapRVAdapter = ScrapContentRVAdapter(
            dataList = scrapItemList,
            onPageClick = { selectedPage ->
                currentPage = selectedPage
                fetchPages()
            },
            currentPageProvider = { currentPage },
            totalPagesProvider = { totalPages }
        )

        binding.communityCategoryContentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = scrapRVAdapter
        }

        scrapRVAdapter.setItemClickListener(object : ScrapContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryPagesDetail) {
                val intent = Intent(requireContext(), CommunityContentActivity::class.java)
                intent.putExtra("postInfo", data.postId)
                startActivity(intent)
            }

            override fun onLikeClick(data: CategoryPagesDetail) {
                postLike(data.postId)
            }

            override fun onUnLikeClick(data: CategoryPagesDetail) {
                deleteLike(data.postId)
            }

            override fun onBookMarkClick(data: CategoryPagesDetail) {
                deleteContentScrap(data.postId)
            }

            override fun onUnBookMarkClick(data: CategoryPagesDetail) {
                postContentScrap(data.postId)
            }
        })
    }

    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val result = bundle.getString("resultKey")
            if (result == "SUCCESS") {
                fetchPages()
            }
        }
    }

    private fun setupLookAroundButton() {
        binding.lookAroundTv.setOnClickListener {
            (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
    }

    private fun fetchPages() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getScrapInfo(type, currentPage, size)
            .enqueue(object : Callback<GetScrapResponse> {
                override fun onResponse(
                    call: Call<GetScrapResponse>,
                    response: Response<GetScrapResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result.postResponses ?: emptyList()
                            scrapItemList.clear()
                            scrapItemList.addAll(pagesResponseList)
                            totalPages = pagesResponse.result.totalPage

                            if (scrapItemList.isNotEmpty()) {
                                scrapRVAdapter.updateList(scrapItemList)
                                binding.emptyScrap.visibility = View.GONE
                                binding.communityCategoryContentRv.visibility = View.VISIBLE
                            } else {
                                binding.emptyScrap.visibility = View.VISIBLE
                                binding.communityCategoryContentRv.visibility = View.GONE
                            }
                        } else {
                            showError(pagesResponse?.message)
                            binding.emptyScrap.visibility = View.VISIBLE
                            binding.communityCategoryContentRv.visibility = View.GONE
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<GetScrapResponse>, t: Throwable) {
                    Log.e("ALL", "Failure: ${t.message}", t)
                }
            })
    }

    private fun postLike(postId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentLike(postId).enqueue(object : Callback<ContentLikeResponse> {
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentLike(postId).enqueue(object : Callback<ContentUnLikeResponse> {
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentScrap(postId).enqueue(object : Callback<ContentLikeResponse> {
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentScrap(postId).enqueue(object : Callback<ContentUnLikeResponse> {
            override fun onResponse(call: Call<ContentUnLikeResponse>, response: Response<ContentUnLikeResponse>) {
                if (response.isSuccessful && response.body()?.isSuccess == "true") {
                    fetchPages()
                } else {
                    showError(response.body()?.message)
                }
            }

            override fun onFailure(call: Call<ContentUnLikeResponse>, t: Throwable) {
                Log.e("ScrapContent", "Failure: ${t.message}", t)
            }
        })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
