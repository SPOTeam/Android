package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        val isLoggedIn = currentEmail != null && sharedPreferences.getBoolean(
            "${currentEmail}_isLoggedIn", false
        )
// 회원가입 화면 작업중이라 여기 아래 코드 주석 처리했습니다. 메인 화면 작업하실꺼면 아래 코드 주석 처리 해제하고 사용하면 됩니다.
//        if (isLoggedIn) {
//            // 이미 로그인된 경우 MainActivity로 이동
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()  // 현재 Activity를 종료
//            return
//        }

        // 로그인 실패 시 혹은 초기화된 상태에서 로그인 처리
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
                sendTokenToServer(token.accessToken)
            }
        }

        binding.itemLogoKakaoIb.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

//        binding.loginwithspotBt.setOnClickListener {
//            val intent = Intent(this, CheckListCategoryActivity::class.java)
//            startActivity(intent)
//        }
    }


    private fun sendTokenToServer(accessToken: String) {
        Log.d("Token", "전송할 액세스 토큰: $accessToken")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(KaKaoApiService::class.java)

        api.getUserInfo(accessToken).enqueue(object : Callback<YourResponse> {
            override fun onResponse(call: Call<YourResponse>, response: Response<YourResponse>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()?.result
                    if (userInfo != null) {
                        Log.d(
                            "UserInfo",
                            "memberId: ${userInfo.memberId}, email: ${userInfo.email}"
                        )

                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e("Kakao", "사용자 정보 요청 실패", error)
                            } else if (user != null) {
                                val nickname = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                                val kakaoProfileImageUrl = user.kakaoAccount?.profile?.profileImageUrl ?: "" // 프로필 이미지 URL 가져오기

                                Log.d("Kakao", "사용자 닉네임: $nickname")
                                Log.d("Kakao", "카카오 프로필 이미지 URL: $kakaoProfileImageUrl")

                                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                                val randomNickname = sharedPreferences.getString("${userInfo.email}_randomNickname", null)

                                with(sharedPreferences.edit()) {
                                    putBoolean("${userInfo.email}_isLoggedIn", true)
                                    putString("${userInfo.email}_accessToken", accessToken)
                                    putInt("${userInfo.email}_memberId", userInfo.memberId)
                                    putString("${userInfo.email}_nickname", nickname)  // 카카오 닉네임 저장
                                    putString("${userInfo.email}_kakaoProfileImageUrl", kakaoProfileImageUrl) // 카카오 프로필 이미지 URL 저장
                                    putString("currentEmail", userInfo.email)
                                    apply()
                                }

                                // randomNickname이 존재하면 MainActivity로, 그렇지 않으면 CheckListCategoryActivity로 이동
                                val intent = if (randomNickname != null) {
                                    Intent(this@StartLoginActivity, MainActivity::class.java)
                                } else {
                                    Intent(this@StartLoginActivity, CheckListCategoryActivity::class.java)
                                }

                                startActivity(intent)
                                finish() // 로그인 완료 후 현재 Activity 종료
                            }
                        }
                    }
                } else {
                    Log.e("Token", "토큰 전송 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<YourResponse>, t: Throwable) {
                Log.e("Token", "토큰 전송 실패: ${t.message}")
            }
        })
    }
}



    // Retrofit 관련 인터페이스 정의
interface KaKaoApiService {
    @GET("/spot/members/sign-in/kakao") // 엔드포인트 경로로 변경
    fun getUserInfo(@Query("accessToken") accessToken: String): Call<YourResponse>
}

// 토큰 정보 담기
data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)

// 서버 응답 결과 담기
data class Result(
    val tokens: Tokens,
    val email: String,
    val memberId: Int
)

// 서버 응답 담기
data class YourResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Result
)