package com.example.spoteam_android.domain.login.entity

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Int
)