package com.example.spoteam_android.presentation.study

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyCommunityBinding
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.community.PostDetail
import com.example.spoteam_android.presentation.community.StudyContentLikeResponse
import com.example.spoteam_android.presentation.community.StudyContentUnLikeResponse
import com.example.spoteam_android.presentation.community.StudyPostListResponse
import com.example.spoteam_android.ui.community.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStudyCommunityFragment : Fragment() {

    private lateinit var binding: FragmentMystudyCommunityBinding
    private val studyViewModel: StudyViewModel by activityViewModels()

    private var itemList = ArrayList<PostDetail>()
    private var currentPage = 0
    private var totalPages = 0
    private var currentStudyId: Int = -1
    private var themeQuery: String = ""

    private lateinit var adapter: MyStudyPostRVAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMystudyCommunityBinding.inflate(inflater, container, false)

        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context)

        adapter = MyStudyPostRVAdapter(itemList,
            onItemClick = { post ->
                val intent = Intent(requireContext(), MyStudyPostContentActivity::class.java)
                intent.putExtra("myStudyId", currentStudyId.toString())
                intent.putExtra("myStudyPostId", post.postId.toString())
                startActivity(intent)
            },
            onLikeClick = { post -> deleteStudyContentLike(post.postId) },
            onUnLikeClick = { post -> postStudyContentLike(post.postId) },
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

        binding.writeContentIv.setOnClickListener {
            val fragment = MyStudyWriteContentFragment().apply {
                setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
            }
            fragment.show(parentFragmentManager, "MyStudyWriteContent")
        }

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                currentStudyId = studyId
                fetchPages()
            }
        }

        initCategoryButtons()

        fetchPages()
        return binding.root
    }

    private fun initCategoryButtons() {
        binding.allRb.setOnClickListener { themeQuery = ""; fetchPages() }
        binding.notiRb.setOnClickListener { themeQuery = "ANNOUNCEMENT"; fetchPages() }
        binding.introHelloRb.setOnClickListener { themeQuery = "WELCOME"; fetchPages() }
        binding.shareInfoRb.setOnClickListener { themeQuery = "INFO_SHARING"; fetchPages() }
        binding.afterStudyRb.setOnClickListener { themeQuery = "STUDY_REVIEW"; fetchPages() }
        binding.freeTalkRb.setOnClickListener { themeQuery = "FREE_TALK"; fetchPages() }
        binding.qnaRb.setOnClickListener { themeQuery = "QNA"; fetchPages() }
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
    }

    private fun fetchPages() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPost(currentStudyId, themeQuery, currentPage, 5)
            .enqueue(object : Callback<StudyPostListResponse> {
                override fun onResponse(call: Call<StudyPostListResponse>, response: Response<StudyPostListResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.isSuccess == true) {
                            val posts = body.result.posts
                            itemList.clear()
                            itemList.addAll(posts)
                            totalPages = body.result.totalPages

                            adapter.updateList(itemList)

                            if (posts.isEmpty()) {
                                showEmptyState()
                            } else {
                                showContentState()
                            }
                        } else {
                            showError(body?.message)
                            showEmptyState()
                        }
                    } else {
                        showError("응답 오류: ${response.code()}")
                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<StudyPostListResponse>, t: Throwable) {
                    Log.e("MyStudyCommunity", "Error: ${t.message}", t)
                    showError("네트워크 오류: ${t.message}")
                    showEmptyState()
                }
            })
    }

    private fun showContentState() {
        binding.fileNoneIv.visibility = View.GONE
        binding.noneMemberAlertTv.visibility = View.GONE
        binding.communityCategoryContentRv.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.fileNoneIv.visibility = View.VISIBLE
        binding.noneMemberAlertTv.visibility = View.VISIBLE
        binding.communityCategoryContentRv.visibility = View.GONE
    }

    private fun postStudyContentLike(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .postStudyContentLike(currentStudyId, postId)
            .enqueue(object : Callback<StudyContentLikeResponse> {
                override fun onResponse(call: Call<StudyContentLikeResponse>, response: Response<StudyContentLikeResponse>) {
                    if (response.isSuccessful) {
                        fetchPages()
                    } else {
                        showError("좋아요 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<StudyContentLikeResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                }
            })
    }

    private fun deleteStudyContentLike(postId: Int) {
        RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
            .deleteStudyContentLike(currentStudyId, postId)
            .enqueue(object : Callback<StudyContentUnLikeResponse> {
                override fun onResponse(call: Call<StudyContentUnLikeResponse>, response: Response<StudyContentUnLikeResponse>) {
                    if (response.isSuccessful) {
                        fetchPages()
                    } else {
                        showError("좋아요 취소 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<StudyContentUnLikeResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }
}
