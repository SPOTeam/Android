package com.example.spoteam_android.ui.study

import StudyViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.FragmentMystudyCommunityBinding
import com.example.spoteam_android.ui.community.CategoryPagesDetail
import com.example.spoteam_android.ui.community.CategoryPagesResponse
import com.example.spoteam_android.ui.community.CommunityContentActivity
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.PostDetail
import com.example.spoteam_android.ui.community.StudyPostListResponse
import com.example.spoteam_android.ui.community.communityContent.CommunityCategoryContentRVAdapter
import com.example.spoteam_android.ui.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyStudyCommunityFragment : Fragment() {

    private lateinit var binding: FragmentMystudyCommunityBinding
    private var currentStudyId : Int = -1
    private var offset : Int = 0
    private var limit : Int = 5
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
            Log.d("DetailStudyHomeFragment", "Received studyId from ViewModel: $studyId")
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

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.isOnCommunityHome(HomeFragment())
    }

    override fun onResume() {
        super.onResume()
        fetchPages()
        (activity as? MainActivity)?.isOnCommunityHome(MyStudyCommunityFragment())
    }

    private fun fetchPages() {
        CommunityRetrofitClient.instance.getStudyPost(currentStudyId, themeQuery, offset, limit)
            .enqueue(object : Callback<StudyPostListResponse> {
                override fun onResponse(
                    call: Call<StudyPostListResponse>,
                    response: Response<StudyPostListResponse>
                ) {
                    if (response.isSuccessful) {
                        val pagesResponse = response.body()
                        if (pagesResponse?.isSuccess == "true") {
                            val pagesResponseList = pagesResponse.result?.posts
                            Log.d("ALL", "items: $pagesResponseList")
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

                override fun onFailure(call: Call<StudyPostListResponse>, t: Throwable) {
                    Log.e("ALL", "Failure: ${t.message}", t)
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
//
//            override fun onLikeClick(data: CategoryPagesDetail) {
//                postLike(data.postId)
//            }
//
//            override fun onUnLikeClick(data: CategoryPagesDetail) {
//                deleteLike(data.postId)
//            }
        })
    }
}