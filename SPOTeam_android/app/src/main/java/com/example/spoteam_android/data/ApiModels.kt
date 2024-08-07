package com.example.spoteam_android.data

class ApiModels {

    data class Regions(
        val regions: List<String>
    )

    data class Themes(
        val themes: List<String>
    )

    data class RequestData(
        val regions: Regions,
        val themes: Themes
    )

    // 응답 데이터 클래스 정의
    data class Tokens(
        val accessToken: String,
        val refreshToken: String,
        val accessTokenExpiresIn: Long
    )

    data class Result(
        val memberId: Int,
        val email: String,
        val tokens: Tokens
    )

    data class ResponseData(
        val isSuccess: Boolean,
        val code: String,
        val message: String,
        val result: Result
    )

}