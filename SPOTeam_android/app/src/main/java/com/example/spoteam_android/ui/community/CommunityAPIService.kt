package com.example.spoteam_android.ui.community

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface CommunityAPIService {
    @GET("/spot/posts/best")
    fun getBestCommunityContent(
        @Query("sortType") sortType: String
    ): Call<CommunityResponse>

    @GET("/spot/posts/announcement")
    fun getAnnouncementContent(
    ): Call<AnnouncementResponse>

    @GET("/spot/posts/representative")
    fun getRepresentativeContent(
    ): Call<RepresentativeResponse>

    @GET("/spot/posts")
    fun getCategoryPagesContent(
        @Query("type") type: String,
        @Query("pageNumber") pageNum: Int
    ): Call<CategoryPagesResponse>

    @GET("/spot/posts/{postId}")
    fun getContentInfo(
        @Path("postId") postId: Int
    ): Call<ContentResponse>

    @POST("/spot/posts/{memberId}")
    fun postContent(
        @Path("memberId") memberId: Int,
        @Body requestBody : WriteContentRequest
    ): Call<WriteContentResponse>

    @POST("/spot/posts/{postId}/{memberId}/like")
    fun postContentLike(
        @Path("postId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<ContentLikeResponse>

    @DELETE("/spot/posts/{postId}/{memberId}/like")
    fun deleteContentLike(
        @Path("postId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<ContentUnLikeResponse>

    @POST("/spot/posts/{postId}/{memberId}/comments")
    fun postContentComment(
        @Path("postId") postId: Int,
        @Path("memberId") memberId: Int,
        @Body requestBody : WriteCommentRequest
    ): Call<WriteCommentResponse>

    @GET("/spot/search/studies/theme/")
    fun getCategoryStudy(
        @Query("theme") theme: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String
    ): Call<CategoryStudyResponse>

    @POST("/spot/posts/comments/{commentId}/{memberId}/like")
    fun postCommentLike(
        @Path("commentId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<LikeCommentResponse>

    @DELETE("/spot/posts/comments/{commentId}/{memberId}/like")
    fun deleteCommentLike(
        @Path("commentId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<UnLikeCommentResponse>

    @POST("/spot/posts/comments/{commentId}/{memberId}/dislike")
    fun postCommentDisLike(
        @Path("commentId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<DisLikeCommentResponse>

    @DELETE("/spot/posts/comments/{commentId}/{memberId}/dislike")
    fun deleteCommentDisLike(
        @Path("commentId") postId: Int,
        @Path("memberId") memberId: Int
    ): Call<UnDisLikeCommentResponse>

    @Multipart
    @POST("/spot/studies/{studyId}/posts")
    fun postStudyPost(
        @Path("studyId") studyId: Int,
        @Part("isAnnouncement") isAnnouncementPart: RequestBody,
        @Part("title") titlePart: RequestBody,
        @Part("content") contentPart: RequestBody,
        @Part("theme") themePart: RequestBody,
        @Part imagePart: List<MultipartBody.Part>
    ): Call<StudyPostResponse>

    @GET("/spot/studies/{studyId}/posts")
    fun getStudyPost(
        @Path("studyId") studyId: Int,
        @Query("themeQuery") themeQuery : String,
        @Query("offset") offset : Int,
        @Query("limit") limit : Int
    ): Call<StudyPostListResponse>

    @GET("/spot/studies/{studyId}/posts/{postId}")
    fun getStudyPostContent(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
    ): Call<StudyPostContentResponse>

    @GET("/spot/studies/{studyId}/posts/{postId}/comments")
    fun getStudyPostContentComment(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int
    ): Call<StudyPostContentCommentResponse>

    @GET("/spot/search/studies/my-page/members/{memberId}")
    fun getMyPageStudyNum(
        @Path("memberId") memberId: Int
    ): Call<MyPageStudyNumResponse>

    @GET("/spot/search/studies/my-recruiting-studies/members/{memberId}/")
    fun getMyPageRecruitingStudy(
        @Path("memberId") memberId: Int,
        @Query("page") page: Int,
        @Query("size") size : Int
    ): Call<MyRecruitingStudiesResponse>

    @GET("/spot/studies/{studyId}/applicants")
    fun getMyStudyAttendanceMember(
        @Path("studyId") studyId: Int
    ): Call<MyStudyAttendanceMemberResponse>

    @POST("/spot/studies/{studyId}/applicants/{applicantId}")
    fun postAttendanceMember(
        @Path("studyId") studyId: Int,
        @Path("applicantId") applicantId: Int,
        @Query("isAccept") isAccept: Boolean
    ): Call<MemberAcceptResponse>

    @GET("/spot/studies/{studyId}/applicants/{applicantId}")
    fun getAttendanceIntroduction(
        @Path("studyId") studyId: Int,
        @Path("applicantId") applicantId: Int
    ): Call<MemberAttendanceIntroResponse>

    @GET("/spot/notifications")
    fun getAlert(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<AlertResponse>

    @GET("/spot/notifications/applied-study")
    fun getStudyAlert(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<AlertStudyResponse>

    @POST("/spot/notifications/applied-study/{studyId}/join")
    fun postAcceptedStudyAlert(
        @Path("studyId") studyId: Int,
        @Query("isAccepted") isAccepted: Boolean
    ): Call<AcceptedAlertStudyResponse>

    @POST("/spot/notifications/{notificationId}/read")
    fun postNotificationState(
        @Path("notificationId") notificationId: Int
    ): Call<NotificationStateResponse>

    @GET("/spot/search/studies/on-studies/members/{memberId}")
    fun getMemberOnStudies(
        @Path("memberId") memberId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<MemberOnStudiesResponse>

    @GET("/spot/search/studies/applied-studies/members/{memberId}/")
    fun getMemberAppliedStudies(
        @Path("memberId") memberId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<MemberOnStudiesResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/likes")
    fun postStudyContentLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int
    ): Call<StudyContentLikeResponse>

    @DELETE("/spot/studies/{studyId}/posts/{postId}/likes")
    fun deleteStudyContentLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int
    ): Call<StudyContentUnLikeResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/comments")
    fun postStudyContentComment(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Body requestBody : WriteStudyCommentRequest
    ): Call<WriteStudyCommentResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/comments/{commentId}/replies")
    fun postStudyContentReplyComment(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Body requestBody : WriteStudyCommentRequest
    ): Call<WriteStudyCommentResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/comments/{commentId}/likes")
    fun postMyStudyCommentLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Call<LikeCommentResponse>

    @DELETE("/spot/studies/{studyId}/posts/{postId}/comments/{commentId}/likes")
    fun deleteMyStudyCommentLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Call<UnLikeCommentResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/comments/{commentId}/dislikes")
    fun postMyStudyCommentDisLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Call<DisLikeCommentResponse>

    @DELETE("/spot/studies/{studyId}/posts/{postId}/comments/{commentId}/dislikes")
    fun deleteMyStudyCommentDisLike(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Call<UnDisLikeCommentResponse>
}
