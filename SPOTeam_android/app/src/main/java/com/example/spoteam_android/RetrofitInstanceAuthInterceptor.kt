package com.example.spoteam_android

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.spoteam_android.presentation.login.StartLoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class RetrofitInstanceAuthInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val token = email?.let { sharedPreferences.getString("${it}_accessToken", null) }

        var request = chain.request()
        token?.let {
            request = request.newBuilder()
                .header("Authorization", "Bearer $it")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.e("AuthInterceptor", "401 Unauthorized - redirecting to login")
            val intent = Intent(context, StartLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }

        return response
    }
}