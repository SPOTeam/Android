package com.example.spoteam_android.data.login.datasource.remote

import com.example.spoteam_android.core.network.BaseResponse
import com.example.spoteam_android.data.login.dto.request.NaverLoginRequestDto
import com.example.spoteam_android.data.login.dto.request.NickNameRequestDto
import com.example.spoteam_android.data.login.dto.request.RegionRequestDto
import com.example.spoteam_android.data.login.dto.request.StudyReasonRequestDto
import com.example.spoteam_android.data.login.dto.request.ThemeRequestDto
import com.example.spoteam_android.data.login.dto.response.CheckSpotMemberResponseDto
import com.example.spoteam_android.data.login.dto.response.MemberInfoResponseDto
import com.example.spoteam_android.data.login.dto.response.NickNameCheckResponseDto
import com.example.spoteam_android.data.login.dto.response.NickNameResponseDto
import com.example.spoteam_android.data.login.dto.response.SocialLoginResponseDto
import com.example.spoteam_android.data.login.dto.response.WithdrawResponseDto

interface LoginDataSource {
    suspend fun updateThemes(themes: ThemeRequestDto): BaseResponse<MemberInfoResponseDto>

    suspend fun updateRegions(regions: RegionRequestDto): BaseResponse<MemberInfoResponseDto>

    suspend fun updateStudyReasons(studyReasons: StudyReasonRequestDto): BaseResponse<MemberInfoResponseDto>

    suspend fun signInWithKakao(accessToken: String): BaseResponse<SocialLoginResponseDto>

    suspend fun signInWithNaver(request: NaverLoginRequestDto): BaseResponse<SocialLoginResponseDto>

    suspend fun updateNickName(request: NickNameRequestDto): BaseResponse<NickNameResponseDto>

    suspend fun checkNickName(nickname: String): BaseResponse<NickNameCheckResponseDto>

    suspend fun getNickname() : BaseResponse<String>

    suspend fun checkSpotMember(): BaseResponse<CheckSpotMemberResponseDto>

    suspend fun withdrawSpot(): BaseResponse<WithdrawResponseDto>
}