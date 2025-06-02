package com.example.spoteam_android.interceptor

import android.content.Intent
import com.example.spoteam_android.AuthApiService
import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import com.example.spoteam_android.presentation.login.StartLoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    @ApplicationContext private val context: android.content.Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val currentToken = tokenDataSource.getAccessToken()

        if (!currentToken.isNullOrEmpty()) {
            request = request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }

        var response = chain.proceed(request)

        if (response.code == 401 || response.code == 400) {
            synchronized(this) {
                val updatedToken = tokenDataSource.getAccessToken()
                val token = if (currentToken != updatedToken) {
                    updatedToken
                } else {
                    refreshToken()?.also { tokenDataSource.saveTokens(it, tokenDataSource.getRefreshToken() ?: "") }
                }

                token?.let {
                    response.close()
                    request = request.newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                    return chain.proceed(request)
                } ?: run {
                    handleInvalidRefreshToken()
                }
            }
        }
        return response
    }

    private fun refreshToken(): String? {
        val refreshToken = tokenDataSource.getRefreshToken() ?: return null
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authApi = retrofit.create(AuthApiService::class.java)
        val response = authApi.refreshToken(refreshToken).execute()

        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.isSuccess == true) {
                body.result?.accessToken
            } else {
                if (body?.code == "COMMON4009") handleInvalidRefreshToken()
                null
            }
        } else {
            null
        }
    }

    private fun handleInvalidRefreshToken() {
        tokenDataSource.saveTokens("", "")
        val intent = Intent(context, StartLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
