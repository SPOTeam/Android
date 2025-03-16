package com.example.spoteam_android

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://www.teamspot.site/"
    private var authToken: String? = null
    private var isInitialized = false
    private lateinit var appContext: Context

    /**
     * ✅ RetrofitInstance 초기화. Application의 onCreate에서 호출 필요.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadAuthTokenFromPreferences()
        isInitialized = true
    }

    /**
     * ✅ Auth Token 설정 (Setter).
     */
    fun setAuthToken(token: String?) {
        authToken = token
        if (token != null) {
            saveAuthTokenToPreferences(token)
        } else {
            clearAuthTokenFromPreferences()
        }
        Log.d("RetrofitInstance", "Updated authToken: $authToken")
    }

    /**
     * ✅ Auth Token 조회 (Getter).
     */
    fun getAuthToken(): String? {
        return authToken
    }

    /**
     * ✅ SharedPreferences에서 Auth Token 로드.
     */
    private fun loadAuthTokenFromPreferences() {
        if (!::appContext.isInitialized) {
            Log.e("RetrofitInstance", "initialize() 호출 필요!")
            return
        }
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        authToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }

    /**
     * ✅ SharedPreferences에 Auth Token 저장.
     */
    private fun saveAuthTokenToPreferences(token: String) {
        if (!::appContext.isInitialized) {
            Log.e("RetrofitInstance", "initialize() 호출 필요!")
            return
        }
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
     * ✅ SharedPreferences에서 Auth Token 삭제.
     */
    private fun clearAuthTokenFromPreferences() {
        if (!::appContext.isInitialized) {
            Log.e("RetrofitInstance", "initialize() 호출 필요!")
            return
        }
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            with(sharedPreferences.edit()) {
                remove("${it}_accessToken")
                apply()
            }
        }
    }

    /**
     * ✅ OkHttpClient 생성.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(appContext))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * ✅ Retrofit 객체 생성.
     */
    val retrofit: Retrofit by lazy {
        if (!isInitialized) {
            throw IllegalStateException("RetrofitInstance.initialize()를 먼저 호출하세요!")
        }
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
