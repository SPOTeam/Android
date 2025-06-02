package com.example.spoteam_android.data.login.service

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginService {
    //update themes
    @POST("spot/members/theme")
    suspend fun updateThemes(@Body request: ThemeRequestDto): BaseResponse<MemberInfoResponseDto>

    //update regions
    @POST("spot/members/region")
    suspend fun updateRegions(@Body request: RegionRequestDto): BaseResponse<MemberInfoResponseDto>

    //update reasons
    @POST("spot/members/study-reasons")
    suspend fun updateStudyReasons(@Body request: StudyReasonRequestDto): BaseResponse<MemberInfoResponseDto>

    //kakao login
    @GET("spot/members/sign-in/kakao")
    suspend fun signInWithKakao(@Query("accessToken") accessToken: String): BaseResponse<SocialLoginResponseDto>

    //naver login
    @POST("spot/members/sign-in/naver")
    suspend fun signInWithNaver(@Body request: NaverLoginRequestDto): BaseResponse<SocialLoginResponseDto>

    //update nickname
    @POST("spot/sign-up/update")
    suspend fun updateNickName(@Body request: NickNameRequestDto): BaseResponse<NickNameResponseDto>

    //check nickname
    @GET("spot/check/nickname")
    suspend fun checkNickname(@Query("nickname") nickname: String): BaseResponse<NickNameCheckResponseDto>

    //get nickname
    @GET("/spot/members/nickname")
    suspend fun getNickname(): BaseResponse<String>

    //check spot member
    @GET("spot/check")
    suspend fun checkSpotMember(): BaseResponse<CheckSpotMemberResponseDto>

    //withdraw spot
    @PATCH("spot/withdraw")
    suspend fun withdrawSpot(): BaseResponse<WithdrawResponseDto>

}