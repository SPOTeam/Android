package com.example.spoteam_android.login

import android.util.Log
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.NaverLoginRequest
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.RetrofitInstance
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.create

class LoginRepository(private val tokenManager: TokenManager) {
    private val api: LoginApiService =
        SocialLoginRetrofitInstance.retrofit.create(LoginApiService::class.java)

    suspend fun sendKakaoTokenToServer(
        accessToken: String
    ): Result<KaKaoResult> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getUserInfo(accessToken)

            if (response.isSuccessful) {
                val userInfo = response.body()?.result
                if (userInfo != null) {
                    // ✅ isSpotMember, token을 즉시 저장
                    tokenManager.saveIsSpotMember(userInfo.isSpotMember)
                    tokenManager.saveUserInfo(
                        platform = "kakao",
                        email = userInfo.signInDTO.email,
                        nickname = "",
                        profileImageUrl = "",
                        accessToken = userInfo.signInDTO.tokens.accessToken,
                        refreshToken = userInfo.signInDTO.tokens.refreshToken,
                        memberId = userInfo.signInDTO.memberId
                    )
                    RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)

                    // ✅ 비동기적으로 닉네임과 프로필 업데이트
                    UserApiClient.instance.me { user, error ->
                        if (error == null && user != null) {
                            val nickname = user.kakaoAccount?.profile?.nickname ?: ""
                            val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: ""
                            tokenManager.saveUserInfo(
                                platform = "kakao",
                                email = userInfo.signInDTO.email,
                                nickname = nickname,
                                profileImageUrl = profileImageUrl,
                                accessToken = userInfo.signInDTO.tokens.accessToken,
                                refreshToken = userInfo.signInDTO.tokens.refreshToken,
                                memberId = userInfo.signInDTO.memberId
                            )
                        }
                    }

                    Result.success(userInfo)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginRepository", "카카오 API 응답 실패: $errorBody")
                    Result.failure(Exception("API 응답 실패: $errorBody"))
                }
            } else {
                Log.e(
                    "LoginRepository",
                    "카카오 로그인 API 실패: ${response.errorBody()?.string()}"
                )
                Result.failure(Exception("API 실패"))
            }
        } catch (e: Exception) {
            Log.e("LoginRepository", "예외 발생: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun sendNaverTokenToServer(
        accessToken: String,
        refreshToken: String,
        tokenType: String,
        expiresIn: Long
    ): Result<NaverResult> = withContext(Dispatchers.IO) {
        try {
            val request = NaverLoginRequest(
                access_token = accessToken,
                refresh_token = refreshToken,
                token_type = tokenType,
                expires_in = expiresIn
            )
            val response = api.signInWithNaver(request)

            if (response.isSuccessful) {
                val userInfo = response.body()?.result
                if (userInfo != null) {
                    // 토큰/회원상태 저장
                    tokenManager.saveIsSpotMember(userInfo.isSpotMember)
                    tokenManager.saveUserInfo(
                        platform = "naver",
                        email = userInfo.signInDTO.email,
                        nickname = "",
                        profileImageUrl = "",
                        accessToken = userInfo.signInDTO.tokens.accessToken,
                        refreshToken = userInfo.signInDTO.tokens.refreshToken,
                        memberId = userInfo.signInDTO.memberId
                    )
                    RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)

                    // 비동기 닉네임/프로필 업데이트
                    NidOAuthLogin().callProfileApi(
                        object : NidProfileCallback<NidProfileResponse> {
                            override fun onSuccess(response: NidProfileResponse) {
                                response.profile?.let { profile ->
                                    tokenManager.saveUserInfo(
                                        platform = "naver",
                                        email = userInfo.signInDTO.email,
                                        nickname = profile.nickname ?: "",
                                        profileImageUrl = profile.profileImage ?: "",
                                        accessToken = userInfo.signInDTO.tokens.accessToken,
                                        refreshToken = userInfo.signInDTO.tokens.refreshToken,
                                        memberId = userInfo.signInDTO.memberId
                                    )
                                }
                            }

                            override fun onError(errorCode: Int, message: String) {
                                Log.e("Naver", "프로필 API 오류: $message")
                            }

                            override fun onFailure(httpStatus: Int, message: String) {
                                Log.e("Naver", "프로필 요청 실패: $message")
                            }
                        }
                    )

                    return@withContext Result.success(userInfo)
                } else {
                    return@withContext Result.failure(Exception("네이버 로그인 응답 실패"))
                }
            } else {
                return@withContext Result.failure(
                    Exception("네이버 로그인 API 오류: ${response.errorBody()?.string()}")
                )
            }
        } catch (e: Exception) {
            Log.e("NaverAPI", "API 호출 중 예외 발생", e)
            return@withContext Result.failure(e)
        }
    }
}