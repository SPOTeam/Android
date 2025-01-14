package com.example.spoteam_android.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.RetrofitInstance
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<KaKaoResult>>()
    val loginResult: LiveData<Result<KaKaoResult>> get() = _loginResult

    private val _naverLoginResult = MutableLiveData<Result<NaverResult>>()
    val naverLoginResult: LiveData<Result<NaverResult>> get() = _naverLoginResult

    // 서버로 토큰 전송
    fun sendTokenToServer(accessToken: String) {
        viewModelScope.launch {
            val result = loginRepository.sendTokenToServer(accessToken)
            result.onSuccess { userInfo ->
                // 받은 accessToken을 RetrofitInstance에 설정
                Log.d("kakaoLogin333", "서버 응답 성공: ${userInfo.isSpotMember}")
                RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)
                _loginResult.value = Result.success(userInfo) // MutableLiveData인 _loginResult에 값 설정

            }.onFailure { exception ->
                _loginResult.value = Result.failure(exception)
            }
        }
    }
    //서버로 토큰 전송
    fun sendNaverTokenToServer(
        accessToken: String,
        refreshToken: String,
        tokenType: String,
        expiresIn: Long
    ) {
        viewModelScope.launch {
            val result = loginRepository.sendNaverTokenToServer(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = tokenType,
                expiresIn = expiresIn
            )
            result.onSuccess { userInfo ->
                Log.d("NaverLogin333", "서버 응답 성공: ${userInfo.signInDTO}")
                RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)
                _naverLoginResult.value = Result.success(userInfo)
            }.onFailure { exception ->
                Log.e("NaverLogin333", "서버 응답 실패: ${exception.message}")
                _naverLoginResult.value = Result.failure(exception)
            }
        }
    }


}
