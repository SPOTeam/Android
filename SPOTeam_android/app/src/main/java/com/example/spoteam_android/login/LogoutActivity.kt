package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.ActivityLogoutBinding
import com.kakao.sdk.user.UserApiClient

class LogoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그아웃 버튼 클릭 리스너 설정
        binding.activityLogoutBt.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(this, "로그아웃 실패: $error", Toast.LENGTH_SHORT).show()
                    Log.d("error","$error")

                } else {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()

                    // 로그아웃 성공 시, SharedPreferences의 isLoggedIn 값을 false로 변경
                    val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        apply()
                    }

                    // 로그인 창으로 이동
                    val intent = Intent(this, StartLoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
        }

        // 회원탈퇴 버튼 클릭 리스너 설정
        binding.activityUserlogoutBt.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(this, "회원탈퇴 실패: $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "회원탈퇴 성공", Toast.LENGTH_SHORT).show()

                    // 회원탈퇴 성공 시, SharedPreferences의 isLoggedIn 값을 false로 변경
                    val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isLoggedIn", false)
                        apply()
                    }

                    // MainActivity로 이동
                    val intent = Intent(this, StartLoginActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
        }
    }
}
