package com.example.spoteam_android.ui.study

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.databinding.ActivityMystudyCommunityContentBinding
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.StudyPostContentCommentDetail
import com.example.spoteam_android.ui.community.StudyPostContentCommentResponse
import com.example.spoteam_android.ui.community.StudyPostContentInfo
import com.example.spoteam_android.ui.community.StudyPostContentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyStudyPostContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMystudyCommunityContentBinding
    var currentStudyId : Int = -1
    var postId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentStudyId = intent.getStringExtra("myStudyId")!!.toInt()
        postId = intent.getStringExtra("myStudyPostId")!!.toInt()

        Log.d("MyStudyPostInfo", "$currentStudyId, $postId")

        binding = ActivityMystudyCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.communityPrevIv.setOnClickListener{
            finish()
        }
//
//        binding.communityContentMoreIv.setOnClickListener{
//            showPopupMenu(it)
//        }
//
//        binding.applyCommentIv.setOnClickListener {
//            submitComment()
//        }
//
//        binding.communityContentLikeNumCheckedIv.setOnClickListener{
//            deleteContentLike()
//        }
//
//        binding.communityContentLikeNumUncheckedIv.setOnClickListener{
//            postContentLike()
//        }

        fetchContentInfo()
    }

    private fun fetchContentInfo() {
        CommunityRetrofitClient.instance.getStudyPostContent(currentStudyId, postId)
            .enqueue(object : Callback<StudyPostContentResponse> {
                override fun onResponse(
                    call: Call<StudyPostContentResponse>,
                    response: Response<StudyPostContentResponse>
                ) {

                    if (response.isSuccessful) {
                        val postContentResponse = response.body()
                        if (postContentResponse?.isSuccess == "true") {
                            val postContent = postContentResponse.result
                            Log.d("MyStudyContent", "response: ${postContent}")
                            initContentInfo(postContent)
                            fetchContentCommentInfo()
                        } else {
                            showError(postContentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyPostContentResponse>, t: Throwable) {
                    Log.e("MyStudyContent", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchContentCommentInfo() {
        CommunityRetrofitClient.instance.getStudyPostContentComment(currentStudyId, postId)
            .enqueue(object : Callback<StudyPostContentCommentResponse> {
                override fun onResponse(
                    call: Call<StudyPostContentCommentResponse>,
                    response: Response<StudyPostContentCommentResponse>
                ) {
                    Log.d("MyStudyContentComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val postCommentResponse = response.body()
                        if (postCommentResponse?.isSuccess == "true") {
                            val commentInfo = postCommentResponse.result.comments
                            initMultiViewRecyclerView(commentInfo)
                        } else {
                            showError(postCommentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyPostContentCommentResponse>, t: Throwable) {
                    Log.e("MyStudyContentComment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initContentInfo(contentInfo: StudyPostContentInfo) {
        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.createdAt)
        binding.communityContentTitleTv.text = contentInfo.title
        binding.communityContentContentTv.text = contentInfo.content
        binding.communityContentLikeNumTv.text = contentInfo.likeNum.toString()
        binding.communityContentContentNumTv.text = contentInfo.commentNum.toString()
        binding.communityContentViewNumTv.text = contentInfo.hitNum.toString()

        Glide.with(binding.root.context)
            .load(contentInfo.studyPostImages)
            .into(binding.communityContentImageIv)

        if(contentInfo.isLiked) {
            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
        } else {
            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
        }
    }

    private fun initMultiViewRecyclerView(commentInfo: List<StudyPostContentCommentDetail>) {

        binding.contentCommentRv.layoutManager = LinearLayoutManager(this)
        val adapter = MyStudyContentCommentMultiViewRVAdapter(mutableListOf()) // Your RecyclerView Adapter
        binding.contentCommentRv.adapter = adapter
        adapter.setData(commentInfo)

//        adapter.itemClick = object :MyStudyContentCommentMultiViewRVAdapter.ItemClick {
//            override fun onItemClick(view: View, position: Int, parentId: Int) {
//                parentCommentId = parentId
//            }
//
//            override fun onLikeClick(view: View, position: Int, commentId: Int) {
//                deleteCommentLike(commentId)
//            }
//
//            override fun onUnLikeClick(view: View, position: Int, commentId: Int) {
//                postCommentLike(commentId)
//            }
//
//            override fun onDisLikeClick(view: View, position: Int, commentId: Int) {
//                deleteCommentDisLike(commentId)
//            }
//
//            override fun onUnDisLikeClick(view: View, position: Int, commentId: Int) {
//                postCommentDisLike(commentId)
//            }
//        }
    }

    private fun formatWrittenTime(writtenTime: String): String {
        val formats = arrayOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        )
        var date: Date? = null
        for (format in formats) {
            try {
                date = format.parse(writtenTime)
                break
            } catch (e: ParseException) {
                Log.d("DateParseException", "${e.message}")
            }
        }
        return if (date != null) {
            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
        } else {
            writtenTime // 원본 문자열 반환
        }
    }
}

