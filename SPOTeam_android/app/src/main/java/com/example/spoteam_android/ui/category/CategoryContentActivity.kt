//package com.example.spoteam_android.ui.category
//
//import android.os.Bundle
//import android.util.Log
//import android.view.MenuInflater
//import android.view.View
//import android.widget.PopupMenu
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.spoteam_android.R
//import com.example.spoteam_android.databinding.ActivityCommunityContentBinding
//import com.example.spoteam_android.ui.community.CommunityRetrofitClient
//import com.example.spoteam_android.ui.community.ContentLikeResponse
//import com.example.spoteam_android.ui.community.ContentResponse
//import com.example.spoteam_android.ui.community.ContentUnLikeResponse
//import com.example.spoteam_android.ui.community.LikeCommentResponse
//import com.example.spoteam_android.ui.community.ReportContentFragment
//import com.example.spoteam_android.ui.community.UnLikeCommentResponse
//import com.example.spoteam_android.ui.community.WriteCommentRequest
//import com.example.spoteam_android.ui.community.WriteCommentResponse
//import com.example.spoteam_android.ui.community.contentComment.ContentCommentMultiViewRVAdapter
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.text.ParseException
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class CategoryContentActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityCommunityContentBinding
//    var memberId : Int = -1
//    var postId : Int = -1
//    var parentCommentId : Int = 0
//    var initialIsliked : Boolean = false
//    var initialIsLikedNum : Int = 0
//    var compareIsliked : Boolean = false
//    var compareIslikedNum : Int = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        postId = intent.getStringExtra("postInfo")!!.toInt()
//
//        // SharedPreferences 사용
//        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
//        val currentEmail = sharedPreferences.getString("currentEmail", null)
//
//        // 현재 로그인된 사용자 정보를 로그
//        memberId = if (currentEmail != null) sharedPreferences.getInt("${currentEmail}_memberId", -1) else -1
//        Log.d("SharedPreferences", "MemberId: $memberId, PostId : $postId")
//
//        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.communityPrevIv.setOnClickListener{
//            finish()
//        }
//
//        binding.communityContentMoreIv.setOnClickListener{
//            showPopupMenu(it)
//        }
//
//        fetchContentInfo()
//    }
//
//
//    private fun showPopupMenu(view: View) {
//        val popupMenu = PopupMenu(view.context, view)
//        val inflater: MenuInflater = popupMenu.menuInflater
////        val exit = ExitStudyPopupFragment(view.context)
//        val report = ReportContentFragment(view.context)
//        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager
//        inflater.inflate(R.menu.menu_community_home_options, popupMenu.menu)
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.edit_report -> {
//                    report.start(fragmentManager)
//                    true
//                }
//                R.id.edit_content -> {
////                    exit.start() // 편집하기로 수정
//                    true
//                }
//                else -> false
//            }
//        }
//        popupMenu.show()
//    }
//
//    private fun fetchContentInfo() {
//        CommunityRetrofitClient.instance.getContentInfo(postId)
//            .enqueue(object : Callback<ContentResponse> {
//                override fun onResponse(
//                    call: Call<ContentResponse>,
//                    response: Response<ContentResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val contentResponse = response.body()
//                        if (contentResponse?.isSuccess == "true") {
//                            val contentInfo = contentResponse.result
//                            val commentInfo = contentInfo.commentResponses.comments
//
//
//                            Log.d("Content", "items: $contentInfo")
//                            Log.d("Comment", "items: $commentInfo")
//                            initContentInfo(contentInfo)
//
//                            val sortedComments = sortComments(commentInfo)
//                            initMultiViewRecyclerView(sortedComments)
//                        } else {
//                            showError(contentResponse?.message)
//                        }
//                    } else {
//                        showError(response.code().toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<ContentResponse>, t: Throwable) {
//                    Log.e("CommunityContentActivity", "Failure: ${t.message}", t)
//                }
//            })
//    }
//
//    private fun showError(message: String?) {
//        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
//    }
//
//
//    private fun initContentInfo(contentInfo: ContentInfo) {
//        binding.communityContentWriterTv.text =contentInfo.writer
//        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.writtenTime)
//        binding.communityContentSaveNumTv.text = contentInfo.scrapCount.toString()
//        binding.communityContentTitleTv.text = contentInfo.title
//        binding.communityContentContentTv.text = contentInfo.content
//        binding.communityContentLikeNumTv.text = contentInfo.likeCount.toString()
//        binding.communityContentContentNumTv.text = contentInfo.commentCount.toString()
//        binding.communityContentViewNumTv.text = contentInfo.viewCount.toString()
//
//        if(contentInfo.likedByCurrentUser) {
//            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
//            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
//        } else {
//            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
//            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
//        }
//    }
//
//    private fun initMultiViewRecyclerView(commentInfo: List<CommentsInfo>) {
//        binding.contentCommentRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//
//        val dataRVAdapter = ContentCommentMultiViewRVAdapter(commentInfo)
//        binding.contentCommentRv.adapter = dataRVAdapter
//
//        dataRVAdapter.itemClick = object :ContentCommentMultiViewRVAdapter.ItemClick {
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
//    }
//
//    // 어댑터 상태 초기화 함수
//    private fun resetAdapterState() {
//        val adapter = binding.contentCommentRv.adapter as? ContentCommentMultiViewRVAdapter
//        adapter?.resetClickedState() // 어댑터 내부의 clickedState 초기화
//    }
//
//    private fun sortComments(commentInfo: List<CommentsInfo>): List<CommentsInfo> {
//        val sortedList = mutableListOf<CommentsInfo>()
//
//        // 먼저 부모 댓글을 추가
//        val parentComments = commentInfo.filter { it.parentCommentId == 0 }
//        parentComments.forEach { parentComment ->
//            // 부모 댓글을 리스트에 추가
//            sortedList.add(parentComment)
//            // 그 부모 댓글에 해당하는 대댓글을 추가
//            val replies = commentInfo.filter { it.parentCommentId == parentComment.commentId }
//            sortedList.addAll(replies)
//        }
//        Log.d("sortedList", "$sortedList")
//        Log.d("sortedListLength", "${sortedList.size}")
//
//        return sortedList
//    }
//
//    private fun formatWrittenTime(writtenTime: String): String {
//        val formats = arrayOf(
//            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault()),
//            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
//        )
//        var date: Date? = null
//        for (format in formats) {
//            try {
//                date = format.parse(writtenTime)
//                break
//            } catch (e: ParseException) {
//                Log.d("DateParseException", "${e.message}")
//            }
//        }
//        return if (date != null) {
//            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
//        } else {
//            writtenTime // 원본 문자열 반환
//        }
//    }
//}
//
