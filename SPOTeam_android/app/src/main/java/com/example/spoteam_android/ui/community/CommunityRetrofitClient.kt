package com.example.spoteam_android.ui.community

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CommunityRetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"

    private const val token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MzQsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTcyOTgyMDE1MiwiZXhwIjoxNzI5ODIzNzUyfQ.hkD0INKAdJOaiJyKVEBxYsveg-3hXGsKWU467c78el4"

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