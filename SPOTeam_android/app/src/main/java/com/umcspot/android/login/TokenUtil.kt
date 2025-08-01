package com.umcspot.android.login

import android.content.Context
import android.content.Intent
import android.util.Log
import com.umcspot.android.AuthApiService
import com.umcspot.android.BuildConfig
import com.umcspot.android.RetrofitInstance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TokenUtil {

    fun getStoredTokens(context: Context): Pair<String?, String?> {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = prefs.getString("currentEmail", null)
        val accessToken = email?.let { prefs.getString("${it}_accessToken", null) }
        val refreshToken = email?.let { prefs.getString("${it}_refreshToken", null) }
        return Pair(accessToken, refreshToken)
    }

    fun isExpired(token: String?): Boolean {
        return token.isNullOrEmpty() // 추후 JWT 파싱 후 만료 검사 추가 가능
    }

    fun refreshTokenSync(context: Context, refreshToken: String): String? {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AuthApiService::class.java)
        val call = api.refreshToken(refreshToken)
        val response = call.execute()

        return if (response.isSuccessful) {
            val result = response.body()?.result
            if (result != null) {
                saveTokensToPreferences(context, result.accessToken, result.refreshToken)
                RetrofitInstance.setAuthToken(result.accessToken)
                result.accessToken
            } else null
        } else {
            Log.e("TokenUtils", "Token refresh failed. Redirecting to login.")
            goToLoginScreen(context)
            null
        }
    }

    fun saveTokensToPreferences(context: Context, accessToken: String, refreshToken: String) {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = prefs.getString("currentEmail", null)
        email?.let {
            with(prefs.edit()) {
                putString("${it}_accessToken", accessToken)
                putString("${it}_refreshToken", refreshToken)
                apply()
            }
        }
    }

    fun goToLoginScreen(context: Context) {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(context, StartLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
