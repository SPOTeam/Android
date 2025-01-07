package com.example.spoteam_android.login

import android.util.Log
import com.example.spoteam_android.YourResponse
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.NaverLoginRequest
import com.example.spoteam_android.NaverResult
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.create

// 서버와의 데이터 통신 처리, 서버에서 받은 결과를 정리해 뷰모델로 보내는 레포지토리
class LoginRepository {
    private val api: LoginApiService =
        SocialLoginRetrofitInstance.retrofit.create(LoginApiService::class.java)

    // 토큰을 서버에 보내고 사용자 정보를 가져오는 함수
    suspend fun sendTokenToServer(accessToken: String): Result<KaKaoResult> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val response = api.getUserInfo(accessToken).execute()
                if (response.isSuccessful) {
                    val userInfo = response.body()?.result
                    if (userInfo != null) {
                        Result.success(userInfo) // 성공적인 결과를 담아 반환
                    } else {
                        Result.failure(Exception("사용자 정보를 가져올 수 없습니다")) // 사용자 정보 없음
                    }
                } else {
                    Result.failure(
                        Exception(
                            "API 응답 실패: ${
                                response.errorBody()?.string()
                            }"
                        )
                    ) // API 응답 실패
                }
            } catch (e: Exception) {
                Result.failure(e) // 예외 발생 시 실패 반환
            }
        }

    suspend fun sendNaverTokenToServer(
        accessToken: String,
        refreshToken: String,
        tokenType: String,
        expiresIn: Long
    ): Result<NaverResult> = withContext(Dispatchers.IO) {
        try {
            // 요청 데이터 생성
            val request = NaverLoginRequest(
                access_token = accessToken,
                refresh_token = refreshToken,
                token_type = tokenType,
                expires_in = expiresIn
            )

            // Retrofit API 호출 (suspend function 활용)
            val response = api.signInWithNaver(request)

            // 성공 응답 처리
            val result = response.result
            if (result != null) {
                Log.d("NaverAPI", "성공 응답: $response")
                Result.success(result)
            } else {
                Log.e("NaverAPI", "응답이 성공적이지만 result가 null입니다. 응답 데이터: $response")
                Result.failure(Exception("사용자 정보를 가져올 수 없습니다."))
            }
        } catch (e: Exception) {
            // 네트워크 또는 기타 예외 처리
            Log.e("NaverAPI", "API 호출 중 예외 발생", e)
            Result.failure(e)
        }
    }

}
