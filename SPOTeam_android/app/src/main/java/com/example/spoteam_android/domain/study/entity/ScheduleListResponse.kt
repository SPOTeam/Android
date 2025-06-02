package com.example.spoteam_android.domain.study.entity

data class ScheduleListResponse(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val schedules: List<Schedule>
) {
    data class Schedule(
        val startedAt: String,
        val finishedAt: String,
        val title: String,
        val location: String
    )
}
