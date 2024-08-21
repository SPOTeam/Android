package com.example.spoteam_android.ui.community

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommunityRetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"
    //종훈 토큰 private const val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OSwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzI0MTQ3NzMwLCJleHAiOjE3MjQyMzQxMzB9.0d6fWTixeBSq-vOts_2Ap8LdGtE1K47JQlSoNfmU-Ss"
     private const val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OSwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzI0MjI1MTE3LCJleHAiOjE3MjQzMTE1MTd9.HkcW7WyV9vLhb5T4jTSHUfOVu60SoXnHJZTecLr9H8E"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", token)
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    val instance: CommunityAPIService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(CommunityAPIService::class.java)
    }
}