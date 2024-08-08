package com.example.spoteam_android.login

import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemePreferences
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApiService {
    @POST("spot/member/{memberId}/themes")
    fun postThemes(@Path("memberId") memberId: Int, @Body themePreferences: ThemePreferences): Call<Void>

    @POST("spot/member/{memberId}/regions")
    fun postRegions(@Path("memberId") memberId: Int, @Body regionsPreferences: RegionsPreferences): Call<Void>

    @POST("spot/member/{memberId}/study-reasons")
    fun postPurposes(@Path("memberId") memberId: Int, @Body purposePreferences: StudyReasons): Call<Void>
}
