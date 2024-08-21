package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityMystudyCommunityContentBinding
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.ReportContentFragment
import com.example.spoteam_android.ui.community.StudyContentLikeResponse
import com.example.spoteam_android.ui.community.StudyContentUnLikeResponse
import com.example.spoteam_android.ui.community.StudyPostContentCommentDetail
import com.example.spoteam_android.ui.community.StudyPostContentCommentResponse
import com.example.spoteam_android.ui.community.StudyPostContentInfo
import com.example.spoteam_android.ui.community.StudyPostContentResponse
import com.example.spoteam_android.ui.community.WriteStudyCommentRequest
import com.example.spoteam_android.ui.community.WriteStudyCommentResponse
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
    var parentCommentId : Int? = 0

//    var initialIsliked : Boolean = false
//    var initialIsLikedNum : Int = 0
//    var compareIsliked : Boolean = false
//    var compareIslikedNum : Int = 0

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

        binding.communityContentMoreIv.setOnClickListener{
            showPopupMenu(it)
        }

        binding.applyCommentIv.setOnClickListener {
            if(parentCommentId != 0) {
                submitReplyComment()
            } else {
                submitComment()
            }
        }

        binding.communityContentLikeNumCheckedIv.setOnClickListener{
            deleteStudyContentLike()
        }

        binding.communityContentLikeNumUncheckedIv.setOnClickListener{
            postStudyContentLike()
        }

        fetchContentInfo()
    }

    private fun postStudyContentLike() {
        CommunityRetrofitClient.instance.postStudyContentLike(currentStudyId,postId)
            .enqueue(object : Callback<StudyContentLikeResponse> {
                override fun onResponse(
                    call: Call<StudyContentLikeResponse>,
                    response: Response<StudyContentLikeResponse>
                ) {
                    Log.d("StudyLikeContent", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        Log.d("StudyLikeContent", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {

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

    private fun deleteStudyContentLike() {
        CommunityRetrofitClient.instance.deleteStudyContentLike(currentStudyId, postId)
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

    private fun submitComment() {
        val isAnonymous = binding.writeCommentAnonymous.isChecked
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteStudyCommentRequest(
            isAnonymous = isAnonymous,
            content = commentContent
        )

        // 서버에 댓글 전송
        sendCommentToServer(requestBody)
    }

    private fun sendCommentToServer(requestBody: WriteStudyCommentRequest) {
        Log.d("MyStudyWriteComment", "Reply : $parentCommentId")
        CommunityRetrofitClient.instance.postStudyContentComment(currentStudyId, postId, requestBody)
            .enqueue(object : Callback<WriteStudyCommentResponse> {
                override fun onResponse(
                    call: Call<WriteStudyCommentResponse>,
                    response: Response<WriteStudyCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        Log.d("MyStudyWriteComment", "${response.body()!!.result}")
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        resetAdapterState()
                        fetchContentInfo()
                    } else {
                        Log.d("MyStudyWriteComment", response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<WriteStudyCommentResponse>, t: Throwable) {
                    Toast.makeText(this@MyStudyPostContentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun submitReplyComment() {
        val isAnonymous = binding.writeCommentAnonymous.isChecked
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteStudyCommentRequest(
            isAnonymous = isAnonymous,
            content = commentContent
        )

        // 서버에 댓글 전송
        sendReplyCommentToServer(requestBody)
    }

    private fun sendReplyCommentToServer(requestBody: WriteStudyCommentRequest) {

        Log.d("MyStudyWriteComment", "Reply : $parentCommentId")

        CommunityRetrofitClient.instance.postStudyContentReplyComment(currentStudyId, postId, parentCommentId!!, requestBody)
            .enqueue(object : Callback<WriteStudyCommentResponse> {
                override fun onResponse(
                    call: Call<WriteStudyCommentResponse>,
                    response: Response<WriteStudyCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        Log.d("MyStudyWriteComment", "${response.body()!!.result}")
                        parentCommentId = 0 // postCommentID 초기화
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        resetAdapterState()
                        fetchContentInfo()
                    } else {
                        Log.d("MyStudyWriteComment", response.body()!!.message)
                    }
                }

                override fun onFailure(call: Call<WriteStudyCommentResponse>, t: Throwable) {
                    Toast.makeText(this@MyStudyPostContentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
//        val exit = ExitStudyPopupFragment(view.context)
        val report = ReportContentFragment(view.context)
        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager
        inflater.inflate(R.menu.menu_community_home_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_report -> {
                    report.start(fragmentManager)
                    true
                }
                R.id.edit_content -> {
//                    exit.start() // 편집하기로 수정
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
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
//        compareIsliked = contentInfo.isLiked
//        compareIslikedNum = contentInfo.likeNum
//        initialIsliked = contentInfo.isLiked
//        initialIsLikedNum = contentInfo.likeNum

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

    private fun resetAdapterState() {
        val adapter = binding.contentCommentRv.adapter as? MyStudyContentCommentMultiViewRVAdapter
        adapter?.resetClickedState() // 어댑터 내부의 clickedState 초기화
    }

    private fun initMultiViewRecyclerView(commentInfo: List<StudyPostContentCommentDetail>) {

        binding.contentCommentRv.layoutManager = LinearLayoutManager(this)
        val adapter = MyStudyContentCommentMultiViewRVAdapter(mutableListOf()) // Your RecyclerView Adapter
        binding.contentCommentRv.adapter = adapter
        adapter.setData(commentInfo)

        adapter.itemClick = object : MyStudyContentCommentMultiViewRVAdapter.ItemClick {
            override fun onItemClick(parentId: Int?) {
                if (parentId != 0) {
                    parentCommentId = parentId
                }
                Log.d("MyStudyWriteComment", "parent : $parentId")
                Log.d("MyStudyWriteComment", "parent : $parentCommentId")
            }

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
        }
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

