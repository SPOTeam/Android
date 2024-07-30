package com.example.spoteam_android

data class Event(
    val id: Int,
    val title: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String
)