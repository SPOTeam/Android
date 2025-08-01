package com.spot.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.spot.android.databinding.ActivitySplashBinding
import com.spot.android.login.LoginApiService
import com.spot.android.login.NicNameActivity
import com.spot.android.login.StartLoginActivity
import com.spot.android.login.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        val token = tokenManager.getAccessToken()

        if (!token.isNullOrEmpty()) {
            checkSpotMemberAndNavigateWithProgress()
        } else {
            navigateToStartLogin()
        }
    }

    private fun checkSpotMemberAndNavigateWithProgress() {

        lifecycleScope.launch {
            // Progress 채우며 검사
            for (progress in 1..100 step 10) {
                delay(50)
                binding.splashProgressbar.progress = progress
            }

            // 검사 호출
            checkSpotMemberAndNavigate()
        }
    }

    private fun checkSpotMemberAndNavigate() {
        val apiService = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        apiService.checkIsSpotMember().enqueue(object : Callback<SpotMemberCheckResponse> {
            override fun onResponse(
                call: Call<SpotMemberCheckResponse>,
                response: Response<SpotMemberCheckResponse>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val isSpotMember = response.body()!!.result.isSpotMember
                    if (isSpotMember) {
                        navigateToMain()
                    } else {
                        navigateToNickName() // 가입 미완료
                    }
                } else {
                    navigateToStartLogin() // 상태 검사 실패 → 로그인 유도
                }
            }

            override fun onFailure(
                call: Call<SpotMemberCheckResponse>,
                t: Throwable
            ) {
                navigateToStartLogin() // 네트워크 오류 → 로그인 유도
            }
        })
    }


    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToNickName() {
        startActivity(Intent(this, NicNameActivity::class.java))
        finish()
    }

    private fun navigateToStartLogin() {
        startActivity(Intent(this, StartLoginActivity::class.java))
        finish()
    }
}
