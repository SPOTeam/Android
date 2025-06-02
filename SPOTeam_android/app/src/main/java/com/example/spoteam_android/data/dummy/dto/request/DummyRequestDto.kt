package com.example.spoteam_android.data.dummy.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DummyRequestDto(
    @SerialName("id")
    val id: Long
)