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
    private var isInitialized = false // ✅ 초기화 여부 확인
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
     * ✅ Auth Token 설정. SharedPreferences에도 저장.
     */
    fun setAuthToken(token: String) {
        authToken = token
        saveAuthTokenToPreferences(token)
        Log.d("RetrofitInstance", "Updated authToken: $authToken")
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
     * ✅ OkHttpClient 생성. AuthInterceptor 등록 및 타임아웃 설정.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(appContext)) // 🔒 AuthInterceptor 추가
            .connectTimeout(60, TimeUnit.SECONDS) // ⏳ 연결 타임아웃 (기본 10초 → 60초)
            .readTimeout(60, TimeUnit.SECONDS) // ⏳ 데이터 읽기 타임아웃 (기본 10초 → 60초)
            .writeTimeout(60, TimeUnit.SECONDS) // ⏳ 데이터 쓰기 타임아웃 (기본 10초 → 60초)
            .retryOnConnectionFailure(true) // 🔄 자동 재시도 활성화
            .build()
    }

    /**
     * ✅ Retrofit 객체 생성 (initialize() 이후에만 사용 가능).
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
