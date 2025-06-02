package com.example.spoteam_android.domain.login.entity

data class NickNameResponse(
    val name: String,
    val nickname: String,
    val email: String,
    val personalInfo: Boolean
)