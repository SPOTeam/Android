package com.example.spoteam_android.login

import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RegionApiResponse
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.ThemePreferences
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApiService {
    @POST("spot/member/{memberId}/theme")
    fun postThemes(@Path("memberId") memberId: Int, @Body themePreferences: ThemePreferences): Call<Void>

    @POST("spot/member/{memberId}/region")
    fun postRegions(@Path("memberId") memberId: Int, @Body regionsPreferences: RegionsPreferences): Call<Void>

    @POST("spot/member/{memberId}/study-reasons")
    fun postPurposes(@Path("memberId") memberId: Int, @Body purposePreferences: StudyReasons): Call<Void>

    @GET("spot/member/{memberId}/theme")
    fun getThemes(@Path("memberId") memberId: Int): Call<ThemeApiResponse>

    @GET("spot/member/{memberId}/study-reasons")
    fun getReasons(@Path("memberId") memberId: Int): Call<ReasonApiResponse>

    @GET("/spot/member/{memberId}/region")
    fun getRegion(@Path("memberId") memberId: Int): Call<RegionApiResponse>
}
