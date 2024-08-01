package com.example.spoteam_android

import retrofit2.Call
import retrofit2.http.*

interface CommunityInterface{
    @POST("/spot/{userid}")
    fun getDiary(
        @Path("userid") userId: String
    ): Call<CommunityResponse<CommunityContentInfo>>
}
