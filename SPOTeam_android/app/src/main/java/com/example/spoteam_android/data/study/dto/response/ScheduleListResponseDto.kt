package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleListResponseDto(
    @SerializedName("totalPages")
    val totalPages: Int,
    @SerializedName("totalElements")
    val totalElements: Int,
    @SerializedName("first")
    val first: Boolean,
    @SerializedName("last")
    val last: Boolean,
    @SerializedName("size")
    val size: Int,
    @SerializedName("schedules")
    val schedules: List<ScheduleDto>
) {

    @Serializable
    data class ScheduleDto(
        @SerializedName("startedAt")
        val startedAt: String,
        @SerializedName("finishedAt")
        val finishedAt: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("location")
        val location: String
    )
}