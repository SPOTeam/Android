package com.example.spoteam_android.domain.login.entity


data class NickNameCheckResponse(
    val nickname: String,
    val duplicate: Boolean
)
