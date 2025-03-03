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
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private var currentStudyId : Int = -1
    private var offset : Int = 0
    private var limit : Int = 30
    private var themeQuery : String = ""
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMystudyCommunityBinding.inflate(inflater,container,false)

        // ViewModel에서 studyId를 관찰하고 변경될 때마다 fetchStudyMembers 호출
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                currentStudyId = studyId
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
        initBTN()

        binding.writeContentIv.setOnClickListener{
            val fragment = MyStudyWriteContentFragment().apply {
                setStyle(
                    BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
            }

            fragment.show(parentFragmentManager,"MyStudyWriteContent")
        }

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
        service.getStudyPost(currentStudyId, themeQuery, offset, limit)
            .enqueue(object : Callback<StudyPostListResponse> {
                override fun onResponse(
                    call: Call<StudyPostListResponse>,
                    response: Response<StudyPostListResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result.posts
                            if (pagesResponseList.isNotEmpty()) {
                                binding.fileNoneIv.visibility = View.GONE
                                binding.noneMemberAlertTv.visibility = View.GONE
                                initRecyclerview(pagesResponseList)
                            } else {
                                binding.fileNoneIv.visibility = View.VISIBLE
                                binding.noneMemberAlertTv.visibility = View.VISIBLE
                            }
                        } else {
                            binding.fileNoneIv.visibility = View.VISIBLE
                            binding.noneMemberAlertTv.visibility = View.VISIBLE
                            binding.writeContentIv.visibility = View.GONE
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

    private fun initRecyclerview(pageContent : List<PostDetail>){
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = MyStudyPostRVAdapter(pageContent)
        //리스너 객체 생성 및 전달

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
}