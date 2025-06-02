package com.example.spoteam_android.presentation.login.nickname

sealed class NicknameState {

    object Default : NicknameState()

    object Valid : NicknameState()

    object Error : NicknameState()

}