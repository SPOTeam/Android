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
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCommunityCategoryContentBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.CategoryPagesResponse
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.ContentLikeResponse
import com.example.spoteam_android.ui.community.ContentUnLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFragment : Fragment() {

    lateinit var binding: FragmentCommunityCategoryContentBinding
    var memberId : Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryContentBinding.inflate(inflater, container, false)

        // SharedPreferences 사용
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
//        Log.d("SharedPreferences", "MemberId: $memberId")

        fetchPages("ALL", 0)


        // WriteContentFragment에서 결과 수신
        parentFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val result = bundle.getString("resultKey")
            if (result == "SUCCESS") {
                fetchPages("ALL", 0) // 게시글 등록 성공 시 갱신
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchPages("ALL", 0)
    }

    private fun fetchPages(type : String, pageNum : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getCategoryPagesContent(type, pageNum)
            .enqueue(object : Callback<CategoryPagesResponse> {
                override fun onResponse(
                    call: Call<CategoryPagesResponse>,
                    response: Response<CategoryPagesResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result?.postResponses
//                            Log.d("ALL", "items: $pagesResponseList")
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
                    Log.e("ALL", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteContentScrap(postId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentScrap(postId, memberId)
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
        service.postContentScrap(postId, memberId)
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
        service.postContentLike(postId, memberId)
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
        service.deleteContentLike(postId, memberId)
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

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(pageContent : List<CategoryPagesDetail>) {
        binding.communityCategoryContentRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val reversedPageContent = pageContent.reversed()

        val dataRVAdapter = CommunityCategoryContentRVAdapter(reversedPageContent)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : CommunityCategoryContentRVAdapter.OnItemClickListener {
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

    }
}
