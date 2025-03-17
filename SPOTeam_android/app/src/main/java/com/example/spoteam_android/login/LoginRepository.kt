package com.example.spoteam_android.login

import android.util.Log
import com.example.spoteam_android.YourResponse
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.NaverLoginRequest
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.RetrofitInstance
import com.kakao.sdk.friend.d.e
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.userAgent
import retrofit2.create

class LoginRepository(private val tokenManager: TokenManager) {
    private val api: LoginApiService =
        SocialLoginRetrofitInstance.retrofit.create(LoginApiService::class.java)

    suspend fun sendKakaoTokenToServer(accessToken: String): Result<KaKaoResult> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = api.getUserInfo(accessToken) // suspend 함수이므로 execute() 불필요

                if (response.isSuccessful) {
                    val userInfo = response.body()?.result
                    if (userInfo != null) {
                        val newAccessToken = userInfo.signInDTO.tokens.accessToken
                        val newRefreshToken = userInfo.signInDTO.tokens.refreshToken
                        val memberId = userInfo.signInDTO.memberId

                        UserApiClient.instance.me { user, error ->
                            if (error == null && user != null) {
                                val nickname = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: ""

                                tokenManager.saveUserInfo(
                                    platform = "kakao",
                                    email = userInfo.signInDTO.email,
                                    nickname = nickname,
                                    profileImageUrl = profileImageUrl,
                                    accessToken = newAccessToken,
                                    refreshToken = newRefreshToken,
                                    memberId = memberId
                                )
                            }
                        }
                        RetrofitInstance.setAuthToken(newAccessToken)
                        Result.success(userInfo)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LoginRepository", "API 응답 실패: $errorBody")
                        Result.failure(Exception("API 응답 실패: $errorBody"))
                    }
                } else {
                    Result.failure(Exception("API 응답 실패: ${response.errorBody()?.string()}"))
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
            val request = NaverLoginRequest(accessToken, refreshToken, tokenType, expiresIn)
            val response = api.signInWithNaver(request)

            if (response.isSuccessful) {
                val userInfo = response.body()?.result
                if (userInfo != null) {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(response: NidProfileResponse) {
                            val profile = response.profile
                            if (profile != null) {
                                val nickname = profile.nickname ?: "Unknown"
                                val profileImageUrl = profile.profileImage ?: ""

                                // ✅ 사용자 정보 저장
                                tokenManager.saveUserInfo(
                                    platform = "naver",
                                    email = userInfo.signInDTO.email,
                                    nickname = nickname,
                                    profileImageUrl = profileImageUrl,
                                    accessToken = userInfo.signInDTO.tokens.accessToken,
                                    refreshToken = userInfo.signInDTO.tokens.refreshToken,
                                    memberId = userInfo.signInDTO.memberId
                                )
                                RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)
                            } else {
                                Log.e("Naver", "네이버 프로필 정보를 가져올 수 없습니다.")
                            }
                        }

                        override fun onError(errorCode: Int, message: String) {
                            Log.e("Naver", "네이버 프로필 API 오류: $message")
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            Log.e("Naver", "네이버 프로필 요청 실패: $message")
                        }
                    })

                    return@withContext Result.success(userInfo)
                } else {
                    return@withContext Result.failure(Exception("네이버 로그인 응답 실패"))
                }
            } else {
                return@withContext Result.failure(Exception("네이버 로그인 API 오류: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Log.e("NaverAPI", "API 호출 중 예외 발생", e)
            return@withContext Result.failure(e)
        }
    }



}
