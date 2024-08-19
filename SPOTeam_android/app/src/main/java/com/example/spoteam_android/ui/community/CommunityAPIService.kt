package com.example.spoteam_android.ui.community

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

    @POST("/spot/studies/{studyId}/posts")
    fun postStudyPost(
        @Path("studyId") studyId: Int,
        @Body requestBody : StudyWriteContentRequest
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
        @Path("postId") postId: Int,
    ): Call<StudyPostContentCommentResponse>
}
