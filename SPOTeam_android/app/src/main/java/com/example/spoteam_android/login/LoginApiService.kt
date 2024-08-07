package com.example.spoteam_android.login

import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.ThemePreferences
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("user/theme")
    fun postThemePreferences(@Body themePreferences: ThemePreferences): Call<Void>

    @POST("user/regions")
    fun postRegionsPreferences(@Body regionsPreferences: RegionsPreferences): Call<Void>
}
