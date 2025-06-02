package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.IsAppliedResponseDto
import com.example.spoteam_android.domain.study.entity.IsAppliedResponse

fun IsAppliedResponseDto.toDomain() = IsAppliedResponse(
    studyId = studyId,
    applied = applied
)