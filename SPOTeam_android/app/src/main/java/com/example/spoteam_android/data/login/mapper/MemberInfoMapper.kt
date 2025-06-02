package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.response.MemberInfoResponseDto
import com.example.spoteam_android.domain.login.entity.MemberInfoResponse

fun MemberInfoResponseDto.toMemberInfoModel(): MemberInfoResponse = MemberInfoResponse(
    memberId = memberId,
    updatedAt = updatedAt
)