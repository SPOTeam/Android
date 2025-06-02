package com.example.spoteam_android.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.example.spoteam_android.domain.login.entity.SocialLoginResponse
import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import com.example.spoteam_android.presentation.login.nickname.NicknameActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var tokenDataSource: TokenDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition {
            loginViewModel.loginState.value == LoginState.LOADING
        }

        super.onCreate(savedInstanceState)
        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val token = tokenDataSource.getAccessToken()
            if (!token.isNullOrEmpty()) {
                runCatching {
                    loginViewModel.checkSpotMember()
                }.onSuccess { result ->
                    if (result.isSuccess) {
                        loginViewModel.updateLoginState(LoginState.SUCCESS)
                        lifecycleScope.launch {
                            loginViewModel.getNickname()
                                .onSuccess { tokenDataSource.saveNickname(it) }
                                .onFailure { showErrorToast("닉네임 조회 실패: ${it.message}") }
                        }
                        goToMain()
                    } else {
                        loginViewModel.updateLoginState(LoginState.SUCCESS)
                        goToNicknameActivity()
                    }
                }.onFailure {
                    loginViewModel.updateLoginState(LoginState.FAILURE)
                    showErrorToast("자동 로그인 실패: ${it.message}")
                }
            } else {
                loginViewModel.updateLoginState(LoginState.IDLE)
            }
        }

        setupObservers()

        binding.itemLogoKakaoIb.setOnClickListener {
            loginViewModel.loginWithKakao(this)
        }

        binding.itemLogoNaverIb.setOnClickListener {
            loginViewModel.loginWithNaver(this)
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            result?.onSuccess { model ->
                saveUserInfo(model)

                if (model.isSpotMember) {
                    lifecycleScope.launch {
                        loginViewModel.getNickname()
                            .onSuccess { tokenDataSource.saveNickname(it) }
                            .onFailure { showErrorToast("닉네임 조회 실패: ${it.message}") }
                    }
                    goToMain()
                } else {
                    goToNicknameActivity()
                }
            }?.onFailure {
                showErrorToast("로그인 실패: ${it.message}")
            }
        }
    }

    private fun saveUserInfo(model: SocialLoginResponse) {
        tokenDataSource.saveUserInfo(
            platform = model.loginType,
            email = model.email,
            profileImageUrl = model.profileImage ?: "",
            accessToken = model.tokens.accessToken,
            refreshToken = model.tokens.refreshToken,
            memberId = model.memberId
        )
    }

    private fun goToNicknameActivity() {
        val intent = Intent(this, NicknameActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
