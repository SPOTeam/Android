package com.example.spoteam_android.data.login.datasource.remote

import android.app.Activity
import com.example.spoteam_android.domain.login.entity.KakaoLoginResponse
import com.example.spoteam_android.domain.login.entity.NaverLoginRequest

interface SocialLoginDataSource {
    suspend fun loginWithKakao(activity: Activity): Result<KakaoLoginResponse>
    suspend fun loginWithNaver(activity: Activity): Result<NaverLoginRequest>
}
