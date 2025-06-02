package com.example.spoteam_android.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context
) : TokenDataSource {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    private fun getCurrentEmail(): String? =
        sharedPreferences.getString("currentEmail", null)

    override fun saveUserInfo(
        platform: String,
        email: String,
        profileImageUrl: String,
        accessToken: String,
        refreshToken: String,
        memberId: Int,
    ) {
        with(sharedPreferences.edit()) {
            putBoolean("${email}_isLoggedIn", true)
            putString("${email}_accessToken", accessToken)
            putString("${email}_refreshToken", refreshToken)
            putInt("${email}_memberId", memberId)
            putString("${email}_${platform}ProfileImageUrl", profileImageUrl)
            putString("currentEmail", email)
            putString("loginPlatform", platform)
            commit()
        }
    }

    override fun saveNickname(nickname: String) {
        val email = getCurrentEmail()
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_nickname", nickname)
                apply()
            }
        }
    }


    override fun getAccessToken(): String? {
        val email = getCurrentEmail()
        return email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }

    override fun getRefreshToken(): String? {
        val email = getCurrentEmail()
        return email?.let { sharedPreferences.getString("${it}_refreshToken", null) }
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        val email = getCurrentEmail()
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_accessToken", accessToken)
                putString("${it}_refreshToken", refreshToken)
                apply()
            }
        }
    }

    override fun getIsSpotMember(): Boolean {
        val email = getCurrentEmail()
        return email?.let {
            sharedPreferences.getBoolean("${it}_is_spot_member", false)
        } ?: false
    }


    override fun isUserLoggedIn(): Boolean {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()
        return !accessToken.isNullOrEmpty() || !refreshToken.isNullOrEmpty()
    }

    override fun getMemberId(): Int {
        val email = getCurrentEmail()
        return email?.let {
            sharedPreferences.getInt("${it}_memberId", -1)
        } ?: -1
    }

    override fun getNickname(): String? {
        val email = getCurrentEmail()
        return email?.let {
            sharedPreferences.getString("${it}_nickname", null)
        }
    }

    override fun getEmail(): String? {
        return sharedPreferences.getString("currentEmail", null)
    }

    override fun getLoginPlatform(): String? {
        return sharedPreferences.getString("loginPlatform", null)
    }

    override fun getProfileImageUrl(platform: String): String? {
        val email = getCurrentEmail()
        return email?.let {
            sharedPreferences.getString("${it}_${platform}ProfileImageUrl", null)
        }
    }

    override fun clearAll() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }


    }
}
