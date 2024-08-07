package com.example.spoteam_android.test

import android.content.Context
import android.content.SharedPreferences
import com.example.spoteam_android.data.ApiModels
import com.google.gson.Gson

fun saveToSharedPreferences(context: Context, data: ApiModels.ResponseData) {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // JSON 문자열로 변환하여 저장
    val jsonString = Gson().toJson(data)
    editor.putString("responseData", jsonString)

    // 개별 필드를 저장할 수도 있습니다.
    editor.putBoolean("isSuccess", data.isSuccess)
    editor.putString("code", data.code)
    editor.putString("message", data.message)
    editor.putInt("memberId", data.result.memberId)
    editor.putString("email", data.result.email)
    editor.putString("accessToken", data.result.tokens.accessToken)
    editor.putString("refreshToken", data.result.tokens.refreshToken)
    editor.putLong("accessTokenExpiresIn", data.result.tokens.accessTokenExpiresIn)

    editor.apply()
    println("saveToSharedPreferences()이 성공적으로 수행되었습니다.")
}

