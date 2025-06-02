package com.example.spoteam_android.data.login.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThemeRequestDto(
    @SerialName("themes")
    val themes: List<String>
)