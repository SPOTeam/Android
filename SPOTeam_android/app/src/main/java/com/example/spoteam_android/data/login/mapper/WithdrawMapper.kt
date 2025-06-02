package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.response.WithdrawResponseDto
import com.example.spoteam_android.domain.login.entity.WithdrawResponse

fun WithdrawResponseDto.toWithdrawModel(): WithdrawResponse = WithdrawResponse(
    memberId = memberId,
    name = name,
    email = email,
    inactive = inactive
)