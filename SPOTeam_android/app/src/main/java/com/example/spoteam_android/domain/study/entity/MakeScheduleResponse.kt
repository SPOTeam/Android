package com.example.spoteam_android.domain.study.entity

data class MakeScheduleResponse(
    val title: String,
    val location: String,
    val startedAt: String,
    val finishedAt: String,
    val isAllDay: Boolean,
    val period: String
)