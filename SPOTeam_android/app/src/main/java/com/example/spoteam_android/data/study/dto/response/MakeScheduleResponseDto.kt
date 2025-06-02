package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MakeScheduleResponseDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("statedAt")
    val startedAt: String,
    @SerializedName("finishedAt")
    val finishedAt: String,
    @SerializedName("isAllDay")
    val isAllDay: Boolean,
    @SerializedName("period")
    val period: String
)