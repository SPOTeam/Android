package com.example.spoteam_android.data.login.datasource.local

interface TokenDataSource {
    fun saveUserInfo(
        platform: String,
        email: String,
        profileImageUrl: String,
        accessToken: String,
        refreshToken: String,
        memberId: Int,
    )

    fun saveNickname(nickname: String)

    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun saveTokens(accessToken: String, refreshToken: String)

    fun getIsSpotMember(): Boolean

    fun isUserLoggedIn(): Boolean

    fun getMemberId(): Int

    fun getNickname(): String?

    fun getEmail(): String?

    fun getLoginPlatform(): String?

    fun getProfileImageUrl(platform: String): String?

    fun clearAll()
}
