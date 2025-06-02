package com.example.spoteam_android.domain.login.entity

data class NaverLoginRequest(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val error: String? = null,
    val errorDescription: String? = null
)