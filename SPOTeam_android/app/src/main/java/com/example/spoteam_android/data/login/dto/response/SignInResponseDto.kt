package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInResponseDto(
    @SerialName("tokens")
    val tokens: TokenResponseDto,
    @SerialName("email")
    val email: String,
    @SerialName("memberId")
    val memberId: Int,
    @SerialName("loginType")
    val loginType: String
)