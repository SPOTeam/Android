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

    fun initialize(context: Context) {
        appContext = context.applicationContext
        loadAuthTokenFromPreferences()
    }

    fun setAuthToken(token: String) {
        authToken = token
        saveAuthTokenToPreferences(token)
        Log.d("RetrofitInstance", "Updated authToken: $authToken")
    }


    private fun loadAuthTokenFromPreferences() {
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        authToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
//        Log.d("RetrofitInstance", "Loaded auth token: $authToken")
    }

    private fun saveAuthTokenToPreferences(token: String) {
        val sharedPreferences = appContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_accessToken", token)
                apply()
            }
        }
//        Log.d("RetrofitInstance", "Saved auth token to preferences: $token")
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(appContext))
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
