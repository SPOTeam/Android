package com.spot.android.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.spot.android.MainActivity
import com.spot.android.databinding.ActivityStartLoginBinding

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        setupLoginUI() // UI만 준비
    }

    private fun setupLoginUI() {
        val loginRepository = LoginRepository(tokenManager)
        val viewModelFactory = LoginViewModelFactory(loginRepository)
        loginViewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        setupObservers()

        binding.itemLogoKakaoIb.setOnClickListener {
            loginViewModel.startKakaoLogin(this)
        }
        binding.itemLogoNaverIb.setOnClickListener {
            loginViewModel.startNaverLogin(this)
        }
    }

    private fun setupObservers() {
        loginViewModel.loginResult.observe(this) { result ->
            result.onSuccess { kakaoResult ->
                if (kakaoResult.isSpotMember) {
                    navigateToMain()
                } else {
                    navigateToNickName()
                }
            }.onFailure {
                showErrorToast("카카오 로그인 실패: ${it.message}")
            }
        }

        loginViewModel.naverLoginResult.observe(this) { result ->
            result.onSuccess { naverResult ->
                if (naverResult.isSpotMember) {
                    navigateToMain()
                } else {
                    navigateToNickName()
                }
            }.onFailure {
                showErrorToast("네이버 로그인 실패: ${it.message}")
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToNickName() {
        startActivity(Intent(this, NicNameActivity::class.java))
        finish()
    }

    private fun showErrorToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
