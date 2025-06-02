package com.example.spoteam_android.data.login.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NickNameRequestDto(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("personalInfo")
    val personalInfo: Boolean,
    @SerialName("idInfo")
    val idInfo: Boolean
)