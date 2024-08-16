package com.example.spoteam_android.ui.community

/*********게시글 작성 Request********/
data class WriteContentRequest(
    val title: String,
    val content : String,
    val type: String,
    val anonymous : Boolean
)

/*********댓글 작성 Request********/
data class WriteCommentRequest(
    val content: String,
    val parentCommentId : Int,
    val anonymous : Boolean
)
