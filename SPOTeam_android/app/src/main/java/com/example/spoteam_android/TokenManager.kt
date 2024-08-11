package com.example.spoteam_android

object TokenManager {
    private var token: String? = null

    fun getToken(): String? {
        return token
    }

    fun setToken(newToken: String) {
        token = newToken
    }
}
