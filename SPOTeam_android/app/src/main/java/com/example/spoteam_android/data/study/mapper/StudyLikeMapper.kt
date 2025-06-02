package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.StudyLikeResponseDto
import com.example.spoteam_android.domain.study.entity.StudyLikeResponse

fun StudyLikeResponseDto.toDomain() = StudyLikeResponse(
    studyTitle = studyTitle,
    createdAt = createdAt,
    status = status
)
