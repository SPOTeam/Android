package com.example.spoteam_android.data.login.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckSpotMemberResponseDto(
    @SerialName("isSpotMember")
    val isSpotMember: Boolean

)