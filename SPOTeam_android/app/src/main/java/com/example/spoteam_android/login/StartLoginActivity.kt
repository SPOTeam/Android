package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.KaKaoResult
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.NaverResult
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tokenManager = TokenManager(this)
        val loginRepository = LoginRepository(tokenManager)
        val viewModelFactory = LoginViewModelFactory(loginRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        setupObservers()
        checkIfAlreadyLoggedIn()

        binding.itemLogoKakaoIb.setOnClickListener { loginViewModel.startKakaoLogin(this) }
        binding.itemLogoNaverIb.setOnClickListener { loginViewModel.startNaverLogin(this) }
        binding.itemLogoGoogleIb.setOnClickListener {
            val loginIntent = Intent(this, NormalLoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.activityStartLoginNextBt.setOnClickListener {
            val loginIntent = Intent(this, EmailVerificationActivity::class.java)
            startActivity(loginIntent)
        }
        binding.activityStartLoginAlreadyLoginTv.setOnClickListener {
            val loginIntent = Intent(this, NicNameActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun waitForTokenAndNavigate() {
        lifecycleScope.launch {
            var token: String?
            repeat(10) { //토큰 체크 후 화면 이동이 필요함.
                token = tokenManager.getAccessToken() //토큰 매니저를 통해 직접 토큰 가져옴
                if (!token.isNullOrEmpty()) {
                    navigateToNextScreen()
                    return@launch
                }
                Log.w("Login", "토큰 대기 (${it + 1}/10)")
                delay(500) // 0.5초씩 기다림
            }
            Log.e("Login", "토큰 발급지연")
            navigateToNextScreen() // 그래도 이동
        }
    }


    private fun checkIfAlreadyLoggedIn() {
        if (tokenManager.isUserLoggedIn()) {
            Log.d("AutoLogin", "자동 로그인")
            navigateToNextScreen() // 메인 화면으로 이동
        } else {
            Log.d("AutoLogin", "로그인 필요")
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess { waitForTokenAndNavigate() }
                .onFailure { showErrorToast("카카오 로그인 실패: ${it.message}") }
        }

        loginViewModel.naverLoginResult.observe(this) { result ->
            result.onSuccess { waitForTokenAndNavigate() }
                .onFailure { showErrorToast("네이버 로그인 실패: ${it.message}") }
        }
    }

    private fun navigateToNextScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}





//    private fun fetchThemesAndNavigate() {
//        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)
//
//        service.getThemes().enqueue(object : Callback<ThemeApiResponse> {
//            override fun onResponse(
//                call: Call<ThemeApiResponse>,
//                response: Response<ThemeApiResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val apiResponse = response.body()
//                    if (apiResponse != null && apiResponse.isSuccess) {
//                        val themes = apiResponse.result.themes
//                        if (themes.isNotEmpty()) {
//                            navigateToActivity(MainActivity::class.java)
//                        } else {
//                            navigateToActivity(CheckListCategoryActivity::class.java)
//                        }
//                    } else {
//                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
//                        Log.e("NavigateToNextScreen", "테마 가져오기 실패: $errorMessage")
//
//                        // 관심 테마를 찾을 수 없는 경우 CheckListCategoryActivity로 이동
//                        if (errorMessage.contains("해당하는 회원의 관심 테마를 찾을 수 없습니다.")) {
//                            navigateToActivity(CheckListCategoryActivity::class.java)
//                        }
//                    }
//                } else {
//                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
//                    Log.e("NavigateToNextScreen", "테마 가져오기 실패: $errorMessage")
//
//                    //  서버 오류 응답에서도 처리
//                    if (errorMessage.contains("해당하는 회원의 관심 테마를 찾을 수 없습니다.")) {
//                        navigateToActivity(CheckListCategoryActivity::class.java)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<ThemeApiResponse>, t: Throwable) {
//                Log.e("NavigateToNextScreen", "테마 가져오기 오류", t)
//            }
//        })
//    }

