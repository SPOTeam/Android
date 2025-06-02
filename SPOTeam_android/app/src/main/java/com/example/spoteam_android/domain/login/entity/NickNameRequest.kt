package com.example.spoteam_android.domain.login.entity

data class NickNameRequest(
    val nickname: String,
    val personalInfo: Boolean,
    val idInfo: Boolean
)