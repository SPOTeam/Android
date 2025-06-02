package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.request.NickNameRequestDto
import com.example.spoteam_android.data.login.dto.response.NickNameResponseDto
import com.example.spoteam_android.domain.login.entity.NickNameRequest
import com.example.spoteam_android.domain.login.entity.NickNameResponse

fun NickNameResponseDto.toNickNameModel(): NickNameResponse = NickNameResponse(
    name = name,
    nickname = nickname,
    email = email,
    personalInfo = personalInfo
)

fun NickNameRequest.toNickNameInfoModel(): NickNameRequestDto = NickNameRequestDto(
    nickname = nickname,
    personalInfo = personalInfo,
    idInfo = idInfo
)