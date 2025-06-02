package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.StudyDetailResponseDto
import com.example.spoteam_android.domain.study.entity.StudyDetailResponse

fun StudyDetailResponseDto.toDomain(): StudyDetailResponse {
    return StudyDetailResponse(
        studyId = studyId,
        studyName = studyName,
        ownerId = studyOwner.ownerId,
        ownerName = studyOwner.ownerName,
        hitNum = hitNum,
        heartCount = heartCount,
        memberCount = memberCount,
        maxPeople = maxPeople,
        gender = gender,
        minAge = minAge,
        maxAge = maxAge,
        fee = fee,
        isOnline = isOnline,
        profileImage = profileImage,
        themes = themes,
        regions = regions,
        goal = goal,
        introduction = introduction
    )
}