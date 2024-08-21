package com.example.spoteam_android.ui.community

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommunityRetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"
    //종훈 토큰 private const val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6OSwidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzI0MTQ3NzMwLCJleHAiOjE3MjQyMzQxMzB9.0d6fWTixeBSq-vOts_2Ap8LdGtE1K47JQlSoNfmU-Ss"
     private const val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MTQsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTcyNDA0NzM3OSwiZXhwIjoxNzI0MTMzNzc5fQ.FM1iVo2n1jgAiewsrNF-z6CkTUHEr3MX50bS1uqJgvo"

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