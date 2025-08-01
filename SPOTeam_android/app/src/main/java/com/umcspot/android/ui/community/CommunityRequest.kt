package com.umcspot.android.ui.community

import okhttp3.MultipartBody

/*********게시글 작성 Request********/
data class WriteContentRequest(
    val title: String,
    val content : String,
    val type: String,
    val anonymous : Boolean
)

/*********게시글 댓글 작성 Request********/
data class WriteCommentRequest(
    val content: String,
    val anonymous : Boolean,
    val parentCommentId : Int?
)

/*********내 스터디 게시글 작성 Request********/
data class StudyWriteContentRequest(
    val isAnnouncement: Boolean,
    val theme: String,
    val title: String,
    val content: String,
    val images: List<MultipartBody.Part?>
)

/*********스터디 게시글 댓글 작성 Request********/
data class WriteStudyCommentRequest(
    val isAnonymous : Boolean,
    val content : String
)

/*********퀴즈 생성 Request********/
data class QuizContentRequest(
    val createdAt : String,
    val question : String,
    val answer : String
)

/*********퀴즈 정답 확인 Request********/
data class CrewAnswerRequest(
    val dateTime : String,
    val answer : String
)

/*********스터디원 신고 Request********/
data class ReportCrewRequest(
    val content : String
)