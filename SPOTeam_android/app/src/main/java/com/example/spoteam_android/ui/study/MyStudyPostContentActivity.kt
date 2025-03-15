package com.example.spoteam_android.ui.study

import StudyContentImageRVAdapter
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityMystudyCommunityContentBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.DeleteStudyPostContentResponse
import com.example.spoteam_android.ui.community.DisLikeCommentResponse
import com.example.spoteam_android.ui.community.LikeCommentResponse
import com.example.spoteam_android.ui.community.PostImages
import com.example.spoteam_android.ui.community.ReportContentDialog
import com.example.spoteam_android.ui.community.ReportStudyPostResponse
import com.example.spoteam_android.ui.community.StudyContentLikeResponse
import com.example.spoteam_android.ui.community.StudyContentUnLikeResponse
import com.example.spoteam_android.ui.community.StudyPostContentCommentDetail
import com.example.spoteam_android.ui.community.StudyPostContentCommentResponse
import com.example.spoteam_android.ui.community.StudyPostContentInfo
import com.example.spoteam_android.ui.community.StudyPostContentResponse
import com.example.spoteam_android.ui.community.UnDisLikeCommentResponse
import com.example.spoteam_android.ui.community.UnLikeCommentResponse
import com.example.spoteam_android.ui.community.WriteStudyCommentRequest
import com.example.spoteam_android.ui.community.WriteStudyCommentResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
    var canComment : Boolean = false
    var currentWriter : Boolean = false

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
            if(canComment) {
                if (parentCommentId != 0) {
                    submitReplyComment()
                } else {
                    submitComment()
                }
            }
        }

        binding.communityContentLikeNumCheckedIv.setOnClickListener{
            deleteStudyContentLike()
        }

        binding.communityContentLikeNumUncheckedIv.setOnClickListener{
            postStudyContentLike()
        }

        fetchContentInfo()

        initTextWatcher()
    }

    override fun onResume() {
        super.onResume()
        fetchContentInfo()
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

    private fun postStudyContentLike() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postStudyContentLike(currentStudyId,postId)
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
                            fetchContentInfo()
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
                            fetchContentInfo()
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
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteStudyCommentRequest(
            isAnonymous = false,
            content = commentContent
        )

        // 서버에 댓글 전송
        sendCommentToServer(requestBody)
    }

    private fun sendCommentToServer(requestBody: WriteStudyCommentRequest) {
        Log.d("MyStudyWriteComment", "Reply : $parentCommentId")
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postStudyContentComment(currentStudyId, postId, requestBody)
            .enqueue(object : Callback<WriteStudyCommentResponse> {
                override fun onResponse(
                    call: Call<WriteStudyCommentResponse>,
                    response: Response<WriteStudyCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        Log.d("MyStudyWriteComment", "${response.body()!!.result}")
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.writeCommentContentEt.windowToken, 0)
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
        val commentContent = binding.writeCommentContentEt.text.toString().trim()

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "댓글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val requestBody = WriteStudyCommentRequest(
            isAnonymous = false,
            content = commentContent
        )

        // 서버에 댓글 전송
        sendReplyCommentToServer(requestBody)
    }

    private fun sendReplyCommentToServer(requestBody: WriteStudyCommentRequest) {

        Log.d("MyStudyWriteComment", "Reply : $parentCommentId")
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postStudyContentReplyComment(currentStudyId, postId, parentCommentId!!, requestBody)
            .enqueue(object : Callback<WriteStudyCommentResponse> {
                override fun onResponse(
                    call: Call<WriteStudyCommentResponse>,
                    response: Response<WriteStudyCommentResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == "true") {
                        Log.d("MyStudyWriteComment", "${response.body()!!.result}")
                        parentCommentId = 0 // postCommentID 초기화
                        binding.writeCommentContentEt.text.clear()
                        binding.writeCommentContentEt.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.writeCommentContentEt.windowToken, 0)
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
        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager

        inflater.inflate(R.menu.menu_community_home_options, popupMenu.menu)

        // ✅ "신고하기" 버튼을 동적으로 숨기거나 보이게 설정
        val reportContentItem = popupMenu.menu.findItem(R.id.edit_report)
        reportContentItem.isVisible = !currentWriter

        // ✅ "수정하기" 버튼을 동적으로 숨기거나 보이게 설정
        val editMenuItem = popupMenu.menu.findItem(R.id.edit_content)
        editMenuItem.isVisible = currentWriter

        // ✅ "삭제하기" 버튼을 동적으로 숨기거나 보이게 설정
        val deleteMenuItem = popupMenu.menu.findItem(R.id.delete_content)
        deleteMenuItem.isVisible = currentWriter

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit_report -> {
                    // ✅ 신고 다이얼로그 생성 및 표시
                    reportStudyPost(view, fragmentManager)
                    true
                }

                R.id.edit_content -> {
                    if (currentWriter) {
                        val editContext = MyStudyEditContentFragment().apply {
                            arguments = Bundle().apply {
                                putInt("MyStudyId", currentStudyId)
                                putInt("MyStudyPostId", postId)
                            }
                            setStyle(
                                BottomSheetDialogFragment.STYLE_NORMAL,
                                R.style.AppBottomSheetDialogBorder20WhiteTheme
                            )
                        }
                        editContext.show(fragmentManager, "EditContent")
                    }
                    true
                }

                R.id.delete_content -> {
                    deleteStudyPostContent(view, fragmentManager)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun deleteStudyPostContent(view: View, fragmentManager: FragmentManager) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.deleteStudyPostContent(currentStudyId, postId)
            .enqueue(object : Callback<DeleteStudyPostContentResponse> {
                override fun onResponse(
                    call: Call<DeleteStudyPostContentResponse>,
                    response: Response<DeleteStudyPostContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val deletePostResponse = response.body()
                        if (deletePostResponse?.isSuccess == "true") {
                            showSuccessDeleteDialog(view, fragmentManager)

                        } else {
                            showFailedDeleteDialog(view, fragmentManager)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<DeleteStudyPostContentResponse>, t: Throwable) {
                    Log.e("CommunityHomeFragment", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showFailedDeleteDialog(view: View, fragmentManager: FragmentManager) {
        val failedDeletePost = FailedDeleteStudyContentDialog(view.context)
        failedDeletePost.start(fragmentManager)
    }

    private fun showSuccessDeleteDialog(view: View, fragmentManager: FragmentManager) {
        val completeDeletePost = DeleteStudyContentDialog(view.context)
        completeDeletePost.start(fragmentManager)
    }

    private fun reportStudyPost(view: View, fragmentManager: FragmentManager) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.reportStudyPost(currentStudyId, postId)
            .enqueue(object : Callback<ReportStudyPostResponse> {
                override fun onResponse(
                    call: Call<ReportStudyPostResponse>,
                    response: Response<ReportStudyPostResponse>
                ) {

                    if (response.isSuccessful) {
                        val postContentResponse = response.body()
                        if (postContentResponse?.isSuccess == "true") {
                            val postContent = postContentResponse.result
                            Log.d("ReportedStudyPost", "response: ${postContent.title}")
                            val reportContentDialog = ReportContentDialog(view.context)
                            reportContentDialog.start(fragmentManager) // start() 메서드가 있는 경우 사용
                        } else {
                            showError(postContentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ReportStudyPostResponse>, t: Throwable) {
                    Log.e("MyStudyContent", "Failure: ${t.message}", t)
                }
            })
    }

    fun fetchContentInfo() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPostContent(currentStudyId, postId)
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
                            if(postContent.studyPostImages.isEmpty()) {
                                binding.communityContentImagesRv.visibility = View.GONE
                            } else {
                                binding.communityContentImagesRv.visibility = View.VISIBLE
                            }
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyPostContentComment(currentStudyId, postId)
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

    private fun postCommentLike(commentId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postMyStudyCommentLike(currentStudyId, postId, commentId)
            .enqueue(object : Callback<LikeCommentResponse> {
                override fun onResponse(
                    call: Call<LikeCommentResponse>,
                    response: Response<LikeCommentResponse>
                ) {
                    Log.d("LikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        Log.d("LikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                            fetchContentCommentInfo()
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
        service.deleteMyStudyCommentLike(currentStudyId, postId, commentId)
            .enqueue(object : Callback<UnLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnLikeCommentResponse>,
                    response: Response<UnLikeCommentResponse>
                ) {
                    Log.d("UnLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        Log.d("UnLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                            fetchContentCommentInfo()
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
        service.postMyStudyCommentDisLike(currentStudyId, postId, commentId)
            .enqueue(object : Callback<DisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<DisLikeCommentResponse>,
                    response: Response<DisLikeCommentResponse>
                ) {
                    Log.d("DisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val likeResponse = response.body()
                        Log.d("DisLikeComment", "responseBody: ${likeResponse?.isSuccess}")
                        if (likeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                            fetchContentCommentInfo()
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
        service.deleteMyStudyCommentDisLike(currentStudyId, postId, commentId)
            .enqueue(object : Callback<UnDisLikeCommentResponse> {
                override fun onResponse(
                    call: Call<UnDisLikeCommentResponse>,
                    response: Response<UnDisLikeCommentResponse>
                ) {
                    Log.d("UnDisLikeComment", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val unLikeResponse = response.body()
                        Log.d("UnDisLikeComment", "responseBody: ${unLikeResponse?.isSuccess}")
                        if (unLikeResponse?.isSuccess == "true") {
                            fetchContentInfo()
                            fetchContentCommentInfo()
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

    private fun showError(message: String?) {
        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initContentInfo(contentInfo: StudyPostContentInfo) {
//        compareIsliked = contentInfo.isLiked
//        compareIslikedNum = contentInfo.likeNum
//        initialIsliked = contentInfo.isLiked
//        initialIsLikedNum = contentInfo.likeNum

        binding.communityContentWriterTv.text = contentInfo.member.name
        binding.communityContentDateTv.text = formatWrittenTime(contentInfo.createdAt)
        binding.communityContentTitleTv.text = contentInfo.title
        binding.communityContentContentTv.text = contentInfo.content
        binding.communityContentLikeNumTv.text = contentInfo.likeNum.toString()
        binding.communityContentContentNumTv.text = contentInfo.commentNum.toString()
        binding.communityContentViewNumTv.text = contentInfo.hitNum.toString()

        if(contentInfo.theme == "WELCOME") binding.communityContentThemeTv.text = "#가입인사"
        else if(contentInfo.theme == "INFO_SHARING") binding.communityContentThemeTv.text = "#정보공유"
        else if(contentInfo.theme == "FREE_TALK") binding.communityContentThemeTv.text = "#자유"
        else if(contentInfo.theme == "STUDY_REVIEW") binding.communityContentThemeTv.text = "#스터디후기"
        else if(contentInfo.theme == "QNA") binding.communityContentThemeTv.text = "#Q&A"


        Glide.with(binding.root.context)
            .load(contentInfo.member.profileImage)
            .into(binding.communityContentProfileIv)

        if(contentInfo.studyPostImages.isNotEmpty()) {
            initContentImage(contentInfo.studyPostImages)
        }

        currentWriter = contentInfo.isWriter

        if(contentInfo.isLiked) {
            binding.communityContentLikeNumCheckedIv.visibility = View.VISIBLE
            binding.communityContentLikeNumUncheckedIv.visibility = View.GONE
        } else {
            binding.communityContentLikeNumCheckedIv.visibility = View.GONE
            binding.communityContentLikeNumUncheckedIv.visibility = View.VISIBLE
        }
    }

    private fun initContentImage(studyPostImages: List<PostImages>) {
        binding.communityContentImagesRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val adapter = StudyContentImageRVAdapter(studyPostImages)
        binding.communityContentImagesRv.adapter = adapter
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
                binding.writeCommentContentEt.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.writeCommentContentEt, InputMethodManager.SHOW_IMPLICIT)

                Log.d("MyStudyWriteComment", "parent : $parentId")
                Log.d("MyStudyWriteComment", "parent : $parentCommentId")
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

