package com.example.spoteam_android.data.login.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyReasonRequestDto(
    @SerialName("reasons")
    val reasons: List<Int>
)
