package com.example.spoteam_android.presentation.login

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.domain.login.entity.CheckSpotMemberResponse
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.domain.login.repository.LoginRepository
import com.example.spoteam_android.domain.login.repository.SocialLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialLoginRepository: SocialLoginRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<SocialLoginResponse>?>()
    val loginResult: LiveData<Result<SocialLoginResponse>?> = _loginResult

    private val _loginState = MutableLiveData<LoginState>(LoginState.LOADING)
    val loginState: LiveData<LoginState> = _loginState

    fun loginWithKakao(activity: Activity) {
        viewModelScope.launch {
            val result = socialLoginRepository.loginWithKakao(activity)
            _loginResult.value = result
        }
    }

    fun loginWithNaver(activity: Activity) {
        viewModelScope.launch {
            val result = socialLoginRepository.loginWithNaver(activity)
            _loginResult.value = result
        }
    }

    suspend fun checkSpotMember(): Result<CheckSpotMemberResponse> {
        return loginRepository.checkSpotMember()
    }

    suspend fun getNickname(): Result<String> = loginRepository.getNickname()

    fun updateLoginState(state: LoginState) {
        _loginState.value = state
    }
}
