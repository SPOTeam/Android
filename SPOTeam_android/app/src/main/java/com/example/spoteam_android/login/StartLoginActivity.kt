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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginRepository = LoginRepository()
        val viewModelFactory = LoginViewModelFactory(loginRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        checkIfAlreadyLoggedIn()
        setupObservers()

        binding.itemLogoKakaoIb.setOnClickListener { startKakaoLogin() }
        binding.itemLogoNaverIb.setOnClickListener { startNaverLogin() }

        binding.activityStartLoginNextBt.setOnClickListener {
            val loginIntent = Intent(this, NormalLoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun checkIfAlreadyLoggedIn() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val isLoggedIn = email?.let { sharedPreferences.getBoolean("${it}_isLoggedIn", false) } ?: false
        val accessToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }

        if (isLoggedIn && !accessToken.isNullOrEmpty()) {
            SocialLoginRetrofitInstance.setToken(accessToken)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess { userInfo ->
                fetchAndSaveUserInfo(userInfo, "kakao")
            }.onFailure { exception ->
                Log.e("Token", "카카오 토큰 전송 실패: ${exception.message}")
            }
        }

        loginViewModel.naverLoginResult.observe(this) { result ->
            result.onSuccess { userInfo ->
                fetchAndSaveUserInfo(userInfo, "naver")
            }.onFailure { exception ->
                Log.e("Token", "네이버 토큰 전송 실패: ${exception.message}")
            }
        }

        // 사용자 정보 저장 완료 후 화면 이동을 분리
        loginViewModel.userInfoSaved.observe(this) { isSaved ->
            if (isSaved) {
                navigateToNextScreen() // 화면 이동
            }
        }
    }

    private fun startKakaoLogin() {
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
                Log.d("KakaoLogin", "AccessToken: ${token.accessToken}")
                Toast.makeText(this, "카카오 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                loginViewModel.sendTokenToServer(token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    private fun startNaverLogin() {
        val naverCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                Toast.makeText(this@StartLoginActivity, "네이버 로그인 성공", Toast.LENGTH_SHORT).show()

                val accessToken = NaverIdLoginSDK.getAccessToken()
                val refreshToken = NaverIdLoginSDK.getRefreshToken()
                val expiresAt = NaverIdLoginSDK.getExpiresAt()
                val tokenType = NaverIdLoginSDK.getTokenType()

                if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() && !tokenType.isNullOrEmpty()) {
                    loginViewModel.sendNaverTokenToServer(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tokenType = tokenType,
                        expiresIn = expiresAt
                    )
                } else {
                    Log.e("NaverLogin", "토큰 데이터를 가져오지 못했습니다.")
                    Toast.makeText(this@StartLoginActivity, "로그인 실패: 토큰 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorMessage = NaverIdLoginSDK.getLastErrorDescription()
                Log.e("NaverLogin", "네이버 로그인 실패: $errorCode, $errorMessage")
                Toast.makeText(this@StartLoginActivity, "네이버 로그인 실패: $message", Toast.LENGTH_SHORT).show()
            }

            override fun onError(errorCode: Int, message: String) {
                Log.e("NaverLogin", "네이버 로그인 에러: $errorCode, $message")
                Toast.makeText(this@StartLoginActivity, "네이버 로그인 오류: $message", Toast.LENGTH_SHORT).show()
            }
        }

        NaverIdLoginSDK.authenticate(this, naverCallback)
    }

    private fun fetchAndSaveUserInfo(userInfo: Any, platform: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        when (platform) {
            "kakao" -> {
                if (userInfo is KaKaoResult) {
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("Kakao", "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            val nickname = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                            val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: ""

                            saveUserInfo(platform, userInfo.signInDTO.email, nickname, profileImageUrl, userInfo.signInDTO.tokens.accessToken, userInfo.signInDTO.tokens.refreshToken, userInfo.signInDTO.memberId)
                            loginViewModel.saveUserInfoToPreferences() // ViewModel 상태 업데이트
                        }
                    }
                }
            }
            "naver" -> {
                if (userInfo is NaverResult) {
                    NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                        override fun onSuccess(response: NidProfileResponse) {
                            val profile = response.profile
                            if (profile != null) {
                                val nickname = profile.nickname ?: "Unknown"
                                val profileImageUrl = profile.profileImage ?: ""

                                saveUserInfo(platform, userInfo.signInDTO.email, nickname, profileImageUrl, userInfo.signInDTO.tokens.accessToken, userInfo.signInDTO.tokens.refreshToken, userInfo.signInDTO.memberId)
                                loginViewModel.saveUserInfoToPreferences() // ViewModel 상태 업데이트
                            } else {
                                Log.e("Naver", "프로필 정보를 가져올 수 없습니다.")
                            }
                        }

                        override fun onFailure(httpStatus: Int, message: String) {
                            Log.e("Naver", "프로필 요청 실패: $httpStatus, $message")
                        }

                        override fun onError(errorCode: Int, message: String) {
                            Log.e("Naver", "프로필 요청 중 오류 발생: $errorCode, $message")
                        }
                    })
                }
            }
        }
    }


    private fun saveUserInfo(
        platform: String,
        email: String,
        nickname: String,
        profileImageUrl: String,
        accessToken: String,
        refreshToken: String,
        memberId: Int
    ) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("${email}_isLoggedIn", true)
            putString("${email}_accessToken", accessToken)
            putString("${email}_refreshToken", refreshToken)
            putInt("${email}_memberId", memberId)
            putString("${email}_nickname", nickname)
            putString("${email}_${platform}ProfileImageUrl", profileImageUrl)
            putString("currentEmail", email)
            putString("loginPlatform", platform)
            apply()
        }
    }

    private fun navigateToNextScreen() {
        // 테마 정보 확인 후 화면 이동
        fetchThemesAndNavigate()
    }


    private fun fetchThemesAndNavigate() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getThemes().enqueue(object : Callback<ThemeApiResponse> {
            override fun onResponse(
                call: Call<ThemeApiResponse>,
                response: Response<ThemeApiResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val themes = apiResponse.result.themes
                        if (themes.isNotEmpty()) {
                            // 테마가 존재하면 MainActivity로 이동
                            navigateToActivity(MainActivity::class.java)
                        } else {
                            // 테마가 없으면 CheckListCategoryActivity로 이동
                            navigateToActivity(CheckListCategoryActivity::class.java)
                        }
                    } else {
                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                        Log.e("NavigateToNextScreen", "테마 가져오기 실패: $errorMessage")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                    Log.e("NavigateToNextScreen", "테마 가져오기 실패: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ThemeApiResponse>, t: Throwable) {
                Log.e("NavigateToNextScreen", "테마 가져오기 오류", t)
            }
        })
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

}