package com.spot.android

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("spot/reissue") //토큰 재발급
    fun refreshToken(@Header("refreshToken") refreshToken: String): Call<NewTokensResponse>
}
data class NewTokensResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: TokenResult
)

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)
