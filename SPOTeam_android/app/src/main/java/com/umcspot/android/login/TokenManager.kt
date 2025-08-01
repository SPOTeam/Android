package com.umcspot.android.login

import android.content.Context
import android.content.SharedPreferences

class   TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveUserInfo(
        platform: String,
        email: String,
        nickname: String,
        profileImageUrl: String,
        accessToken: String,
        refreshToken: String,
        memberId: Int
    ) {
        with(sharedPreferences.edit()) {
            putBoolean("${email}_isLoggedIn", true)
            putString("${email}_accessToken", accessToken)
            putString("${email}_refreshToken", refreshToken)
            putInt("${email}_memberId", memberId)
            putString("${email}_nickname", nickname)
            putString("${email}_${platform}ProfileImageUrl", profileImageUrl)
            putString("currentEmail", email)
            putString("loginPlatform", platform)
            apply()
        }
    }

    fun getCurrentEmail(): String? =
        sharedPreferences.getString("currentEmail", null)

    fun getAccessToken(): String? {
        val email = getCurrentEmail()
        return email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }

    fun getRefreshToken(): String? {
        val email = getCurrentEmail()
        return email?.let { sharedPreferences.getString("${it}_refreshToken", null) }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        val email = getCurrentEmail()
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_accessToken", accessToken)
                putString("${it}_refreshToken", refreshToken)
                apply()
            }
        }
    }

    fun saveIsSpotMember(isMember: Boolean) {
        sharedPreferences.edit().putBoolean("is_spot_member", isMember).apply()
    }

    fun getIsSpotMember(): Boolean {
        return sharedPreferences.getBoolean("is_spot_member", false)
    }

    fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }

    fun isUserLoggedIn(): Boolean {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()
        return !accessToken.isNullOrEmpty() || !refreshToken.isNullOrEmpty()
    }


}
