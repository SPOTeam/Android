package com.example.spoteam_android.domain.login.repository

import com.example.spoteam_android.domain.login.entity.CheckSpotMemberResponse
import com.example.spoteam_android.domain.login.entity.MemberInfoResponse
import com.example.spoteam_android.domain.login.entity.NaverLoginRequest
import com.example.spoteam_android.domain.login.entity.NickNameCheckResponse
import com.example.spoteam_android.domain.login.entity.NickNameRequest
import com.example.spoteam_android.domain.login.entity.NickNameResponse
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.domain.login.entity.WithdrawResponse

interface LoginRepository {
    suspend fun updateThemes(themes: List<String>): Result<MemberInfoResponse>

    suspend fun updateRegions(regions: List<String>): Result<MemberInfoResponse>

    suspend fun updateStudyReasons(reasons: List<Int>): Result<MemberInfoResponse>

    suspend fun signInWithKakao(accessToken: String): Result<SocialLoginResponse>

    suspend fun signInWithNaver(request: NaverLoginRequest): Result<SocialLoginResponse>

    suspend fun updateNickName(request: NickNameRequest): Result<NickNameResponse>

    suspend fun checkNickName(nickname: String): Result<NickNameCheckResponse>

    suspend fun getNickname(): Result<String>

    suspend fun checkSpotMember(): Result<CheckSpotMemberResponse>

    suspend fun withdrawSpot(): Result<WithdrawResponse>
}