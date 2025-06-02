package com.example.spoteam_android.data.login.datasourceimpl.remote

import android.app.Activity
import com.example.spoteam_android.data.login.datasource.remote.SocialLoginDataSource
import com.example.spoteam_android.domain.login.entity.KakaoLoginResponse
import com.example.spoteam_android.domain.login.entity.NaverLoginRequest
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class SocialLoginDataSourceImpl @Inject constructor() : SocialLoginDataSource {


    override suspend fun loginWithKakao(activity: Activity): Result<KakaoLoginResponse> {
        val result = CompletableDeferred<Result<KakaoLoginResponse>>()

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                result.complete(Result.failure(error))
            } else if (token != null) {
                result.complete(Result.success(KakaoLoginResponse(token.accessToken)))
            } else {
                result.complete(Result.failure(Exception("카카오 로그인 실패")))
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(
                context = activity,
                callback = callback
            )
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                context = activity,
                callback = callback
            )
        }
        return result.await()
    }



    override suspend fun loginWithNaver(activity: Activity): Result<NaverLoginRequest> {
        val result = CompletableDeferred<Result<NaverLoginRequest>>()

        val callback = object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()
                val expiresIn = NaverIdLoginSDK.getExpiresAt().toInt()
                val tokenType = NaverIdLoginSDK.getTokenType() !!

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    val info = NaverLoginRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tokenType = tokenType,
                        expiresIn = expiresIn
                    )
                    result.complete(Result.success(info))
                } else {
                    result.complete(Result.failure(Exception("네이버 토큰이 유효하지 않습니다")))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                result.complete(Result.failure(Exception("네이버 로그인 실패: $message")))
            }

            override fun onError(errorCode: Int, message: String) {
                result.complete(Result.failure(Exception("네이버 로그인 오류: $message")))
            }
        }

        NaverIdLoginSDK.authenticate(activity, callback)

        return result.await()
    }

}
