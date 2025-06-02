package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NickNameCheckResponseDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("duplicate")
    val duplicate: Boolean
)