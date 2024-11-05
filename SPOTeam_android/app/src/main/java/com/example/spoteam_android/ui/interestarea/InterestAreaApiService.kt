package com.example.spoteam_android.ui.interestarea

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface InterestAreaApiService {
    @GET("/spot/search/studies/preferred-region/all/members/{memberId}")
    fun InterestArea(
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

    @GET("/spot/search/studies/interested/main/")
    fun getInterestedBestStudies(
        @Query("memberId") memberId: Int // 필요한 경우 추가
    ): Call<ApiResponse>
}

interface InterestSpecificAreaApiService {
    @GET("/spot/search/studies/preferred-region/specific/members/{memberId}")
    fun InterestSpecificArea(
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
        @Path("memberId") memberId: Int,  // Path parameter for memberId
    ): Call<ApiResponse>
}

interface RecommendStudyApiService{
    @GET("/spot/search/studies/recommend/main/members/{memberId}")
    fun GetRecommendStudy(
        @Path("memberId") memberId: Int,
    ): Call<ApiResponse>
}

interface RecruitingStudyApiService {
    @GET("/spot/search/studies/recruiting")
    fun GetRecruitingStudy(
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

interface MyInterestStudyAllApiService {
    @GET("/spot/search/studies/interest-themes/all/members/{memberId}")
    fun GetMyInterestStudy(
        @Path("memberId") memberId: Int,
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

interface GetInterestCategoryApiService {
    @GET("/spot/member/{memberId}/theme")
    fun GetMyInterestStudy(
        @Path("memberId") memberId: Int,
    ): Call<ApiResponse>
}

interface MyInterestStudySpecificApiService {
    @GET("/spot/search/studies/interest-themes/specific/members/{memberId}/")
    fun GetMyInterestStudys(
        @Path("memberId") memberId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("fee") fee: Int?,
        @Query("theme") theme : String?
    ): Call<ApiResponse>
}








