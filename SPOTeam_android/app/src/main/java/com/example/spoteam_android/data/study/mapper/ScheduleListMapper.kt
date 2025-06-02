package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.ScheduleListResponseDto
import com.example.spoteam_android.domain.study.entity.ScheduleListResponse

fun ScheduleListResponseDto.toDomain() = ScheduleListResponse(
    totalPages = totalPages,
    totalElements = totalElements,
    first = first,
    last = last,
    size = size,
    schedules = schedules.map { it.toDomain() }
)

fun ScheduleListResponseDto.ScheduleDto.toDomain() = ScheduleListResponse.Schedule(
    startedAt = startedAt,
    finishedAt = finishedAt,
    title = title,
    location = location
)