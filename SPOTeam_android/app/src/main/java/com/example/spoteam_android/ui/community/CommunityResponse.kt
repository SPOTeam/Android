package com.example.spoteam_android.ui.community

/*********Best 인기글 조회********/
data class CommunityResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: CommunityContentInfo
)

data class CommunityContentInfo(
    val sortType: String,
    val postBest5Responses: List<ContentDetailInfo>
)

data class ContentDetailInfo(
    val rank: Int,
    val postTitle: String,
    val commentCount: Int
)
/**********게시판 홈 게시글 조회*************/
data class RepresentativeResponse(
    val isSuccess : String,
    val code : String,
    val message : String,
    val result : RepresentativeContentInfo
)

data class RepresentativeContentInfo(
    val responses: List<RepresentativeDetailInfo>
)

data class RepresentativeDetailInfo (
    val postType : String,
    val postTitle : String,
    val commentCount : Int
)
/************게시판 공지 조회*************/
data class AnnouncementResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: AnnouncementContentInfo
)

data class AnnouncementContentInfo(
    val responses: List<AnnouncementDetailInfo>
)

data class AnnouncementDetailInfo(
    val rank: Int,
    val postTitle: String,
    val commentCount: Int
)
/************게시글 페이지 조회***********/
data class CategoryPagesResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: CategoryPagesInfo
)

data class CategoryPagesInfo(
    val postType : String,
    val postResponses: List<CategoryPagesDetail>,
    val totalPages : Int,
    val totalElements : Int,
    val isFirst : Boolean,
    val isLast : Boolean
)

data class CategoryPagesDetail(
    val postId : Int,
    val writer : String,
    val writtenTime : String,
    val scrapCount : Int,
    val title : String,
    val content : String,
    val likeCount : Int,
    val commentCount : Int,
    val viewCount : Int,
    val likedByCurrentUser : Boolean
)
/************게시글 단건 조회***********/
data class ContentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: ContentInfo
)

data class ContentInfo(
    val type : String,
    val writer: String,
    val writtenTime : String,
    val scrapCount : Int,
    val fileUrls : List<String>,
    val title : String,
    val content : String,
    val likeCount : Int,
    val commentCount : Int,
    val viewCount : Int,
    val likedByCurrentUser : Boolean,
    val commentResponses : CommentResponse,
    val reported : Boolean
)

data class CommentResponse(
    val comments : List<CommentsInfo>
)

data class CommentsInfo(
    val commentId : Int,
    val commentContent : String,
    val parentCommentId : Int,
    val writer : String,
    val writtenTime : String,
    val likeCount : Int,
    val likedByCurrentUser : Boolean,
    val dislikedByCurrentUser : Boolean
)

/************게시글 작성 완료***********/
data class WriteContentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: WriteContentInfo
)

data class WriteContentInfo (
    val id : String,
    val type : String,
    val createAt : String
)

/************게시글 좋아요 완료***********/
data class ContentLikeResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: ContentLikeInfo
)

data class ContentLikeInfo (
    val postId : Int,
    val likeCount : Int,
)

/************게시글 좋아요 취소***********/
data class ContentUnLikeResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: ContentUnLikeInfo
)

data class ContentUnLikeInfo (
    val postId : Int,
    val likeCount : Int,
)

/************댓글 작성 완료***********/
data class WriteCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: WriteCommentInfo
)

data class WriteCommentInfo (
    val id : Int,
    val parentCommentId : Int,
    val content : String,
    val writer : String
)

/************카테고리 별 스터디***********/
data class CategoryStudyResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: CategoryStudyInfo
)

data class CategoryStudyInfo (
    val totalPages : Int,
    val totalElements : Int,
    val first : Boolean,
    val last : Boolean,
    val size : Int,
    val content : List<CategoryStudyDetail>,
    val number : Int
)

data class CategoryStudyDetail (
    val studyId : Int,
    val imageUrl : String,
    val title : String,
    val introduction : String,
    val memberCount : Int,
    val heartCount : Int,
    val hitNum : Int,
    val maxPeople : Int,
    val studyState : String,
    val themeTypes : List<String>,
    val regions : List<Int>,
    val createdAt : String,
    val liked : Boolean
)

/************댓글 좋아요 완료***********/
data class LikeCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: LikeCommentInfo
)

data class LikeCommentInfo (
    val commentId : Int,
    val likeCount : Int,
    val disLikeCount : Int
)

/************댓글 좋아요 취소***********/
data class UnLikeCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: UnLikeCommentInfo
)

data class UnLikeCommentInfo (
    val commentId : Int,
    val likeCount : Int,
    val disLikeCount : Int
)

/************댓글 싫어요 완료***********/
data class DisLikeCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: LikeCommentInfo
)

data class DisLikeCommentInfo (
    val commentId : Int,
    val likeCount : Int,
    val disLikeCount : Int
)

/************댓글 싫어요 취소***********/
data class UnDisLikeCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: UnLikeCommentInfo
)

data class UnDisLikeCommentInfo (
    val commentId : Int,
    val likeCount : Int,
    val disLikeCount : Int
)

