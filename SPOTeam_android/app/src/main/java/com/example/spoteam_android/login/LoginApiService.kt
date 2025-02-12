package com.example.spoteam_android.login

import com.example.spoteam_android.EmailResponse
import com.example.spoteam_android.EmailVerifyResponse
import com.example.spoteam_android.IdResponse
import com.example.spoteam_android.NaverLoginRequest
import com.example.spoteam_android.NaverResponse
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RegionApiResponse
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.ThemePreferences
import com.example.spoteam_android.ValidateEmailResponse
import com.example.spoteam_android.YourResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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
    fun getUserInfo(@Query("accessToken") accessToken: String): Call<YourResponse>

    //네이버 로그인 api 서비스
    @POST("/spot/members/sign-in/naver")
    suspend fun signInWithNaver(@Body request: NaverLoginRequest): NaverResponse

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

    //닉네임 생성 및 약관 동의
//    @GET("/spot/sign-up/update")
//    fun getNicName(
//        @Query("nickname") nickname: String,
//        @Query("personalInfo") personalInfo: Boolean,
//        @Query("idInfo") idInfo: Boolean
//    ): Call<NickNameResponse>
//    }



}
