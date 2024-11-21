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
                RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)
                _loginResult.value = Result.success(userInfo) // MutableLiveData인 _loginResult에 값 설정

            }.onFailure { exception ->
                _loginResult.value = Result.failure(exception)
            }
        }
    }
    //서버로 토큰 전송
    fun sendNaverTokenToServer(accessToken: String) {
        viewModelScope.launch {
            val result = loginRepository.sendNaverTokenToServer(accessToken)
            result.onSuccess { userInfo ->
                RetrofitInstance.setAuthToken(userInfo.signInDTO.tokens.accessToken)
                _naverLoginResult.value = Result.success(userInfo)
            }.onFailure { exception ->
                _naverLoginResult.value = Result.failure(exception)
            }
        }
    }
}
