package com.example.spoteam_android.data.login.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NaverLoginRequestDto(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("error")
    val error: String? = null,
    @SerialName("error_description")
    val errorDescription: String? = null
)
