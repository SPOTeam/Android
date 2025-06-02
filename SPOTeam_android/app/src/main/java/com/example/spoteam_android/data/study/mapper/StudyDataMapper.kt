package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.StudyDataResponseDto
import com.example.spoteam_android.domain.study.entity.StudyDataResponse

fun StudyDataResponseDto.toDomain(): StudyDataResponse {
    return StudyDataResponse(
        totalPages = totalPages,
        totalElements = totalElements,
        first = first,
        last = last,
        size = size,
        number = pageNumber,
        content = content.map { it.toDomain() }
    )
}

fun StudyDataResponseDto.StudyContentResponseDto.toDomain(): StudyDataResponse.StudyContent {
    return StudyDataResponse.StudyContent(
        studyId = studyId,
        imageUrl = imageUrl,
        title = title,
        introduction = introduction,
        goal = goal,
        memberCount = memberCount,
        heartCount = heartCount,
        hitNum = hitNum,
        maxPeople = maxPeople,
        studyState = studyState,
        themeTypes = themeTypes,
        regions = regions,
        createdAt = createdAt,
        liked = liked
    )
}