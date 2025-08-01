package com.umcspot.android

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private var authToken: String? = null
    private var isInitialized = false
    private lateinit var appContext: Context


    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadAuthTokenFromPreferences()
        isInitialized = true
    }


    fun setAuthToken(token: String?) {
        authToken = token
        if (token != null) {
            saveAuthTokenToPreferences(token)
        } else {
            clearAuthTokenFromPreferences()
        }
        Log.d("RetrofitInstance", "Updated authToken: $authToken")
    }


    fun getAuthToken(): String? {
        return authToken
    }


    private fun loadAuthTokenFromPreferences() {
        if (!::appContext.isInitialized) {
            Log.e("RetrofitInstance", "initialize() 호출 필요!")
            return
        }
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        authToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }


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


    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(appContext))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    var gson = GsonBuilder().setLenient().create()
    val retrofit: Retrofit by lazy {


        if (!isInitialized) {
            throw IllegalStateException("RetrofitInstance.initialize()를 먼저 호출하세요!")
        }
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
