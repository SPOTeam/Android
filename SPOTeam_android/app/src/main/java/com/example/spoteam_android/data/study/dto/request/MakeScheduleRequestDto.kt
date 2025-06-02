package com.example.spoteam_android.data.study.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class MakeScheduleRequestDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("startedAt")
    val startedAt: String,
    @SerializedName("finishedAt")
    val finishedAt: String,
    @SerializedName("isAllDay")
    val isAllDay: Boolean,
    @SerializedName("period")
    val period: String
)