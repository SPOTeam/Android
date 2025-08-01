package com.umcspot.android

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umcspot.android.login.StartLoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val currentToken = getAccessTokenFromPreferences()

        if (currentToken != null &&
            !request.url.toString().contains("/spot/check/login-id") &&
            !request.url.toString().contains("/spot/check/email")
        ) {
            request = request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }

        // 요청 실행
        var response = chain.proceed(request)

        // 토큰 만료 처리: 401 또는 400 상태 코드 확인
        if (response.code == 401 || response.code == 400) {

            synchronized(this) {
                // 갱신된 액세스 토큰 확인
                val updatedToken = getAccessTokenFromPreferences()

                val token = if (currentToken != updatedToken) {
                    // 이미 갱신된 토큰이 있는 경우
                    updatedToken
                } else {
                    // 리프레시 토큰으로 새로운 토큰 갱신 시도
                    refreshToken()?.also {
                        saveAccessTokenToPreferences(it)
                        RetrofitInstance.setAuthToken(it)
                    }
                }

                token?.let {
                    Log.d("AuthInterceptor", "Token refresh successful.")
                    response.close()

                    // 새로운 토큰으로 요청 재시도
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                    response = chain.proceed(request)
                } ?: run {
                    Log.d("AuthInterceptor", "Token refresh failed. Navigating to login screen...")
                    handleInvalidRefreshToken()
                }
            }
        }
        return response
    }

    // 새로운 액세스 및 리프레시 토큰 요청
    private fun getNewTokens(refreshToken: String): TokenResult? {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApi = retrofit.create(AuthApiService::class.java)

        // 리프레시 토큰을 사용해 새로운 토큰 요청
        val response = authApi.refreshToken(refreshToken).execute()

        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.isSuccess == true) {
                Log.d("AuthInterceptor", "Token refresh succeeded: ${body.result}")
                body.result
            } else {
                Log.e("AuthInterceptor", "Token refresh failed: code: ${body?.code}, message: ${body?.message}")

                // 리프레시 토큰이 유효하지 않을 경우 로그인 화면으로 이동
                if (body?.code == "COMMON4009") {
                    handleInvalidRefreshToken()
                }
                null
            }
        } else {
            Log.e("AuthInterceptor", "Token refresh request failed with code: ${response.code()}, errorBody: ${response.errorBody()?.string()}")
            null
        }
    }

    // 리프레시 토큰으로 새로운 액세스 토큰 요청
    private fun refreshToken(): String? {
        val refreshToken = getRefreshTokenFromPreferences()
        return refreshToken?.let {
            val newTokens = getNewTokens(it)
            if (newTokens != null) {
                saveTokensToPreferences(newTokens.accessToken, newTokens.refreshToken)
                RetrofitInstance.setAuthToken(newTokens.accessToken)
                newTokens.accessToken
            } else {
                handleInvalidRefreshToken()
                null // 토큰 갱신 실패 시 null 반환
            }
        }
    }

    // SharedPreferences에서 액세스 토큰 가져오기
    private fun getAccessTokenFromPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        return email?.let { sharedPreferences.getString("${it}_accessToken", null) }
    }

    // SharedPreferences에서 리프레시 토큰 가져오기
    private fun getRefreshTokenFromPreferences(): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        return email?.let { sharedPreferences.getString("${it}_refreshToken", null) }
    }

    // SharedPreferences에 새로운 액세스 토큰 저장
    private fun saveAccessTokenToPreferences(accessToken: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        email?.let {
            sharedPreferences.edit().putString("${it}_accessToken", accessToken).apply()
        }
    }

    // SharedPreferences에 새로운 액세스 및 리프레시 토큰 저장
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

    // 리프레시 토큰이 유효하지 않을 때 로그인 화면으로 이동
    private fun handleInvalidRefreshToken() {
        Log.d("AuthInterceptor", "Invalid token. Clearing SharedPreferences and navigating to login screen...")
        // SharedPreferences 초기화
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // 로그인 화면으로 이동
        val intent = Intent(context, StartLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
