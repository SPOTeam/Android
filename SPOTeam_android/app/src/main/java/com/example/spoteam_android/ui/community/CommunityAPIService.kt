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
}
