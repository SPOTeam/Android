package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.request.MakeScheduleRequestDto
import com.example.spoteam_android.data.study.dto.response.MakeScheduleResponseDto
import com.example.spoteam_android.domain.study.entity.MakeScheduleRequest
import com.example.spoteam_android.domain.study.entity.MakeScheduleResponse

fun MakeScheduleResponseDto.toDomain() = MakeScheduleResponse(
    title = title,
    location = location,
    startedAt = startedAt,
    finishedAt = finishedAt,
    isAllDay = isAllDay,
    period = period
)

fun MakeScheduleRequest.toDto(): MakeScheduleRequestDto {
    return MakeScheduleRequestDto(
        title = title,
        location = location,
        startedAt = startedAt,
        finishedAt = finishedAt,
        isAllDay = isAllDay,
        period = period
    )
}