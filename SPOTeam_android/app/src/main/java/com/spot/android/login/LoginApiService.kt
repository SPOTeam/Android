package com.spot.android.login

import com.spot.android.EmailResponse
import com.spot.android.EmailVerifyResponse
import com.spot.android.IdResponse
import com.spot.android.NaverLoginRequest
import com.spot.android.NaverResponse
import com.spot.android.NickNameRequest
import com.spot.android.NickNameResponse
import com.spot.android.NicknameCheckResponse
import com.spot.android.ReasonApiResponse
import com.spot.android.RegionApiResponse
import com.spot.android.RegionsPreferences
import com.spot.android.SpotMemberCheckResponse
import com.spot.android.StudyReasons
import com.spot.android.ThemeApiResponse
import com.spot.android.ThemePreferences
import com.spot.android.ValidateEmailResponse
import com.spot.android.WithdrawResponse
import com.spot.android.YourResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApiService {
    @POST("spot/members/theme")
    fun postThemes(@Body themePreferences: ThemePreferences): Call<Void>

    @POST("spot/members/region")
    fun postRegions(@Body regionsPreferences: RegionsPreferences): Call<Void>

    @POST("spot/members/study-reasons")
    fun postPurposes(@Body purposePreferences: StudyReasons): Call<Void>

    @GET("spot/members/theme")
    fun getThemes(): Call<ThemeApiResponse>

    @GET("spot/members/study-reasons")
    fun getReasons(): Call<ReasonApiResponse>

    @GET("spot/members/region")
    fun getRegion(): Call<RegionApiResponse>


    //카카오 로그인 api 서비스
    @GET("/spot/members/sign-in/kakao")
    suspend fun getUserInfo(@Query("accessToken") accessToken: String): Response<YourResponse>

    //네이버 로그인 api 서비스
    @POST("/spot/members/sign-in/naver")
    suspend fun signInWithNaver(@Body request: NaverLoginRequest): Response<NaverResponse>

    //일반 로그인 아이디 사용 가능 여부 확인
    @GET("/spot/check/login-id")
    fun checkID(@Query("loginId") loginId: String): Call<IdResponse>

    //일반 로그인 이메일 사용 가능 여부 확인
    @GET("/spot/check/email")
    fun checkEmail(@Query("email") email: String): Call<EmailResponse>
    //이메일 인증 코드 발급 api
    @POST("/spot/send-verification-code")
    fun getVerifyCode(@Query("email") email: String) : Call<EmailVerifyResponse>

    //이메일 인증 코드를 입력해 이메일의 유효성 확인
    @POST("/spot/verify")
    fun validateEmailCode(
        @Query("verificationCode") verificationCode: String, @Query("email") email: String)
    :Call<ValidateEmailResponse>

    @GET("/spot/check")
    fun checkIsSpotMember(): Call<SpotMemberCheckResponse>

    @GET("/spot/check/nickname")
    fun checkNickname(
        @Query("nickname") nickname: String
    ): Call<NicknameCheckResponse>


    @POST("/spot/sign-up/update")
    fun updateNickName(
        @Body request: NickNameRequest
    ): Call<NickNameResponse>

    //회원 탈퇴
    @PATCH("spot/withdraw")
    fun withdraw(): Call<WithdrawResponse>



}
