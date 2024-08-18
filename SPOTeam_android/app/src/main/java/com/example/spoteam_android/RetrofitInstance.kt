package com.example.spoteam_android

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://www.teamspot.site/"

    private val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OSwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzOTAzNTk4LCJleHAiOjE3MjM5ODk5OTh9.aLTrsz358wheVw3vbxg4bAZVd0ZHqlgeUrFtUPMXfSg"
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }
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