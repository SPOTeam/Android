package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.StudyApplyResponseDto
import com.example.spoteam_android.domain.study.entity.StudyApplyResponse

fun StudyApplyResponseDto.toDomain() = StudyApplyResponse(
    memberId = memberId,
    study = StudyApplyResponse.StudyApplyInfo(
        studyId = study.studyId,
        title = study.title
    )
)