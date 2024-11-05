package com.example.spoteam_android.ui.study

import StudyApiService
import StudyViewModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMystudyCommunityBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.PostDetail
import com.example.spoteam_android.ui.community.StudyContentLikeResponse
import com.example.spoteam_android.ui.community.StudyContentUnLikeResponse
import com.example.spoteam_android.ui.community.StudyPostListResponse
import com.example.spoteam_android.ui.home.HomeFragment
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
//                fetchStudyMembers(currentStudyId)
//                fetchPages()
            } else {
                Toast.makeText(requireContext(), "Study ID is missing", Toast.LENGTH_SHORT).show()
            }
        }
//        fetchStudyMembers(currentStudyId)
        initBTN()

        return binding.root
    }

    private fun fetchStudyMembers(studyId: Int) {
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList())
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${email}_nickname", null)
        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result?.members ?: emptyList()
                        profileAdapter.updateList(members.map {
                            ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
                        })

                        // 닉네임 리스트에서 현재 사용자의 닉네임과 일치하는지 확인
                        val nicknames = members.map { it.nickname }
                        val isNicknameFound = kakaoNickname?.let { nicknames.contains(it) } ?: false
                        if(isNicknameFound) {
                            binding.categoryHsv.visibility = View.VISIBLE
                            (activity as? MainActivity)?.isOnCommunityHome(MyStudyCommunityFragment())
                            fetchPages()
                        } else {
                            binding.noneMemberAlert.visibility = View.VISIBLE
                        }
                    }
                } else {
//                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
        fetchStudyMembers(currentStudyId)
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
                            val pagesResponseList = pagesResponse.result?.posts
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

                override fun onFailure(call: Call<StudyPostListResponse>, t: Throwable) {
//                    Log.e("ALL", "Failure: ${t.message}", t)
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