package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.StudyMemberResponseDto
import com.example.spoteam_android.domain.study.entity.StudyMemberResponse


fun StudyMemberResponseDto.toDomain(): StudyMemberResponse {
    return StudyMemberResponse(
        totalCount = this.totalElements,
        members = this.members.map {
            StudyMemberResponse.StudyMember(
                memberId = it.memberId,
                nickname = it.nickname,
                profileImage = it.profileImage
            )
        }
    )
}