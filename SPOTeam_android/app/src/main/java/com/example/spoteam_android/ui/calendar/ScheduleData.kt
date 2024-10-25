package com.example.spoteam_android.ui.calendar

import com.google.gson.annotations.SerializedName

data class ScheduleResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultData // 이 부분을 String이 아닌 ResultData로 수정
)

data class ResultData(
    @SerializedName("studyId") val studyId: Int,
    @SerializedName("scheduleList") val scheduleList: List<Schedule>
)

data class Schedule(
    @SerializedName("scheduleId") val scheduleId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("location") val location: String,
    @SerializedName("startedAt") val startedAt: String,
    @SerializedName("finishedAt") val finishedAt: String,
    @SerializedName("isAllDay") val isAllDay: Boolean,
    @SerializedName("period") val period: String
)
