package com.example.spoteam_android.data

import kotlinx.serialization.Serializable

class ApiModels {
    @Serializable
    data class Regions(
        val regions: List<String>
    )
    @Serializable
    data class Themes(
        val themes: List<String>
    )
    @Serializable
    data class RequestData(
        val regions: Regions,
        val themes: Themes
    )
    @Serializable
    // 응답 데이터 클래스 정의
    data class Tokens(
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiresIn: Long
    )
    @Serializable
    data class Result(
        val memberId: Int,
        val email: String,
        val tokens: Tokens
    )
    @Serializable
    data class ResponseData(
        val isSuccess: Boolean,
        val code: String,
        val message: String,
        val result: Result
    )

}