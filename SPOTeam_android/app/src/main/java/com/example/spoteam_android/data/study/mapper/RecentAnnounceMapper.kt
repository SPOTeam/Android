package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.RecentAnnounceResponseDto
import com.example.spoteam_android.domain.study.entity.RecentAnnounceResponse

fun RecentAnnounceResponseDto.toDomain() = RecentAnnounceResponse(
    title = title,
    content = content
)
