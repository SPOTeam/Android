package com.example.spoteam_android.login

import android.app.Activity
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

//소셜 로그인을 관리하는 객체
object SocialLoginManager {
    fun loginWithKakao(activity: Activity, callback: (String?) -> Unit) {
        val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("Kakao", "로그인 실패", error)
                callback(null)
            } else if (token != null) {
                callback(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
            UserApiClient.instance.loginWithKakaoTalk(activity, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity, callback = kakaoCallback)
        }
    }
}
