package com.example.spoteam_android.data.login.datasourceimpl.remote

import com.example.spoteam_android.data.login.datasource.remote.LoginDataSource
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
import com.example.spoteam_android.data.login.service.LoginService
import javax.inject.Inject

class LoginDataSourceImpl @Inject constructor(private val loginService: LoginService) :
    LoginDataSource {
    override suspend fun updateThemes(themes: ThemeRequestDto): BaseResponse<MemberInfoResponseDto> = loginService.updateThemes(themes)

    override suspend fun updateRegions(regions: RegionRequestDto): BaseResponse<MemberInfoResponseDto> = loginService.updateRegions(regions)

    override suspend fun updateStudyReasons(studyReasons: StudyReasonRequestDto): BaseResponse<MemberInfoResponseDto> = loginService.updateStudyReasons(studyReasons)

    override suspend fun signInWithKakao(accessToken: String): BaseResponse<SocialLoginResponseDto> = loginService.signInWithKakao(accessToken)

    override suspend fun signInWithNaver(request: NaverLoginRequestDto): BaseResponse<SocialLoginResponseDto> = loginService.signInWithNaver(request)

    override suspend fun updateNickName(request: NickNameRequestDto): BaseResponse<NickNameResponseDto> = loginService.updateNickName(request)

    override suspend fun checkNickName(nickname: String): BaseResponse<NickNameCheckResponseDto> = loginService.checkNickname(nickname)

    override suspend fun getNickname(): BaseResponse<String> = loginService.getNickname()

    override suspend fun checkSpotMember(): BaseResponse<CheckSpotMemberResponseDto> = loginService.checkSpotMember()

    override suspend fun withdrawSpot(): BaseResponse<WithdrawResponseDto> = loginService.withdrawSpot()
}