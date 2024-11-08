package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginRepository = LoginRepository()
        val viewModelFactory = LoginViewModelFactory(loginRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        checkIfAlreadyLoggedIn()
        setupObservers()

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                val message = when (error.toString()) {
                    AuthErrorCause.AccessDenied.toString() -> "접근이 거부 됨(동의 취소)"
                    AuthErrorCause.InvalidClient.toString() -> "유효하지 않은 앱"
                    AuthErrorCause.InvalidGrant.toString() -> "인증 수단이 유효하지 않아 인증할 수 없는 상태"
                    AuthErrorCause.Misconfigured.toString() -> "설정이 올바르지 않음(android key hash)"
                    AuthErrorCause.ServerError.toString() -> "서버 내부 에러"
                    AuthErrorCause.Unauthorized.toString() -> "앱이 요청 권한이 없음"
                    else -> "기타 에러"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                loginViewModel.sendTokenToServer(token.accessToken)
                Log.d("Token_Success", token.accessToken)
            }
        }

        binding.itemLogoKakaoIb.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        binding.activityStartLoginNextBt.setOnClickListener {
            val loginIntent = Intent(this, NormalLoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun checkIfAlreadyLoggedIn() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)  // currentEmail 가져오기
        val isLoggedIn = email?.let { sharedPreferences.getBoolean("${it}_isLoggedIn", false) } ?: false
        val accessToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }

        if (isLoggedIn && !accessToken.isNullOrEmpty()) {
            RetrofitInstance.setAuthToken(accessToken)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess { userInfo ->
                fetchAndSaveKakaoUserInfo(userInfo)
                navigateToNextScreen(userInfo)
            }.onFailure { exception ->
                Log.e("Token", "토큰 전송 실패: ${exception.message}")
            }
        }
    }

    private fun fetchAndSaveKakaoUserInfo(userInfo: KaKaoResult) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("Kakao", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val nickname = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: ""

                saveUserInfo(userInfo, nickname, profileImageUrl)
            }
        }
    }

    private fun saveUserInfo(userInfo: KaKaoResult, nickname: String, profileImageUrl: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("${userInfo.email}_isLoggedIn", true)
            putString("${userInfo.email}_accessToken", userInfo.tokens.accessToken)
            putString("${userInfo.email}_refreshToken", userInfo.tokens.refreshToken)
            putInt("${userInfo.email}_memberId", userInfo.memberId)
            putString("${userInfo.email}_nickname", nickname)
            putString("${userInfo.email}_kakaoProfileImageUrl", profileImageUrl)
            putString("currentEmail", userInfo.email)  // 현재 로그인된 사용자 추적용
            apply()
        }
    }

    private fun navigateToNextScreen(userInfo: KaKaoResult) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = userInfo.email
        val isLoggedIn = sharedPreferences.getBoolean("${email}_isLoggedIn", false)

        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, CheckListCategoryActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

}
