package com.example.spoteam_android.presentation.community

import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
@Serializable
/*********게시글 작성 Request********/
data class WriteContentRequest(
    val title: String,
    val content : String,
    val type: String,
    val anonymous : Boolean
)@Serializable

/*********게시글 댓글 작성 Request********/
data class WriteCommentRequest(
    val content: String,
    val anonymous : Boolean,
    val parentCommentId : Int?
)

@Serializable
/*********스터디 게시글 댓글 작성 Request********/
data class WriteStudyCommentRequest(
    val isAnonymous : Boolean,
    val content : String
)@Serializable

/*********퀴즈 생성 Request********/
data class QuizContentRequest(
    val createdAt : String,
    val question : String,
    val answer : String
)
@Serializable
/*********퀴즈 정답 확인 Request********/
data class CrewAnswerRequest(
    val dateTime : String,
    val answer : String
)
@Serializable
/*********스터디원 신고 Request********/
data class ReportCrewRequest(
    val content : String
)