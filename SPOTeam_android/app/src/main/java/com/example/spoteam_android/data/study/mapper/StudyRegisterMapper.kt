package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.request.StudyRegisterRequestDto
import com.example.spoteam_android.data.study.dto.response.StudyRegisterResponseDto
import com.example.spoteam_android.domain.study.entity.StudyRegisterRequest
import com.example.spoteam_android.domain.study.entity.StudyRegisterResponse

fun StudyRegisterResponseDto.toDomain(): StudyRegisterResponse {
    return StudyRegisterResponse(
        studyId = studyId,
        title = title,

    )
}
fun StudyRegisterRequest.toDto(): StudyRegisterRequestDto {
    return StudyRegisterRequestDto(
        themes = themes,
        title = title,
        goal = goal,
        introduction = introduction,
        isOnline = isOnline,
        profileImage = profileImage,
        regions = regions,
        maxPeople = maxPeople,
        gender = gender,
        minAge = minAge,
        maxAge = maxAge,
        fee = fee,
        hasFee = hasFee
    )
}
