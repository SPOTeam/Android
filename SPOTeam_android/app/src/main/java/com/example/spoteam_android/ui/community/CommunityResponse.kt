package com.example.spoteam_android.ui.community

import com.example.spoteam_android.ProfileItem

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
    val postId : Int,
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
    val likedByCurrentUser : Boolean,
    val scrapedByCurrentUser : Boolean
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
    val anonymous: Boolean,
    val profileImage : String,
    val writtenTime : String,
    val scrapCount : Int,
    val title : String,
    val content : String,
    var likeCount : Int,
    val commentCount : Int,
    val viewCount : Int,
    var likedByCurrentUser : Boolean,
    var scrapedByCurrentUser : Boolean,
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
    val anonymous: Boolean,
    val profileImage : String,
    val writtenTime : String,
    var likeCount : Int,
    var likedByCurrentUser : Boolean,
    var dislikedByCurrentUser : Boolean
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
    val goal: String,
    val introduction : String,
    val memberCount : Int,
    var heartCount : Int,
    val hitNum : Int,
    val maxPeople : Int,
    val studyState : String,
    val themeTypes : List<String>,
    val regions : List<String>,
    val createdAt : String,
    var liked : Boolean
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
    val result: DisLikeCommentInfo
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
    val result: UnDisLikeCommentInfo
)

data class UnDisLikeCommentInfo (
    val commentId : Int,
    val likeCount : Int,
    val disLikeCount : Int
)

/************스터디 게시글 작성***********/
data class StudyPostResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyPostInfo
)

data class StudyPostInfo (
    val postId : Int,
    val title : String
)

/************스터디 게시글들***********/
data class StudyPostListResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyPostListInfo
)

data class StudyPostListInfo (
    val studyId : Int,
    val posts : List<PostDetail>
)

data class PostDetail (
    val postId : Int,
    val title : String,
    val content : String,
    val theme : String,
    val isAnnouncement : Boolean,
    val createdAt : String,
    val likeNum : Int,
    val hitNum : Int,
    val commentNum : Int,
    val isLiked : Boolean,
)

/************스터디 게시글 내용***********/
data class StudyPostContentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyPostContentInfo
)

data class StudyPostContentInfo (
    val member : MemberInfo,
    val postId : Int,
    val title : String,
    val content : String,
    val theme : String,
    val isAnnouncement : Boolean,
    val createdAt : String,
    val likeNum : Int,
    val hitNum : Int,
    val commentNum : Int,
    val isLiked : Boolean,
    val studyPostImages : List<PostImages>
)

data class PostImages (
    val imageId : Int,
    val imageUrl : String
)

/************스터디 게시글 댓글***********/
data class StudyPostContentCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyPostContentCommentInfo
)

data class StudyPostContentCommentInfo (
    val postId : Int,
    val comments : List<StudyPostContentCommentDetail>
)

data class StudyPostContentCommentDetail(
    val commentId : Int,
    val member : MemberInfo,
    val content : String,
    val likeCount : Int,
    val dislikeCount : Int,
    val isDeleted : Boolean,
    val isLiked : String,
    val applies : List<StudyPostContentCommentDetail>
)

data class MemberInfo (
    val memberId : Int,
    val name : String,
    val profileImage : String
)


/************마이 페이지 멤버 관련 스터디 갯수***********/
data class MyPageStudyNumResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MyPageStudyNumInfo
)

data class MyPageStudyNumInfo (
    val name : String,
    val appliedStudies : Int,
    val ongoingStudies : Int,
    val myRecruitingStudies : Int
)

/************내가 모집하고 있는 스터디***********/
data class MyRecruitingStudiesResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MyRecruitingStudyInfo
)

data class MyRecruitingStudyInfo(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: List<MyRecruitingStudyDetail>,
    val number : Int
)

data class MyRecruitingStudyDetail (
    val studyId : Int,
    val imageUrl : String,
    val title : String,
    val introduction : String,
    val goal : String,
    val memberCount : Int,
    val heartCount : Int,
    val hitNum : Int,
    val maxPeople : Int,
    val studyState : String,
    val themeTypes : List<String>,
    val regions : List<String>,
    val createdAt : String,
    val liked : Boolean
)

/************모집 스터디 참여 신청 멤버들***********/
data class MyStudyAttendanceMemberResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MyStudyAttendanceMemberInfo
)

data class MyStudyAttendanceMemberInfo(
    val totalElements: Int,
    val members : List<AttendanceMemberInfo>
)

data class AttendanceMemberInfo(
    val memberId: Int,
    val nickname: String,
    val profileImage: String
)

/************신청자 소개 결과***********/
data class MemberAttendanceIntroResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MemberIntroInfo
)

data class MemberIntroInfo(
    val memberId: Int,
    val studyId : Int,
    val nickname: String,
    val profileImage : String,
    val introduction : String
)

/************참여 합불 결과***********/
data class MemberAcceptResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MemberAcceptInfo
)

data class MemberAcceptInfo(
    val status: String,
    val updatedAt : String,
)


/************알림 결과***********/
data class AlertResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: AlertInfo
)

data class AlertInfo(
    val notifications: List<AlertDetail>,
    val totalNotificationCount : Int,
    val uncheckedNotificationCount : Int
)

data class AlertDetail(
    val notificationId: Int,
    val studyTitle : String,
    val notifierName : String,
    val type : String,
    val isChecked : Boolean,
    val createdAt : String
)

/************내가 신청한 스터디 알림 결과***********/
data class AlertStudyResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: AlertStudyInfo
)

data class AlertStudyInfo(
    val notifications: List<AlertStudyDetail>,
    val totalNotificationCount : Int,
    val uncheckedNotificationCount : Int
)

data class AlertStudyDetail(
    val notificationId: Int,
    val studyId: Int,
    val studyTitle : String,
    val notifierName : String,
    val type : String,
    val isChecked : Boolean,
    val createdAt : String
)

/************내가 신청한 스터디 수락 알림 처리 결과***********/
data class AcceptedAlertStudyResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: AcceptedAlertStudyInfo
)

data class AcceptedAlertStudyInfo(
    val processedAt : String,
    val accept : Boolean
)

/************내가 신청한 스터디 수락 알림 처리 결과***********/
data class NotificationStateResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: NotificationStateInfo
)

data class NotificationStateInfo(
    val processedAt : String,
    val accept : Boolean
)

/************내가 진행중인 스터디***********/
data class MemberOnStudiesResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MemberOnStudiesInfo
)

data class MemberOnStudiesInfo(
    val totalPages : Int,
    val totalElements : Int,
    val first : Boolean,
    val last : Boolean,
    val size : Int,
    val content : List<MyRecruitingStudyDetail>,
    val number : Int
)

/************내가 참여신청한 스터디***********/
data class MemberAppliedStudiesResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: MemberAppliedStudiesInfo
)

data class MemberAppliedStudiesInfo(
    val totalPages : Int,
    val totalElements : Int,
    val first : Boolean,
    val last : Boolean,
    val size : Int,
    val content : List<MyRecruitingStudyDetail>,
    val number : Int
)

/************스터디 게시글 좋아요 완료***********/
data class StudyContentLikeResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyContentLikeInfo
)

data class StudyContentLikeInfo (
    val postId : Int,
    val title : String,
    val likeNum : Int
)

/************스터디 게시글 좋아요 취소***********/
data class StudyContentUnLikeResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyContentUnLikeInfo
)

data class StudyContentUnLikeInfo (
    val postId : Int,
    val title : String,
    val likeNum : Int
)

/************스터디 게시글 댓글 작성 완료***********/
data class WriteStudyCommentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: WriteStudyCommentInfo
)

data class WriteStudyCommentInfo (
    val isAnonymous : Boolean,
    val content : String
)

/************스터디 멤버 정보***********/
data class StudyMemberResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: StudyMemberInfo
)

data class StudyMemberInfo (
    val totalElements: Int,
    val members : List<MembersDetail>
)

data class MembersDetail (
    val memberId : Int,
    val nickname : String,
    val profileImage : String
)

/************퀴즈 생성 완료***********/
data class QuizContentResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: QuizContentInfo
)

data class QuizContentInfo (
    val createdAt : String,
    val question: String,
    val answer : String
)

/************퀴즈 가져오기 완료***********/
data class GetQuizResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: QuizInfo
)

data class QuizInfo (
    val quizId : Long,
    val question: String
)

