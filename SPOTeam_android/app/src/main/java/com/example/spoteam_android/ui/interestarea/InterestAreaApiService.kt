package com.example.spoteam_android.ui.interestarea

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface InterestAreaApiService {
    @GET("/spot/search/studies/preferred-region/all/members/{memberId}")
    fun InterestArea(
        @Header("Authorization") authToken: String,
        @Path("memberId") memberId: Int,  // Path parameter for memberId
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?
    ): Call<ApiResponse>
}

interface InterestSpecificAreaApiService {
    @GET("/spot/search/studies/preferred-region/specific/members/{memberId}")
    fun InterestSpecificArea(
        @Header("Authorization") authToken: String,
        @Path("memberId") memberId: Int,  // Path parameter for memberId
        @Query("regionCode") regionCode: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
    ): Call<ApiResponse>
}

interface GetMemberInterestAreaApiService{
    @GET("/spot/member/{memberId}/region")
    fun GetInterestArea(
        @Header("Authorization") authToken: String,
        @Path("memberId") memberId: Int,  // Path parameter for memberId
    ): Call<ApiResponse>
}

interface RecommendStudyApiService{
    @GET("/spot/search/studies/recommend/main/members/{memberId}")
    fun GetRecommendStudy(
        @Header("Authorization") authToken: String,
        @Path("memberId") memberId: Int,
    ): Call<ApiResponse>
}



