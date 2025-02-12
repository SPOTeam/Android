package com.example.spoteam_android.login

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsetsAnimation.Callback
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spoteam_android.EmailVerifyResponse
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityEmailVerificationBinding
import retrofit2.Call
import retrofit2.Response

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    private var countDownTimer: CountDownTimer? = null
    private val timerDuration = 1 * 60 * 1000L // 5분 현재는 1분

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEmailEditTextListener()
        setupVerifyButtonListener()
        setupResendButtonListener()
    }

    private fun setupEmailEditTextListener() {
        binding.activityNormalLoginEmailTextInputEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.activityEmailVerificationLoginVerifyBt.isEnabled = !s.isNullOrEmpty()
                binding.activityEmailVerificationLoginVerifyBt.setTextColor(getColor(R.color.b500))
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupVerifyButtonListener() {
        binding.activityEmailVerificationLoginVerifyBt.setOnClickListener {
            val email = binding.activityNormalLoginEmailTextInputEt.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVerificationCode(email)
            startTimer()

            binding.activityEmailVerficationNumberTv.visibility = View.VISIBLE
            binding.activityNormalLoginEmailNumberInputEt.visibility = View.VISIBLE
            binding.activityNormalLoginEmailErrorTv.visibility = View.GONE
            binding.activityEmailVerficationProblemTv.visibility = View.VISIBLE
            binding.activityEmailVerficationRetransmitTv.visibility = View.VISIBLE
        }
    }

    private fun setupResendButtonListener() {
        binding.activityEmailVerficationRetransmitTv.setOnClickListener {
            val email = binding.activityNormalLoginEmailTextInputEt.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendVerificationCode(email)
            startTimer()
        }
    }

    private fun sendVerificationCode(email: String) {
        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val call = api.getVerifyCode(email)
        call.enqueue(object: retrofit2.Callback<EmailVerifyResponse>{
            override fun onResponse(
                call: Call<EmailVerifyResponse>,
                response: Response<EmailVerifyResponse>
            ) {
                if (response.isSuccessful){
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Toast.makeText(this@EmailVerificationActivity, "인증번호가 $email 로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@EmailVerificationActivity, "인증번호 전송 실패: ${result?.message}", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(this@EmailVerificationActivity, "서버 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<EmailVerifyResponse>, t: Throwable) {
                Toast.makeText(this@EmailVerificationActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun startTimer() {
        countDownTimer?.cancel()

        // 버튼 비활성화 및 타이머 시작
        binding.activityNormalLoginEmailTextInputEt.background = ContextCompat.getDrawable(this, R.drawable.email_input)
        binding.activityEmailVerificationLoginVerifyBt.isEnabled = false
        binding.activityEmailVerificationLoginVerifyBt.background = ContextCompat.getDrawable(this, R.drawable.email_timer)
        binding.activityEmailVerificationLoginVerifyBt.setTextColor(getColor(R.color.white))

        countDownTimer = object : CountDownTimer(timerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                binding.activityEmailVerificationLoginVerifyBt.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                // 타이머 종료 후 버튼 복원
                binding.activityNormalLoginEmailTextInputEt.background = ContextCompat.getDrawable(applicationContext, R.drawable.email_timer_finished_input)
                binding.activityEmailVerificationLoginVerifyBt.text = "00:00"
                binding.activityEmailVerificationLoginVerifyBt.isEnabled = false
                binding.activityEmailVerificationLoginVerifyBt.background = ContextCompat.getDrawable(applicationContext, R.drawable.email_timer_finished)
                binding.activityEmailVerificationLoginVerifyBt.setTextColor(getColor(R.color.white))
            }
        }
        countDownTimer?.start()
    }
}
