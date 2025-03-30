package com.example.spoteam_android.ui.community

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.time.LocalDate

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
        @Query("pageNumber") pageNum: Int,
        @Query("pageSize") pageSize: Int,
    ): Call<CategoryPagesResponse>

    @GET("/spot/posts/{postId}")
    fun getContentInfo(
        @Path("postId") postId: Int,
        @Query("likeOrScrap") isChecked : Boolean
    ): Call<ContentResponse>

    @POST("/spot/posts")
    fun postContent(
        @Body requestBody : WriteContentRequest
    ): Call<WriteContentResponse>

    @PATCH("/spot/posts/{postId}")
    fun editContent(
        @Path("postId") postId: String,
        @Body requestBody : WriteContentRequest
    ): Call<WriteContentResponse>

    @POST("/spot/posts/{postId}/like")
    fun postContentLike(
        @Path("postId") postId: Int,
    ): Call<ContentLikeResponse>

    @DELETE("/spot/posts/{postId}/like")
    fun deleteContentLike(
        @Path("postId") postId: Int,
    ): Call<ContentUnLikeResponse>

    @POST("/spot/posts/{postId}/scrap")
    fun postContentScrap(
        @Path("postId") postId: Int,
    ): Call<ContentLikeResponse>

    @DELETE("/spot/posts/{postId}/scrap")
    fun deleteContentScrap(
        @Path("postId") postId: Int,
    ): Call<ContentUnLikeResponse>

    @POST("/spot/posts/{postId}/comments")
    fun postContentComment(
        @Path("postId") postId: Int,
        @Body requestBody : WriteCommentRequest
    ): Call<WriteCommentResponse>

    @GET("/spot/search/studies/theme/")
    fun getCategoryStudy(
        @Query("theme") theme: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String
    ): Call<CategoryStudyResponse>

    @GET("/spot/search/studies/all/no-conditions")
    fun getAllStudy(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String
    ): Call<CategoryStudyResponse>

    @POST("/spot/posts/comments/{commentId}//like")
    fun postCommentLike(
        @Path("commentId") postId: Int,
    ): Call<LikeCommentResponse>

    @DELETE("/spot/posts/comments/{commentId}/like")
    fun deleteCommentLike(
        @Path("commentId") postId: Int,
    ): Call<UnLikeCommentResponse>

    @POST("/spot/posts/comments/{commentId}/dislike")
    fun postCommentDisLike(
        @Path("commentId") postId: Int,
    ): Call<DisLikeCommentResponse>

    @DELETE("/spot/posts/comments/{commentId}/dislike")
    fun deleteCommentDisLike(
        @Path("commentId") postId: Int,
    ): Call<UnDisLikeCommentResponse>

    @Multipart
    @POST("/spot/studies/{studyId}/posts")
    fun postStudyPost(
        @Path("studyId") studyId: Int,
        @Part("isAnnouncement") isAnnouncementPart: RequestBody,
        @Part("theme") themePart: RequestBody,
        @Part("title") titlePart: RequestBody,
        @Part("content") contentPart: RequestBody,
        @Part images: List<MultipartBody.Part> // 여기에 `images`라는 이름을 사용해야 합니다.
    ): Call<StudyPostResponse>

    @Multipart
    @PATCH("/spot/studies/{studyId}/posts/{postId}")
    fun patchStudyPost(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int,
        @Part("isAnnouncement") isAnnouncementPart: RequestBody,
        @Part("theme") themePart: RequestBody,
        @Part("title") titlePart: RequestBody,
        @Part("content") contentPart: RequestBody,
        @Part images: List<MultipartBody.Part> // 여기에 `images`라는 이름을 사용해야 합니다.
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

    @GET("/spot/search/studies/my-page")
    fun getMyPageStudyNum(
    ): Call<MyPageStudyNumResponse>

    @GET("/spot/search/studies/my-recruiting-studies")
    fun getMyPageRecruitingStudy(
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

    @GET("/spot/search/studies/on-studies")
    fun getMemberOnStudies(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<MemberOnStudiesResponse>

    @GET("/spot/search/studies/applied-studies")
    fun getMemberAppliedStudies(
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

    @GET("/spot/studies/{studyId}/members")
    fun getStudyMembers(
        @Path("studyId") studyId: Int
    ): Call<StudyMemberResponse>

    @POST("/spot/studies/{studyId}/schedules/{scheduleId}/quiz")
    fun makeQuiz(
        @Path("studyId") studyId: Int,
        @Path("scheduleId") scheduleId: Int,
        @Body requestBody : QuizContentRequest
    ): Call<QuizContentResponse>

    @GET("/spot/studies/{studyId}/schedules/{scheduleId}/quiz")
    fun getStudyScheduleQuiz(
        @Path("studyId") studyId: Int,
        @Path("scheduleId") scheduleId: Int,
        @Query("date") date : String
    ) : Call<GetQuizResponse>

    @POST("/spot/studies/{studyId}/schedules/{scheduleId}/attendance")
    fun checkCrewAnswer(
        @Path("studyId") studyId: Int,
        @Path("scheduleId") scheduleId: Int,
        @Body requestBody : CrewAnswerRequest
    ) : Call<GetCrewQuizResponse>

    @GET("/spot/studies/{studyId}/schedules/{scheduleId}/attendance")
    fun getScheduleInfo(
        @Path("studyId") studyId: Int,
        @Path("scheduleId") scheduleId: Int,
        @Query("date") date : String
    ) : Call<GetScheduleResponse>

    @DELETE("/spot/posts/{postId}")
    fun deletePostContent(
        @Path("postId") postId: Int
    ) : Call<DeletePostContentResponse>

    @POST("/spot/studies/{studyId}/members/{memberId}/reports")
    fun reportStudyMember(
        @Path("studyId") studyId: Int,
        @Path("memberId") memberId: Int,
        @Body requestBody : ReportCrewRequest
    ) : Call<ReportCrewResponse>

    @POST("/spot/studies/{studyId}/posts/{postId}/reports")
    fun reportStudyPost(
        @Path("studyId") studyId: Int,
        @Path("postId") postId: Int
    ) : Call<ReportStudyPostResponse>

    @DELETE("/spot/studies/{studyId}/posts/{postId}")
    fun deleteStudyPostContent(
        @Path("studyId") studyId : Int,
        @Path("postId") postId: Int
    ) : Call<DeleteStudyPostContentResponse>

    @GET("/spot/posts/scraps")
    fun getScrapInfo(
        @Query("type") type: String,
        @Query("pageNumber") pageNumber: Int,
        @Query("pageSize") pageSize : Int
    ) : Call<GetScrapResponse>

    @POST("/spot/posts/{postId}/report")
    fun reportCommunityContent(
        @Path("postId") postId : Int
    ) : Call<ReportCommunityContentResponse>

    @PATCH("/spot/studies/{studyId}/termination")
    fun endStudy(
        @Path("studyId") studyId : Int,
        @Query("performance") performance : String
    ) : Call<EndStudyResponse>

    @GET("/spot/studies/{studyId}/host")
    fun getStudyHost(
        @Path("studyId") studyId : Int
    ) : Call<GetHostResponse>
}
