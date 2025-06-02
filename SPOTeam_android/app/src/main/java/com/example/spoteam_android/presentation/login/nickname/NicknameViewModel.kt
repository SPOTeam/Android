package com.example.spoteam_android.presentation.nickname

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import com.example.spoteam_android.domain.login.entity.NickNameRequest
import com.example.spoteam_android.domain.login.entity.NickNameResponse
import com.example.spoteam_android.domain.login.repository.LoginRepository
import com.example.spoteam_android.presentation.login.nickname.NicknameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenDataSource: TokenDataSource
) : ViewModel() {

    private val _nicknameState = MutableLiveData<NicknameState>(NicknameState.Default)
    val nicknameState: LiveData<NicknameState> = _nicknameState

    private val _nicknameUpdateResult = MutableLiveData<Result<NickNameResponse>>()
    val nicknameUpdateResult: LiveData<Result<NickNameResponse>> = _nicknameUpdateResult

    fun checkNickname(nickname: String) {
        if (!isNicknameValidFormat(nickname)) {
            _nicknameState.value = NicknameState.Error
            return
        }
        viewModelScope.launch {
            val result = loginRepository.checkNickName(nickname)
            result.onSuccess {
                _nicknameState.value = if (!it.duplicate) {
                    NicknameState.Valid
                } else {
                    NicknameState.Error
                }
            }.onFailure {
                _nicknameState.value = NicknameState.Default
            }
        }
    }

    fun updateNickname(nickname: String, personalInfo: Boolean, idInfo: Boolean) {
        viewModelScope.launch {
            val result = loginRepository.updateNickName(
                NickNameRequest(nickname, personalInfo, idInfo)
            )
            _nicknameUpdateResult.value = result
        }
    }

    fun saveNickname(nickname: String){
        tokenDataSource.saveNickname(nickname)
    }


    fun isNicknameValid(): Boolean = _nicknameState.value == NicknameState.Valid

    fun clearAll() = tokenDataSource.clearAll()


    private fun isNicknameValidFormat(nickname: String): Boolean {
        val nicknameRegex = Regex("^[가-힣a-zA-Z0-9_/]{1,8}$")
        return nicknameRegex.matches(nickname)
    }
}
