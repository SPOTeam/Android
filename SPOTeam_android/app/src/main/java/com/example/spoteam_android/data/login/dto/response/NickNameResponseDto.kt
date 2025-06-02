package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NickNameResponseDto(
    @SerialName("name")
    val name: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("email")
    val email: String,
    @SerialName("idInfo")
    val idInfo: Boolean,
    @SerialName("personalInfo")
    val personalInfo: Boolean

)
