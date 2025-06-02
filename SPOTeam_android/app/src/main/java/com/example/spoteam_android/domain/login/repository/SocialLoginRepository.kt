package com.example.spoteam_android.domain.login.repository

import android.app.Activity
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse

interface SocialLoginRepository {
    suspend fun loginWithKakao(activity: Activity): Result<SocialLoginResponse>
    suspend fun loginWithNaver(activity: Activity): Result<SocialLoginResponse>
}