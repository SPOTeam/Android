package com.example.spoteam_android.login

import com.example.spoteam_android.NaverLoginRequest
import com.example.spoteam_android.NaverResponse
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RegionApiResponse
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.ThemePreferences
import com.example.spoteam_android.YourResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApiService {
    @POST("spot/members/theme")
    fun postThemes(@Body themePreferences: ThemePreferences): Call<Void>

    @POST("spot/members/region")
    fun postRegions(@Body regionsPreferences: RegionsPreferences): Call<Void>

    @POST("spot/members/study-reasons")
    fun postPurposes(@Body purposePreferences: StudyReasons): Call<Void>

    @GET("spot/members/theme")
    fun getThemes(): Call<ThemeApiResponse>

    @GET("spot/members/study-reasons")
    fun getReasons(): Call<ReasonApiResponse>

    @GET("spot/members/region")
    fun getRegion(): Call<RegionApiResponse>


    //카카오 로그인 api 서비스
    @GET("/spot/members/sign-in/kakao")
    fun getUserInfo(@Query("accessToken") accessToken: String): Call<YourResponse>

    //네이버 로그인 api 서비스
    @POST("/spot/members/sign-in/naver")
    suspend fun signInWithNaver(@Body request: NaverLoginRequest): NaverResponse


}
