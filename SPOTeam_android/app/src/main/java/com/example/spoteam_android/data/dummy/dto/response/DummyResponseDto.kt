package com.example.spoteam_android.data.dummy.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DummyResponseDto(
    @SerialName("info")
    val info: List<String>
)