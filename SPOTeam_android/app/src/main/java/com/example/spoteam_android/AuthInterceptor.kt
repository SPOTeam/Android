package com.example.spoteam_android

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 현재 요청에 대해 새로운 요청을 생성합니다.
        val request = chain.request().newBuilder()
            .apply {
                val token = TokenManager.getToken()
                if (token != null) {
                    addHeader("Authorization", "Bearer $token") // 동적 토큰 사용
                }
            }
            .build()

        // 요청을 진행합니다.
        return chain.proceed(request)
    }
}
