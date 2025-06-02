package com.example.spoteam_android.domain.login.entity

data class WithdrawResponse(
    val memberId: Int,
    val name: String,
    val email: String,
    val inactive: String
)