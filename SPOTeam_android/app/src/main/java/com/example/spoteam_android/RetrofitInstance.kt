package com.example.spoteam_android

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://www.teamspot.site/"
    private var authToken: String? = null
    private lateinit var appContext: Context

    /**
     * RetrofitInstance 초기화. Application의 onCreate에서 호출 필요.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadAuthTokenFromPreferences()
    }

    /**
     * Auth Token 설정. SharedPreferences에도 저장.
     */
    fun setAuthToken(token: String) {
        authToken = token
        saveAuthTokenToPreferences(token)
        Log.d("RetrofitInstance", "Updated authToken: $authToken")
    }

    /**
     * SharedPreferences에서 Auth Token 로드.
     */
    private fun loadAuthTokenFromPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        authToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }

    /**
     * SharedPreferences에 Auth Token 저장.
     */
    private fun saveAuthTokenToPreferences(token: String) {
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_accessToken", token)
                apply()
            }
        }
    }

    /**
     * OkHttpClient 생성. AuthInterceptor 등록.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(appContext)) // AuthInterceptor 등록
            .build()
    }

    /**
     * Retrofit 객체 생성.
     */
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
