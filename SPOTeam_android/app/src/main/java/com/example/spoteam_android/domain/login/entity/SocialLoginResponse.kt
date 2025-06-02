package com.example.spoteam_android.domain.login.entity
data class SocialLoginResponse(
    val isSpotMember: Boolean,
    val tokens: TokenResponse,
    val email: String,
    val memberId: Int,
    val loginType: String,
    val profileImage: String = ""
)

