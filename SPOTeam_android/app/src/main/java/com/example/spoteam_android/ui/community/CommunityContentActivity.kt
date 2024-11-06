package com.example.spoteam_android.ui.community

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityCommunityContentBinding
import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityContentBinding
    var memberId : Int = -1
    var postId : Int = -1
    var parentCommentId : Int = 0
    var initialIsliked : Boolean = false
    var initialIsLikedNum : Int = 0
    var compareIsliked : Boolean = false
    var compareIslikedNum : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postId = intent.getStringExtra("postInfo")!!.toInt()

        // SharedPreferences 사용
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
//        Log.d("SharedPreferences", "MemberId: $memberId, PostId : $postId")

        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.communityPrevIv.setOnClickListener{
//            compareLiked()
            finish()
        }

        binding.communityContentMoreIv.setOnClickListener{
            showPopupMenu(it)
        }

        binding.applyCommentIv.setOnClickListener {
            submitComment()
        }

        binding.communityContentLikeNumCheckedIv.setOnClickListener{
            deleteContentLike()
//            compareIsliked = false
//            compareIslikedNum--
//
//            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
//            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
//
//            binding.communityContentLikeNumTv.text = compareIslikedNum.toString()
        }

        binding.communityContentLikeNumUncheckedIv.setOnClickListener{
            postContentLike()
//            compareIsliked = true
//            compareIslikedNum++
//
//            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
//            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
//
//            binding.communityContentLikeNumTv.text = compareIslikedNum.toString()
        }

        fetchContentInfo()
    }

    private fun compareLiked(){
        if(compareIsliked != initialIsliked){
            if(compareIsliked){
                postContentLike()
            } else {
                deleteContentLike()
            }
        }
    }

    private fun postContentLike() {
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
                            fetchContentInfo()
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

    private fun deleteContentLike() {
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
                            fetchContentInfo()
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

    private fun submitComment() {
        val isAnonymous = binding.writeCommentAnonymous.isChecked
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteCommentRequest(
            content = commentContent,
            parentCommentId = parentCommentId,
            anonymous = isAnonymous
        )

        // 서버에 댓글 전송
        sendCommentToServer(requestBody)
    }

    private fun sendCommentToServer(requestBody: WriteCommentRequest) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentComment(postId, memberId, requestBody)
            .enqueue(object : Callback<WriteCommentResponse> {
                override fun onResponse(
                    call: Call<WriteCommentResponse>,
                    response: Response<WriteCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
//                        Log.d("WriteComment", "${response.body()!!.result}")
                        parentCommentId = 0 // postCommentID 초기화
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.writeCommentContentEt.windowToken, 0)
                        // 어댑터의 상태 초기화
                        resetAdapterState()
                        fetchContentInfo()
                    }
                }

                override fun onFailure(call: Call<WriteCommentResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityContentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val inflater: MenuInflater = popupMenu.menuInflater
//        val exit = ExitStudyPopupFragment(view.context)
        val report = ReportContentDialog(view.context)
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getContentInfo(postId)
            .enqueue(object : Callback<ContentResponse> {
                override fun onResponse(
                    call: Call<ContentResponse>,
                    response: Response<ContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == "true") {
                            val contentInfo = contentResponse.result
                            val commentInfo = contentInfo.commentResponses.comments
//                            compareIsliked = contentInfo.likedByCurrentUser
//                            compareIslikedNum = contentInfo.likeCount
//                            initialIsliked = contentInfo.likedByCurrentUser
//                            initialIsLikedNum = contentInfo.likeCount
//                            //Glide를 사용하여 imageUrl을 ImageView에 로드
//                            Log.d("Content", "items: $contentInfo")
//                            Log.d("Comment", "items: $commentInfo")
                            initContentInfo(contentInfo)

                            val sortedComments = sortComments(commentInfo)
                            initMultiViewRecyclerView(sortedComments)
                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ContentResponse>, t: Throwable) {
                    Log.e("CommunityContentActivity", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun postCommentLike(commentId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postCommentLike(commentId, memberId)
            .enqueue(object : Callback<LikeCommentResponse> {
                override fun onResponse(
                    call: Call<LikeCommentResponse>,
                    response: Response<LikeCommentResponse>
                ) {
//                    Log.d("LikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("LikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<LikeCommentResponse>, t: Throwable) {
                    Log.e("LikeComment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteCommentLike(commentId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteCommentLike(commentId, memberId)
            .enqueue(object : Callback<UnLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnLikeCommentResponse>,
                    response: Response<UnLikeCommentResponse>
                ) {
//                    Log.d("UnLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
//                        Log.d("UnLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                        } else {
                            showError(unLikeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<UnLikeCommentResponse>, t: Throwable) {
                    Log.e("UnLikeComment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun postCommentDisLike(commentId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postCommentDisLike(commentId, memberId)
            .enqueue(object : Callback<DisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<DisLikeCommentResponse>,
                    response: Response<DisLikeCommentResponse>
                ) {
//                    Log.d("DisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("DisLikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                        } else {
                            showError(likeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<DisLikeCommentResponse>, t: Throwable) {
                    Log.e("DisLikeComment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun deleteCommentDisLike(commentId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteCommentDisLike(commentId, memberId)
            .enqueue(object : Callback<UnDisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnDisLikeCommentResponse>,
                    response: Response<UnDisLikeCommentResponse>
                ) {
//                    Log.d("UnDisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
//                        Log.d("UnDisLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                        } else {
                            showError(unLikeResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<UnDisLikeCommentResponse>, t: Throwable) {
                    Log.e("UnDisLikeComment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initContentInfo(contentInfo: ContentInfo) {
        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.writtenTime)
        binding.communityContentSaveNumTv.text = contentInfo.scrapCount.toString()
        binding.communityContentTitleTv.text = contentInfo.title
        binding.communityContentContentTv.text = contentInfo.content
        binding.communityContentLikeNumTv.text = contentInfo.likeCount.toString()
        binding.communityContentContentNumTv.text = contentInfo.commentCount.toString()
        binding.communityContentViewNumTv.text = contentInfo.viewCount.toString()

        if(contentInfo.anonymous) {
            binding.communityContentWriterTv.text = "익명"
            Glide.with(binding.root.context)
                .load(R.drawable.fragment_calendar_spot_logo)
                .into(binding.communityContentProfileIv)
        } else {
            binding.communityContentWriterTv.text =contentInfo.writer
            Glide.with(binding.root.context)
                .load(contentInfo.profileImage)
                .into(binding.communityContentProfileIv)
        }

        if(contentInfo.likedByCurrentUser) {
            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
        } else {
            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
        }
    }

    private fun initMultiViewRecyclerView(commentInfo: List<CommentsInfo>) {
        binding.contentCommentRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = ContentCommentMultiViewRVAdapter(commentInfo)
        binding.contentCommentRv.adapter = dataRVAdapter

        dataRVAdapter.itemClick = object :ContentCommentMultiViewRVAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int, parentId: Int) {
                parentCommentId = parentId
                binding.writeCommentContentEt.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.writeCommentContentEt, InputMethodManager.SHOW_IMPLICIT)
            }

            override fun onLikeClick(view: View, position: Int, commentId: Int) {
                deleteCommentLike(commentId)
            }

            override fun onUnLikeClick(view: View, position: Int, commentId: Int) {
                postCommentLike(commentId)
            }

            override fun onDisLikeClick(view: View, position: Int, commentId: Int) {
                deleteCommentDisLike(commentId)
            }

            override fun onUnDisLikeClick(view: View, position: Int, commentId: Int) {
                postCommentDisLike(commentId)
            }
        }
    }

    // 어댑터 상태 초기화 함수
    private fun resetAdapterState() {
        val adapter = binding.contentCommentRv.adapter as? ContentCommentMultiViewRVAdapter
        adapter?.resetClickedState() // 어댑터 내부의 clickedState 초기화
    }

    private fun sortComments(commentInfo: List<CommentsInfo>): List<CommentsInfo> {
        val sortedList = mutableListOf<CommentsInfo>()

        // 먼저 부모 댓글을 추가
        val parentComments = commentInfo.filter { it.parentCommentId == 0 }
        parentComments.forEach { parentComment ->
            // 부모 댓글을 리스트에 추가
            sortedList.add(parentComment)
            // 그 부모 댓글에 해당하는 대댓글을 추가
            val replies = commentInfo.filter { it.parentCommentId == parentComment.commentId }
            sortedList.addAll(replies)
        }
//        Log.d("sortedList", "$sortedList")
//        Log.d("sortedListLength", "${sortedList.size}")

        return sortedList
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

