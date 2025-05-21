package com.example.spoteam_android.ui.interestarea

import com.example.spoteam_android.FinishedStudyResponse
import com.example.spoteam_android.HostApiResponse
import com.example.spoteam_android.HostWithDrawl
import com.example.spoteam_android.WithdrawHostRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface InterestAreaApiService {
    @GET("/spot/search/studies/preferred-region/all")
    fun InterestArea(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("maxFee") maxFee: Int?,
        @Query("minFee") minFee: Int?,
        @Query("themeTypes") themeTypes: List<String>?
    ): Call<ApiResponse>

    @GET("/spot/search/studies/main/interested")
    fun getInterestedBestStudies(
    ): Call<ApiResponse>
}

interface InterestSpecificAreaApiService {
    @GET("/spot/search/studies/preferred-region/specific")
    fun InterestSpecificArea(
        @Query("regionCode") regionCode: String?,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("maxFee") maxFee: Int?,
        @Query("minFee") minFee: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("themeTypes") themeTypes: List<String>?
    ): Call<ApiResponse>
}

interface GetMemberInterestAreaApiService{
    @GET("/spot/members/region")
    fun GetInterestArea(
    ): Call<ApiResponse>
}

interface RecommendStudyApiService{
    @GET("/spot/search/studies/main/recommend")
    fun GetRecommendStudy(
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
        @Query("maxFee") maxFee: Int?,
        @Query("minFee") minFee: Int?,
        @Query("themeTypes") themeTypes: List<String>?,
        @Query("regionCodes") regionCodes: MutableList<String>?
    ): Call<ApiResponse>
}

interface MyInterestStudyAllApiService {
    @GET("/spot/search/studies/interest-themes/all")
    fun GetMyInterestStudy(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("maxFee") maxFee: Int?,
        @Query("minFee") minFee: Int?,
        @Query("regionCodes") regionCodes: MutableList<String>?
    ): Call<ApiResponse>
}

interface GetInterestCategoryApiService {
    @GET("/spot/members/theme")
    fun GetMyInterestStudy(
    ): Call<ApiResponse>
}

interface MyInterestStudySpecificApiService {
    @GET("/spot/search/studies/interest-themes/specific")
    fun GetMyInterestStudys(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
        @Query("gender") gender: String?,
        @Query("minAge") minAge: Int?,
        @Query("maxAge") maxAge: Int?,
        @Query("isOnline") isOnline: Boolean?,
        @Query("hasFee") hasFee: Boolean?,
        @Query("maxFee") maxFee: Int?,
        @Query("minFee") minFee: Int?,
        @Query("theme") theme: String?,
        @Query("regionCodes") regionCodes: MutableList<String>?
    ): Call<ApiResponse>
}


interface GetHostInterface {
    @GET("/spot/studies/{studyId}/host")
    fun getHost(
        @Path("studyId") studyId: Int
    ): Call<HostApiResponse>


    @HTTP(method = "DELETE", path = "/spot/studies/{studyId}/hosts/withdrawal", hasBody = true)
    fun withDrawlHost(
        @Path("studyId") studyId: Int,
        @Body body: WithdrawHostRequest
    ): Call<HostWithDrawl>

    @HTTP(method = "DELETE", path = "/spot/studies/{studyId}/withdrawal", hasBody = false)
    fun withDrawlMember(
        @Path("studyId") studyId: Int
    ): Call<HostWithDrawl>
}

interface FinishedStudyApiService {
    @GET("/spot/search/studies/finished-studies")
    fun GetFinshedStudy(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<FinishedStudyResponse>
}








