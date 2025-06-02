package com.example.spoteam_android.data.login.repositoryimpl

import android.app.Activity
import android.util.Log
import com.example.spoteam_android.data.login.datasource.remote.SocialLoginDataSource
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.domain.login.repository.LoginRepository
import com.example.spoteam_android.domain.login.repository.SocialLoginRepository
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class SocialLoginRepositoryImpl @Inject constructor(
    private val socialLoginDataSource: SocialLoginDataSource,
    private val loginRepository: LoginRepository
) : SocialLoginRepository {

    override suspend fun loginWithKakao(activity: Activity): Result<SocialLoginResponse> {
        val kakaoTokenResult = socialLoginDataSource.loginWithKakao(activity)
        return kakaoTokenResult.fold(
            onSuccess = { kakaoLoginResult ->
                val serverResult = loginRepository.signInWithKakao(kakaoLoginResult.accessToken)
                serverResult.map { model ->
                    var profileImage = ""
                    val done = CompletableDeferred<Unit>()

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("KakaoProfile", "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            profileImage = user.kakaoAccount?.profile?.profileImageUrl ?: ""
                        }
                        done.complete(Unit)
                    }

                    done.await()

                    model.copy(
                        profileImage = profileImage
                    )
                }
            },
            onFailure = {
                Log.e("KakaoLogin", "카카오 로그인 실패", it)
                return Result.failure(it)
            }
        )
    }

    override suspend fun loginWithNaver(activity: Activity): Result<SocialLoginResponse> {
        val naverResult = socialLoginDataSource.loginWithNaver(activity)
        return naverResult.fold(
            onSuccess = { tokenInfo ->
                val serverResult = loginRepository.signInWithNaver(tokenInfo)
                serverResult.map { model ->
                    var profileImage = ""
                    val done = CompletableDeferred<Unit>()

                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(response: NidProfileResponse) {
                            val profile = response.profile
                            profileImage = profile?.profileImage ?: ""
                            done.complete(Unit)
                        }

                        override fun onError(errorCode: Int, message: String) {
                            done.complete(Unit)
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            done.complete(Unit)
                        }
                    })

                    done.await()

                    model.copy(
                        profileImage = profileImage
                    )
                }
            },
            onFailure = {
                Log.e("NaverLogin", "네이버 로그인 실패", it)
                return Result.failure(it)
            }
        )
    }
}
