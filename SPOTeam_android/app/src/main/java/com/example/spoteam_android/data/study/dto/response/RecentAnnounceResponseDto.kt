package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecentAnnounceResponseDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
)
