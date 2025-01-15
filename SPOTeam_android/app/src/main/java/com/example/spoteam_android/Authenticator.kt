package com.example.spoteam_android

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.spoteam_android.login.StartLoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val currentToken = getAccessTokenFromPreferences()

        request = request.newBuilder()
            .header("Authorization", "Bearer $currentToken")
            .build()

        var response = chain.proceed(request)

        if (response.code == 400) {
            synchronized(this) {
                val updatedToken = getAccessTokenFromPreferences()
                val token = if (currentToken != updatedToken) {
                    updatedToken
                } else {
                    refreshToken()?.also {
                        saveAccessTokenToPreferences(it)
                        RetrofitInstance.setAuthToken(it)
                    }
                }

                token?.let {
                    response.close()
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                    response = chain.proceed(request)
                } ?: run {
                    handleInvalidRefreshToken()
                }
            }
        }
        return response
    }
    private fun getNewTokens(refreshToken: String): TokenResult? {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApi = retrofit.create(AuthApiService::class.java)
        val response = authApi.refreshToken(refreshToken).execute()

        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.isSuccess == true) {
                    Log.d("AuthInterceptor", "Token refresh succeeded: ${body.result}")
                body.result
            } else {
                Log.e("AuthInterceptor", "Token refresh failed: code: ${body?.code}, message: ${body?.message}")
                if (body?.code == "COMMON4009") {
                    handleInvalidRefreshToken()  // 유효하지 않은 경우 재로그인
                }
                null
            }
        } else {
            Log.e("AuthInterceptor", "Token refresh request failed with code: ${response.code()}, errorBody: ${response.errorBody()?.string()}")
            null
        }
    }


    private fun refreshToken(): String? {
        val refreshToken = getRefreshTokenFromPreferences()
        return refreshToken?.let {
            val newTokens = getNewTokens(it)
            if (newTokens != null) {
                saveTokensToPreferences(newTokens.accessToken, newTokens.refreshToken)
                RetrofitInstance.setAuthToken(newTokens.accessToken)
                newTokens.accessToken
            } else {
                // 리프레시 토큰이 유효하지 않다면 null 반환
                null
            }
        }
    }

    private fun getAccessTokenFromPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        return email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }
    private fun getRefreshTokenFromPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        return email?.let { sharedPreferences.getString("${it}_refreshToken", null) }
    }

    private fun saveAccessTokenToPreferences(accessToken: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            sharedPreferences.edit().putString("${it}_accessToken", accessToken).apply()
        }
    }
    private fun saveTokensToPreferences(accessToken: String, refreshToken: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            with(sharedPreferences.edit()) {
                putString("${it}_accessToken", accessToken)
                putString("${it}_refreshToken", refreshToken)
                apply()
            }
        }
    }


    private fun handleInvalidRefreshToken() {
        // SharedPreferences에 저장된 로그인 정보 지우기
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // 앱 내 데이터 초기화 및 오류 발생시 및 스택 모두 제거하고 로그인화면으로 이동 (현재는 회원가입 화면)
        val intent = Intent(context, StartLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }




}
