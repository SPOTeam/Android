package com.example.spoteam_android.login

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.RetrofitInstance
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository): ViewModel() {

    private val _loginResult = MutableLiveData<Result<KaKaoResult>>()
    val loginResult: LiveData<Result<KaKaoResult>> get() = _loginResult

    private val _naverLoginResult = MutableLiveData<Result<NaverResult>>()
    val naverLoginResult: LiveData<Result<NaverResult>> get() = _naverLoginResult



    fun startKakaoLogin(activity: Activity) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                _loginResult.postValue(Result.failure(Exception(error.message ?: "카카오 로그인 실패")))
            } else if (token != null) {
                viewModelScope.launch {
                    val result = loginRepository.sendKakaoTokenToServer(token.accessToken)
                    _loginResult.postValue(result)
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = callback)
        }
    }

    fun startNaverLogin(activity: Activity) {
        val naverCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()
                val expiresAt = NaverIdLoginSDK.getExpiresAt()
                val tokenType = NaverIdLoginSDK.getTokenType() !!

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
                    viewModelScope.launch {
                        val result = loginRepository.sendNaverTokenToServer(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            tokenType = tokenType,
                            expiresIn = expiresAt
                        )
                        _naverLoginResult.postValue(result) // UI에서 로그인 결과 감지
                    }
                } else {
                    _naverLoginResult.postValue(Result.failure(Exception("네이버 토큰이 유효하지 않습니다.")))
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                _naverLoginResult.postValue(Result.failure(Exception("네이버 로그인 실패: $message")))
            }

            override fun onError(errorCode: Int, message: String) {
                _naverLoginResult.postValue(Result.failure(Exception("네이버 로그인 오류: $message")))
            }
        }

        NaverIdLoginSDK.authenticate(activity, naverCallback)
    }

}


