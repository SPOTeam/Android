package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.response.NickNameCheckResponseDto
import com.example.spoteam_android.domain.login.entity.NickNameCheckResponse

fun NickNameCheckResponseDto.toNickNameCheckModel(): NickNameCheckResponse = NickNameCheckResponse(
    nickname = nickname,
    duplicate = duplicate
)