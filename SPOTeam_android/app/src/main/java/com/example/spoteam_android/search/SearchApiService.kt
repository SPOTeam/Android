package com.example.spoteam_android.search

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchApiService {
    @GET("/spot/search/studies")
    fun searchStudies(
        @Header("Authorization") authToken: String,
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String
    ): Call<ApiResponse>
}
