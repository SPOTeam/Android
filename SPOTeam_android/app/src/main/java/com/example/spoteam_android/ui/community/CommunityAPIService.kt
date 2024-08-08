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
}
