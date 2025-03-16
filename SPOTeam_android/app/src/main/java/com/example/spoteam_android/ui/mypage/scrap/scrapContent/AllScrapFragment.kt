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
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityFragment
import com.example.spoteam_android.ui.community.ContentLikeResponse
import com.example.spoteam_android.ui.community.ContentUnLikeResponse
import com.example.spoteam_android.ui.community.GetScrapResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllScrapFragment : Fragment() {

    private var _binding: FragmentScrapContentBinding? = null
    private val binding get() = _binding!!
    private var memberId: Int = -1
    private val type = "ALL"
    private var startPage = 0
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

        // RecyclerView 초기화
        scrapRVAdapter = ScrapContentRVAdapter(scrapItemList)
        binding.communityCategoryContentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = scrapRVAdapter
        }

        // 클릭 리스너 설정
        scrapRVAdapter.setItemClickListener(object : ScrapContentRVAdapter.OnItemClickListener {
            override fun onItemClick(data: CategoryPagesDetail) {
                val intent = Intent(requireContext(), CommunityContentActivity::class.java)
                intent.putExtra("postInfo", data.postId.toString())
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

        // SharedPreferences 사용
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1

        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val result = bundle.getString("resultKey")
            if (result == "SUCCESS") {
                fetchPages()
            }
        }

        binding.lookAroundTv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        return binding.root
    }

    private fun deleteContentScrap(postId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentScrap(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(
                    call: Call<ContentUnLikeResponse>,
                    response: Response<ContentUnLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        if (unLikeResponse?.isSuccess == "true") {
                            onResume()
                        } else {
                            showError(unLikeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentUnLikeResponse>, t: Throwable) {
                    Log.e("UnLikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun postContentScrap(postId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentScrap(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(
                    call: Call<ContentLikeResponse>,
                    response: Response<ContentLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        if (likeResponse?.isSuccess == "true") {
                            onResume()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentLikeResponse>, t: Throwable) {
                    Log.e("LikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun postLike(postId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentLike(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(
                    call: Call<ContentLikeResponse>,
                    response: Response<ContentLikeResponse>
                ) {
//                    Log.d("LikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("LikeContent", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            onResume()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentLikeResponse>, t: Throwable) {
                    Log.e("LikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteLike(postId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentLike(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(
                    call: Call<ContentUnLikeResponse>,
                    response: Response<ContentUnLikeResponse>
                ) {
//                    Log.d("UnLikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
//                        Log.d("UnLikeContent", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            onResume()
                        } else {
                            showError(unLikeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentUnLikeResponse>, t: Throwable) {
                    Log.e("UnLikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    fetchPages()
                }
            }
        }

        setupPageNavigationButtons()
        fetchPages()
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
    }

    private fun fetchPages() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getScrapInfo(type, currentPage, size)
            .enqueue(object : Callback<GetScrapResponse> {
                override fun onResponse(call: Call<GetScrapResponse>, response: Response<GetScrapResponse>) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result.postResponses ?: listOf()
                            scrapItemList.clear()
                            scrapItemList.addAll(pagesResponseList)

                            totalPages = pagesResponse.result.totalPages

                            if (scrapItemList.isNotEmpty()) {
                                scrapRVAdapter.updateList(scrapItemList)
                                updatePageUI()
                                binding.emptyScrap.visibility = View.GONE
                                binding.pageNumberLayout.visibility = View.VISIBLE
                                binding.communityCategoryContentRv.visibility = View.VISIBLE
                            } else {
                                binding.emptyScrap.visibility = View.VISIBLE
                                binding.pageNumberLayout.visibility = View.GONE
                                binding.communityCategoryContentRv.visibility = View.GONE
                            }
                        } else {
                            showError(pagesResponse?.message)
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

    private fun setupPageNavigationButtons() {
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                fetchPages()
            }
        }

        binding.nextPage.setOnClickListener {
            if (currentPage < totalPages - 1) {
                currentPage++
                fetchPages()
            }
        }
    }

    private fun updatePageUI() {
        startPage = if (currentPage <= 2) 0 else minOf(totalPages - 5, maxOf(0, currentPage - 2))
        val endPage = minOf(totalPages, startPage + 5)
        val pages = (startPage until endPage).toList()

        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        pageButtons.forEach { it.text = "" }

        pageButtons.forEachIndexed { index, textView ->
            if (index < pages.size) {
                textView.text = (pages[index] + 1).toString()
                textView.setBackgroundResource(
                    if (pages[index] == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
        binding.nextPage.isEnabled = currentPage < totalPages - 1
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
