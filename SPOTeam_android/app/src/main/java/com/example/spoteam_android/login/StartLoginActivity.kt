package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityStartLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AppsErrorCause
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient

class StartLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
//            if (error != null) {
//                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
//            } else if (tokenInfo != null) {
//                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, LogoutActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
//            }
//        }
//
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                val message = when (error.toString()) {
//                    AuthErrorCause.AccessDenied.toString() -> "접근이 거부 됨(동의 취소)"
//                    AuthErrorCause.InvalidClient.toString() -> "유효하지 않은 앱"
//                    AuthErrorCause.InvalidGrant.toString() -> "인증 수단이 유효하지 않아 인증할 수 없는 상태"
//                    AppsErrorCause.InvalidRequest.toString() -> "요청 파라미터 오류"
//                    AppsErrorCause.InvalidScope.toString() -> "유효하지 않은 scope ID"
//                    AuthErrorCause.Misconfigured.toString() -> "설정이 올바르지 않음(android key hash)"
//                    AuthErrorCause.ServerError.toString() -> "서버 내부 에러"
//                    AuthErrorCause.Unauthorized.toString() -> "앱이 요청 권한이 없음"
//                    else -> "기타 에러"
//                }
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//            } else if (token != null) {
//                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, LogoutActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
//            }
//        }

        binding.loginwithkakaoBt.setOnClickListener {
//            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
//                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
//            } else {
//                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
//            }

//            val intent = Intent(this, CheckListCategoryActivity::class.java)
//            startActivity(intent)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }

        binding.loginwithspotBt.setOnClickListener {
            val intent = Intent(this, CheckListCategoryActivity::class.java)
            startActivity(intent)
        }
    }
}