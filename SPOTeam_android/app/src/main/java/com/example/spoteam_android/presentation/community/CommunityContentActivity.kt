package com.example.spoteam_android.presentation.community

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityCommunityContentBinding
import com.example.spoteam_android.presentation.community.contentComment.ContentCommentMultiViewRVAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityContentActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityCommunityContentBinding
    var postId : Int = -1
    var parentCommentId : Int = 0
    var ischecked : Boolean = false
    var createdByThisMember : Boolean = false
    var scrapByThisMember : Boolean = false
    private var canComment : Boolean = false

    // 추가: 게시물 정보 저장을 위한 변수들
    private var currentTitle: String = ""
    private var currentContent: String = ""
    private var currentType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postId = intent.extras?.getInt("postInfo") ?: -1
        Log.d("CommunityContentActivity", postId.toString())

        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.writeCommentContentEt.setOnFocusChangeListener { _, hasFocus ->
            binding.dismissArea.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        initTextWatcher()

        fetchContentInfo()
        buttonActions()
    }

    private fun initTextWatcher() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.writeCommentContentEt.addTextChangedListener(textWatcher)
    }

    private fun checkFieldsForEmptyValues() {
        val comment = binding.writeCommentContentEt.text.toString().trim()

        if (comment.isEmpty()) {
            // 입력이 있으면 비활성화
            binding.applyCommentIv.setColorFilter(
                ContextCompat.getColor(this, R.color.g300),  // 🔥 빨간색 적용
                PorterDuff.Mode.SRC_IN
            )
            canComment = false

        } else {
            // 입력이 있으면 활성화
            binding.applyCommentIv.setColorFilter(
                ContextCompat.getColor(this, R.color.selector_blue),  // 🔥 원래 색상 적용
                PorterDuff.Mode.SRC_IN
            )
            canComment = true
        }
    }

    private fun buttonActions() {
        binding.communityPrevIv.setOnClickListener{
            finish()
        }

        binding.communityContentMoreIv.setOnClickListener{
            showPopupMenu(it)
        }

        binding.applyCommentIv.setOnClickListener {
            ischecked = true
            if(canComment) {
                submitComment()
            }
        }

        binding.communityContentLikeNumCheckedIv.setOnClickListener {
            ischecked = true
            deleteContentLike()
        }

        binding.communityContentLikeNumUncheckedIv.setOnClickListener{
            ischecked = true
            postContentLike()
        }

        binding.scrapIv.setOnClickListener{
            ischecked = true;
            if(scrapByThisMember) {
                deleteContentScrap()
            } else {
                postContentScrap()
            }
        }

        binding.dismissArea.setOnClickListener {
            // 1. parentCommentId 초기화
            parentCommentId = 0

            // 2. reply 표시 제거
            binding.replyReplyIv.visibility = View.GONE

            // 3. 포커스 해제
            binding.writeCommentContentEt.clearFocus()

            // 👉 4. focus를 다른 곳으로 명시적으로 옮기기 (예: dismissArea 자체로)
            binding.dismissArea.requestFocus()

            // 5. 키보드 내리기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.writeCommentContentEt.windowToken, 0)

            // 6. adapter 상태 초기화
            resetAdapterState()

            // 7. dismissArea 숨기기
            binding.dismissArea.visibility = View.GONE
        }
    }

    private fun postContentScrap() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentScrap(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(
                    call: Call<ContentLikeResponse>,
                    response: Response<ContentLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        if (likeResponse?.isSuccess == true) {
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

    private fun postContentLike() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentLike(postId)
            .enqueue(object : Callback<ContentLikeResponse> {
                override fun onResponse(
                    call: Call<ContentLikeResponse>,
                    response: Response<ContentLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        if (likeResponse?.isSuccess == true) {
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

    private fun deleteContentScrap() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentScrap(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(
                    call: Call<ContentUnLikeResponse>,
                    response: Response<ContentUnLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        if (unLikeResponse?.isSuccess == true) {
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

    private fun deleteContentLike() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteContentLike(postId)
            .enqueue(object : Callback<ContentUnLikeResponse> {
                override fun onResponse(
                    call: Call<ContentUnLikeResponse>,
                    response: Response<ContentUnLikeResponse>
                ) {
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        if (unLikeResponse?.isSuccess == true) {
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
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        val requestBody = WriteCommentRequest(
            content = commentContent,
            anonymous = false,
            parentCommentId = if (parentCommentId == 0) null else parentCommentId
        )
//        Log.d("WriteComment", commentContent)
        // 서버에 댓글 전송

        sendCommentToServer(requestBody)
    }

    private fun sendCommentToServer(requestBody: WriteCommentRequest) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postContentComment(postId, requestBody)
            .enqueue(object : Callback<WriteCommentResponse> {
                override fun onResponse(
                    call: Call<WriteCommentResponse>,
                    response: Response<WriteCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        Log.d("WriteComment", "${response.body()!!.result}")
                        parentCommentId = 0 // postCommentID 초기화
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.writeCommentContentEt.windowToken, 0)
                        // 어댑터의 상태 초기화
                        resetAdapterState()

                        binding.dismissArea.visibility = View.GONE

                        fetchContentInfo()
                    }
                }

                override fun onFailure(call: Call<WriteCommentResponse>, t: Throwable) {
                    Toast.makeText(this@CommunityContentActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun deleteContent(view: View, fragmentManager: FragmentManager) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deletePostContent(postId)
            .enqueue(object : Callback<DeletePostContentResponse> {
                override fun onResponse(
                    call: Call<DeletePostContentResponse>,
                    response: Response<DeletePostContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val deletePostResponse = response.body()
                        if (deletePostResponse?.isSuccess == true) {
                            showSuccessDeleteDialog(view, fragmentManager)

                        } else {
                            showFailedDeleteDialog(view, fragmentManager)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<DeletePostContentResponse>, t: Throwable) {
                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
                }
            })
    }
    private fun showFailedDeleteDialog(view: View, fragmentManager: FragmentManager) {
        val failedDeletePost = FailedDeleteContentDialog(view.context)
        failedDeletePost.start(fragmentManager)
    }

    private fun showSuccessDeleteDialog(view: View, fragmentManager: FragmentManager) {
        val completeDeletePost = DeleteContentDialog(view.context)
        completeDeletePost.start(fragmentManager)
    }

    private fun showPopupMenu(view: View) {

        val popupView =
            LayoutInflater.from(view.context).inflate(R.layout.modify_study_community_menu, null)

        // PopupWindow 생성
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )


        // ✅ "신고하기" 버튼을 동적으로 숨기거나 보이게 설정
        val reportContentItem = popupView.findViewById<TextView>(R.id.study_content_report)
        reportContentItem.isVisible = !createdByThisMember

        // ✅ "수정하기" 버튼을 동적으로 숨기거나 보이게 설정
        val editMenuItem = popupView.findViewById<TextView>(R.id.study_content_edit)
        editMenuItem.isVisible = createdByThisMember // createdByThisMember가 true일 때만 보이도록 설정

        // ✅ "삭제하기" 버튼을 동적으로 숨기거나 보이게 설정
        val deleteMenuItem = popupView.findViewById<TextView>(R.id.study_content_delete)
        deleteMenuItem.isVisible = createdByThisMember

        reportContentItem.setOnClickListener {
            reportContent(view, supportFragmentManager)
            popupWindow.dismiss()
        }

        editMenuItem.setOnClickListener {
            if (createdByThisMember) {

                // WriteContentFragment 생성 및 데이터 전달
                val editContext = EditContentFragment().apply {
                    arguments = Bundle().apply {
                        putInt("postId", postId)
                    }
                    setStyle(
                        BottomSheetDialogFragment.STYLE_NORMAL,
                        R.style.AppBottomSheetDialogBorder20WhiteTheme
                    )
                }
                editContext.show(supportFragmentManager, "EditContent")
            }
            popupWindow.dismiss()
        }

        deleteMenuItem.setOnClickListener {
            deleteContent(view, supportFragmentManager)
            popupWindow.dismiss()
        }
        // 외부 클릭 시 닫힘 설정
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(view.context.getDrawable(R.drawable.custom_popup_background))

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]

        val offsetX = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            75f,
            view.resources.displayMetrics
        ).toInt()

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x - offsetX, y + 55)

    }

    private fun reportContent(view: View, fragmentManager: FragmentManager) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.reportCommunityContent(postId)
            .enqueue(object : Callback<ReportCommunityContentResponse> {
                override fun onResponse(
                    call: Call<ReportCommunityContentResponse>,
                    response: Response<ReportCommunityContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == true) {
                            val reportContentDialog = ReportContentDialog(view.context, fragmentManager)
                            reportContentDialog.start()
                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ReportCommunityContentResponse>, t: Throwable) {
                    Log.e("ReportCommunityContent", "Failure: ${t.message}", t)
                }
            })
    }

    fun fetchContentInfo() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getContentInfo(postId, ischecked)
            .enqueue(object : Callback<ContentResponse> {
                override fun onResponse(
                    call: Call<ContentResponse>,
                    response: Response<ContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == true) {
                            val contentInfo = contentResponse.result
                            val commentInfo = contentInfo.commentResponses.comments
//                            Log.d("CommunityHomeTest", contentInfo.title)
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
        service.postCommentLike(commentId)
            .enqueue(object : Callback<LikeCommentResponse> {
                override fun onResponse(
                    call: Call<LikeCommentResponse>,
                    response: Response<LikeCommentResponse>
                ) {
//                    Log.d("LikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("LikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == true) {
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
        service.deleteCommentLike(commentId)
            .enqueue(object : Callback<UnLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnLikeCommentResponse>,
                    response: Response<UnLikeCommentResponse>
                ) {
//                    Log.d("UnLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
//                        Log.d("UnLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == true) {
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
        service.postCommentDisLike(commentId)
            .enqueue(object : Callback<DisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<DisLikeCommentResponse>,
                    response: Response<DisLikeCommentResponse>
                ) {
//                    Log.d("DisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
//                        Log.d("DisLikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == true) {
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
        service.deleteCommentDisLike(commentId)
            .enqueue(object : Callback<UnDisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnDisLikeCommentResponse>,
                    response: Response<UnDisLikeCommentResponse>
                ) {
//                    Log.d("UnDisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
//                        Log.d("UnDisLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == true) {
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
        createdByThisMember = contentInfo.createdByCurrentUser
        ischecked = false;
        scrapByThisMember = contentInfo.scrapedByCurrentUser

        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.writtenTime)

        if (contentInfo.scrapCount > 999) {
            val formatted = String.format("%.1fK", contentInfo.scrapCount / 1000.0)
            binding.scrapCountTv.text = formatted
        } else {
            binding.scrapCountTv.text = contentInfo.scrapCount.toString()
        }

        binding.communityContentTitleTv.text = contentInfo.title
        binding.communityContentContentTv.text = contentInfo.content

        if (contentInfo.likeCount > 999) {
            binding.communityContentLikeNumTv.text = "${contentInfo.likeCount}+"
        } else {
            binding.communityContentLikeNumTv.text = contentInfo.likeCount.toString()
        }

        if (contentInfo.commentCount > 999) {
            binding.communityContentContentNumTv.text = "${contentInfo.commentCount}+"
        } else {
            binding.communityContentContentNumTv.text = contentInfo.commentCount.toString()
        }

        if (contentInfo.viewCount > 999) {
            binding.communityContentViewNumTv.text = "${contentInfo.viewCount}+"
        } else {
            binding.communityContentViewNumTv.text = contentInfo.viewCount.toString()
        }

        if(contentInfo.type == "PASS_EXPERIENCE") binding.communityContentThemeTv.text = "#합격후기"
        else if(contentInfo.type == "INFORMATION_SHARING") binding.communityContentThemeTv.text = "#정보공유"
        else if(contentInfo.type == "COUNSELING") binding.communityContentThemeTv.text = "#고민상담"
        else if(contentInfo.type == "JOB_TALK") binding.communityContentThemeTv.text = "#취준토크"
        else if(contentInfo.type == "FREE_TALK") binding.communityContentThemeTv.text = "#자유토크"
        else if(contentInfo.type == "SPOT_ANNOUNCEMENT ") binding.communityContentThemeTv.text = "#SPOT공지"

        currentTitle = contentInfo.title
        currentContent = contentInfo.content
        currentType = contentInfo.type

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

        if(contentInfo.imageUrl != null) {
            binding.imageContentIv.visibility = View.VISIBLE
            Glide.with(binding.root.context)
                .load(contentInfo.imageUrl)
                .into(binding.imageContentIv)
        } else {
            binding.imageContentIv.visibility = View.GONE
        }

        if(contentInfo.likedByCurrentUser) {
            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
        } else {
            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
        }

        isScrap(contentInfo.scrapedByCurrentUser)
    }

    private fun isScrap(scraped : Boolean) {
        scrapByThisMember = scraped
        if(scraped) {
            binding.scrapIv.setColorFilter(
                ContextCompat.getColor(this, R.color.selector_blue),  // 🔥 빨간색 적용
                PorterDuff.Mode.SRC_IN
            )
        } else {
            binding.scrapIv.setColorFilter(
                ContextCompat.getColor(this, R.color.black),  // 🔥 빨간색 적용
                PorterDuff.Mode.SRC_IN
            )
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

                if(parentId != null) {
                    binding.replyReplyIv.visibility = View.VISIBLE
                } else {
                    binding.replyReplyIv.visibility = View.GONE
                }

                binding.dismissArea.visibility = View.VISIBLE
            }

            override fun onLikeClick(view: View, position: Int, commentId: Int) {
                ischecked = true;
                deleteCommentLike(commentId)
            }

            override fun onUnLikeClick(view: View, position: Int, commentId: Int) {
                ischecked = true;
                postCommentLike(commentId)
            }

            override fun onDisLikeClick(view: View, position: Int, commentId: Int) {
                ischecked = true;
                deleteCommentDisLike(commentId)
            }

            override fun onUnDisLikeClick(view: View, position: Int, commentId: Int) {
                ischecked = true;
                postCommentDisLike(commentId)
            }
        }
    }

    // 어댑터 상태 초기화 함수
    private fun resetAdapterState() {
        val adapter = binding.contentCommentRv.adapter as? ContentCommentMultiViewRVAdapter
        binding.replyReplyIv.visibility = View.GONE
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

