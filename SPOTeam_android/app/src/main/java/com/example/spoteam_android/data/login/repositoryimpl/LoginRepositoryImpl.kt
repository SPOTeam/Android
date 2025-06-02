package com.example.spoteam_android.data.login.repositoryimpl

import com.example.spoteam_android.data.login.datasource.remote.LoginDataSource
import com.example.spoteam_android.data.login.dto.request.RegionRequestDto
import com.example.spoteam_android.data.login.dto.request.StudyReasonRequestDto
import com.example.spoteam_android.data.login.dto.request.ThemeRequestDto
import com.example.spoteam_android.data.login.mapper.toCheckSpotMemberModel
import com.example.spoteam_android.data.login.mapper.toMemberInfoModel
import com.example.spoteam_android.data.login.mapper.toNaverLoginRequest
import com.example.spoteam_android.data.login.mapper.toNickNameCheckModel
import com.example.spoteam_android.data.login.mapper.toNickNameInfoModel
import com.example.spoteam_android.data.login.mapper.toNickNameModel
import com.example.spoteam_android.data.login.mapper.toSocialLoginModel
import com.example.spoteam_android.data.login.mapper.toWithdrawModel
import com.example.spoteam_android.domain.login.entity.CheckSpotMemberResponse
import com.example.spoteam_android.domain.login.entity.MemberInfoResponse
import com.example.spoteam_android.domain.login.entity.NaverLoginRequest
import com.example.spoteam_android.domain.login.entity.NickNameCheckResponse
import com.example.spoteam_android.domain.login.entity.NickNameRequest
import com.example.spoteam_android.domain.login.entity.NickNameResponse
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.domain.login.entity.WithdrawResponse
import com.example.spoteam_android.domain.login.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginDataSource: LoginDataSource
) : LoginRepository {
    override suspend fun updateThemes(themes: List<String>): Result<MemberInfoResponse> = runCatching {
        val response = loginDataSource.updateThemes(ThemeRequestDto(themes))
        response.result?.toMemberInfoModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun updateRegions(regions: List<String>): Result<MemberInfoResponse> = runCatching {
        val response = loginDataSource.updateRegions(RegionRequestDto(regions))
        response.result?.toMemberInfoModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun updateStudyReasons(reasons: List<Int>): Result<MemberInfoResponse> = runCatching {
        val response = loginDataSource.updateStudyReasons(StudyReasonRequestDto(reasons))
        response.result?.toMemberInfoModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun signInWithKakao(accessToken: String): Result<SocialLoginResponse> = runCatching {
        val response = loginDataSource.signInWithKakao(accessToken)
        response.result?.toSocialLoginModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun signInWithNaver(request: NaverLoginRequest): Result<SocialLoginResponse> = runCatching {
        val response = loginDataSource.signInWithNaver(request.toNaverLoginRequest())
        response.result?.toSocialLoginModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun updateNickName(request: NickNameRequest): Result<NickNameResponse> = runCatching {
        val response = loginDataSource.updateNickName(request.toNickNameInfoModel())
        response.result?.toNickNameModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun checkNickName(nickname: String): Result<NickNameCheckResponse> = runCatching {
        val response = loginDataSource.checkNickName(nickname)
        response.result?.toNickNameCheckModel() ?: throw IllegalStateException("No result")
    }

    override suspend fun getNickname(): Result<String> = runCatching {
        val response = loginDataSource.getNickname()
        response.result ?: throw IllegalStateException("No result")
    }

    override suspend fun checkSpotMember(): Result<CheckSpotMemberResponse> = kotlin.runCatching {
        val response = loginDataSource.checkSpotMember()
        response.result?.toCheckSpotMemberModel() ?: throw  IllegalStateException("No result")
    }

    override suspend fun withdrawSpot(): Result<WithdrawResponse> = runCatching {
        val response = loginDataSource.withdrawSpot()
        response.result?.toWithdrawModel() ?: throw IllegalStateException("No result")
    }
}
