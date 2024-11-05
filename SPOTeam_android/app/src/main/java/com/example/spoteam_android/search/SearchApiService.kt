package com.example.spoteam_android.search

import com.example.spoteam_android.ui.interestarea.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchApiService {
    @GET("/spot/search/studies")
    fun PostSearchApi(
        @Query("keyword") keyword: String,  // Path parameter for memberId
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortBy") sortBy: String,
    ): Call<ApiResponse>
}


