package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SocialLoginResponseDto(
    @SerialName("isSpotMember")
    val isSpotMember: Boolean,
    @SerialName("signInDTO")
    val signInDTO: SignInResponseDto
)