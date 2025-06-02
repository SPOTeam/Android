package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.response.CheckSpotMemberResponseDto
import com.example.spoteam_android.domain.login.entity.CheckSpotMemberResponse


fun CheckSpotMemberResponseDto.toCheckSpotMemberModel(): CheckSpotMemberResponse = CheckSpotMemberResponse(
    isSpotMember = isSpotMember
)