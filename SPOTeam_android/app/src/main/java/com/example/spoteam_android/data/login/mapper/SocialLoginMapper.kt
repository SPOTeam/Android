package com.example.spoteam_android.data.login.mapper

import com.example.spoteam_android.data.login.dto.response.SocialLoginResponseDto
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.data.login.dto.request.NaverLoginRequestDto
import com.example.spoteam_android.domain.login.entity.NaverLoginRequest
import com.example.spoteam_android.domain.login.entity.TokenResponse

fun SocialLoginResponseDto.toSocialLoginModel(): SocialLoginResponse {
    val dto = signInDTO
    val tokens = dto.tokens

    return SocialLoginResponse(
        isSpotMember = isSpotMember,
        tokens = TokenResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            accessTokenExpiresIn = tokens.accessTokenExpiresIn
        ),
        email = dto.email,
        memberId = dto.memberId,
        loginType = dto.loginType
    )
}



fun NaverLoginRequest.toNaverLoginRequest(): NaverLoginRequestDto = NaverLoginRequestDto(
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
    error = error,
    errorDescription = errorDescription
)

