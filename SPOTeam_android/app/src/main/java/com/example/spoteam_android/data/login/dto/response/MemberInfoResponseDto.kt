package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberInfoResponseDto(
    @SerialName("memberId")
    val memberId: Int,
    @SerialName("updatedAt")
    val updatedAt: String
)