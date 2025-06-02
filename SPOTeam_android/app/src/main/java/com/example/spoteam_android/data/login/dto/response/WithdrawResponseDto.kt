package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WithdrawResponseDto(
    @SerialName("memberId")
    val memberId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("inactive")
    val inactive: String
)