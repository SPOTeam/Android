package com.example.spoteam_android.ui.study

import StudyViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyCommunityBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.PostDetail
import com.example.spoteam_android.ui.community.StudyContentLikeResponse
import com.example.spoteam_android.ui.community.StudyContentUnLikeResponse
import com.example.spoteam_android.ui.community.StudyPostListResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStudyCommunityFragment : Fragment() {

    private lateinit var binding: FragmentMystudyCommunityBinding
    private var currentStudyId : Int = -1
    private val studyViewModel: StudyViewModel by activityViewModels()

    private var itemList = ArrayList<PostDetail>()
    private var currentPage = 0
    private var nextPage = 1
    private val size = 5 // 페이지당 항목 수
    private var startPage = 0

    private var themeQuery : String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMystudyCommunityBinding.inflate(inflater,container,false)

        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )

        pageButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                val selectedPage = startPage + index
                if (currentPage != selectedPage) {
                    currentPage = selectedPage
                    nextPage = currentPage+1
                    fetchPages()
                }
            }
        }

        // 페이지 전환 버튼 설정
        binding.previousPage.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                nextPage--
                fetchPages() // 이전 페이지 데이터를 가져옴
            }
        }

        binding.nextPage.setOnClickListener {
            currentPage++
            nextPage++
            fetchPages() // 다음 페이지 데이터를 가져옴
        }

        binding.writeContentIv.setOnClickListener{
            val fragment = MyStudyWriteContentFragment().apply {
                setStyle(
                    BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
            }
            fragment.show(parentFragmentManager,"MyStudyWriteContent")
        }

        // ViewModel에서 studyId를 관찰하고 변경될 때마다 fetchStudyMembers 호출
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                currentStudyId = studyId
                fetchPages()
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        initBTN()
        return binding.root
    }



    private fun initBTN() {
        binding.allRb.setOnClickListener{
            themeQuery = ""
            fetchPages()
        }
        binding.notiRb.setOnClickListener{
            themeQuery = "ANNOUNCEMENT"
            fetchPages()
        }
        binding.introHelloRb.setOnClickListener{
            themeQuery = "WELCOME"
            fetchPages()
        }
        binding.shareInfoRb.setOnClickListener{
            themeQuery = "INFO_SHARING"
            fetchPages()
        }
        binding.afterStudyRb.setOnClickListener{
            themeQuery = "STUDY_REVIEW"
            fetchPages()
        }
        binding.freeTalkRb.setOnClickListener{
            themeQuery = "FREE_TALK"
            fetchPages()
        }
        binding.qnaRb.setOnClickListener{
            themeQuery = "QNA"
            fetchPages()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
    }

    private fun fetchPages() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPost(currentStudyId, themeQuery, currentPage, size)
            .enqueue(object : Callback<StudyPostListResponse> {
                override fun onResponse(
                    call: Call<StudyPostListResponse>,
                    response: Response<StudyPostListResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val posts = pagesResponse.result.posts
                            if (posts.isNotEmpty()) {

                                binding.fileNoneIv.visibility = View.GONE
                                binding.noneMemberAlertTv.visibility = View.GONE
                                binding.communityCategoryContentRv.visibility = View.VISIBLE
                                binding.pageNumberLayout.visibility = View.VISIBLE

                                itemList.clear()
                                itemList.addAll(posts)

                                initRecyclerview()

                                checkNextPageAvailable()
                            } else {
                                binding.fileNoneIv.visibility = View.VISIBLE
                                binding.noneMemberAlertTv.visibility = View.VISIBLE
                                binding.communityCategoryContentRv.visibility = View.GONE
                                binding.pageNumberLayout.visibility = View.GONE
                            }
                        } else {
                            binding.fileNoneIv.visibility = View.VISIBLE
                            binding.noneMemberAlertTv.visibility = View.VISIBLE
                            binding.writeContentIv.visibility = View.GONE
                            binding.pageNumberLayout.visibility = View.GONE
                            Log.d("OtherStudy1", "PASS")
                            showError(pagesResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyPostListResponse>, t: Throwable) {
                    Log.e("ALL", "Failure: ${t.message}", t)
                }
            })
    }

    private fun checkNextPageAvailable() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPost(currentStudyId, themeQuery, nextPage, size)
            .enqueue(object : Callback<StudyPostListResponse> {
                override fun onResponse(
                    call: Call<StudyPostListResponse>,
                    response: Response<StudyPostListResponse>
                ) {
                    val hasNext = response.body()?.result?.posts?.isNotEmpty() == true
                    binding.nextPage.isEnabled = hasNext
                    updatePageUI(hasNext)
                }

                override fun onFailure(call: Call<StudyPostListResponse>, t: Throwable) {
                    binding.nextPage.isEnabled = false
                    updatePageUI(false)
                }
            })
    }

    private fun postStudyContentLike(postId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postStudyContentLike(currentStudyId, postId)
            .enqueue(object : Callback<StudyContentLikeResponse> {
                override fun onResponse(
                    call: Call<StudyContentLikeResponse>,
                    response: Response<StudyContentLikeResponse>
                ) {
//                    Log.d("StudyLikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        Log.d("StudyLikeContent", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            fetchPages()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyContentLikeResponse>, t: Throwable) {
                    Log.e("StudyLikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteStudyContentLike(postId: Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteStudyContentLike(currentStudyId, postId)
            .enqueue(object : Callback<StudyContentUnLikeResponse> {
                override fun onResponse(
                    call: Call<StudyContentUnLikeResponse>,
                    response: Response<StudyContentUnLikeResponse>
                ) {
                    Log.d("StudyUnLikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        Log.d("StudyUnLikeContent", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            fetchPages()
                        } else {
                            showError(unLikeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyContentUnLikeResponse>, t: Throwable) {
                    Log.e("StudyUnLikeContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(){

        val dataRVAdapter = MyStudyPostRVAdapter(itemList)
        //리스너 객체 생성 및 전달

        Log.d("MYSTUDYCOMMUNITY", "${itemList}")

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object : MyStudyPostRVAdapter.OnItemClickListener {
            override fun onItemClick(data: PostDetail) {
                val intent = Intent(requireContext(), MyStudyPostContentActivity::class.java)
                intent.putExtra("myStudyId", currentStudyId.toString())
                intent.putExtra("myStudyPostId", data.postId.toString())
                startActivity(intent)
            }

            override fun onLikeClick(data: PostDetail) {
                deleteStudyContentLike(data.postId)
            }

            override fun onUnLikeClick(data: PostDetail) {
                postStudyContentLike(data.postId)
            }
        })
    }

    private fun updatePageUI(hasNext: Boolean) {
        val pageButtons = listOf(
            binding.page1, binding.page2, binding.page3, binding.page4, binding.page5
        )

        startPage = maxOf(0, currentPage - 2)
        val maxAvailablePage = if (hasNext) currentPage + 1 else currentPage

        pageButtons.forEachIndexed { index, textView ->
            val pageNum = startPage + index
            if (pageNum <= maxAvailablePage) {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(
                    if (pageNum == currentPage) R.drawable.btn_page_bg else 0
                )
                textView.isEnabled = true
                textView.alpha = 1.0f
                textView.visibility = View.VISIBLE
            } else {
                textView.text = (pageNum + 1).toString()
                textView.setBackgroundResource(0)
                textView.isEnabled = false
                textView.alpha = 0.3f
                textView.visibility = View.VISIBLE
            }
        }

        binding.previousPage.isEnabled = currentPage > 0
    }
}