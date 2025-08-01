package com.umcspot.android.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.umcspot.android.EmailVerifyResponse
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.ValidateEmailResponse
import com.umcspot.android.databinding.ActivityEmailVerificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerificationBinding
    private var countDownTimer: CountDownTimer? = null
    private val timerDuration = 1 * 60 * 1000L // 1분 타이머

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatchers()
        setupVerifyButtonListener()
        setupResendButtonListener()
        setupNextButtonListener()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNextButtonState()
                updateVerifyButtonState()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.activityNormalLoginEmailTextInputEt.addTextChangedListener(textWatcher)
        binding.activityNormalLoginEmailNumberInputEt.addTextChangedListener(textWatcher)
    }

    private fun updateVerifyButtonState() {
        val email = binding.activityNormalLoginEmailTextInputEt.text.toString().trim()

        if (email.isNotEmpty()) {
            binding.activityEmailVerificationLoginVerifyBt.isEnabled = true
            binding.activityEmailVerificationLoginVerifyBt.setTextColor(ContextCompat.getColor(this, R.color.b500))
        } else {
            binding.activityEmailVerificationLoginVerifyBt.isEnabled = false
            binding.activityEmailVerificationLoginVerifyBt.setTextColor(ContextCompat.getColor(this, R.color.gray))
        }
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

            binding.activityNormalLoginEmailNumberInputEt.isEnabled = true
        }
    }

    private fun setupNextButtonListener() {
        binding.activityEmailVerficationEmailNextBt.setOnClickListener {
            validateEmailCode()
        }
    }

    private fun sendVerificationCode(email: String) {
        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val call = api.getVerifyCode(email) // **반환 타입이 EmailVerifyResponse인지 확인**
        call.enqueue(object : Callback<EmailVerifyResponse> {
            override fun onResponse(call: Call<EmailVerifyResponse>, response: Response<EmailVerifyResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.isSuccess) {
                        Toast.makeText(this@EmailVerificationActivity, "인증번호가 $email 로 전송되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EmailVerificationActivity, "인증번호 전송 실패: ${result?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@EmailVerificationActivity, "서버 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EmailVerifyResponse>, t: Throwable) {
                Toast.makeText(this@EmailVerificationActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateEmailCode() {
        val email = binding.activityNormalLoginEmailTextInputEt.text.toString().trim()
        val verificationCode = binding.activityNormalLoginEmailNumberInputEt.text.toString().trim()

        if (email.isEmpty() || verificationCode.isEmpty()) {
            Toast.makeText(this, "이메일과 인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val call = api.validateEmailCode(verificationCode, email)

        call.enqueue(object : Callback<ValidateEmailResponse> {
            override fun onResponse(call: Call<ValidateEmailResponse>, response: Response<ValidateEmailResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val isSuccess = result?.isSuccess == true

                    updateValidationUI(
                        isValid = isSuccess,
                        editText = binding.activityNormalLoginEmailNumberInputEt,
                        errorTextView = binding.activityNormalLoginEmailErrorTv,
                        successMessage = "인증이 완료되었습니다.",
                        errorMessage = "인증번호가 일치하지 않습니다."
                    )

                    if (isSuccess) {
                        binding.activityNormalLoginEmailNumberInputEt.setBackgroundResource(R.drawable.button_enabled)
                        moveToNextScreen()
                    } else {
                        binding.activityNormalLoginEmailNumberInputEt.setBackgroundResource(R.drawable.error_background)
                        binding.activityEmailVerficationEmailNextBt.isEnabled = false
                    }
                } else {
                    Toast.makeText(this@EmailVerificationActivity, "서버 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ValidateEmailResponse>, t: Throwable) {
                Toast.makeText(this@EmailVerificationActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveToNextScreen() {
        val intent = Intent(this, NormalLoginActivity::class.java) // 다음 화면 액티비티 변경
        startActivity(intent)
        finish()
    }

    private fun updateNextButtonState() {
        val email = binding.activityNormalLoginEmailTextInputEt.text.toString().trim()
        val verificationCode = binding.activityNormalLoginEmailNumberInputEt.text.toString().trim()
        binding.activityEmailVerficationEmailNextBt.isEnabled = email.isNotEmpty() && verificationCode.isNotEmpty()
    }

    private fun updateValidationUI(isValid: Boolean, editText: EditText, errorTextView: TextView, successMessage: String, errorMessage: String) {
        val drawable = if (isValid) ContextCompat.getDrawable(this, R.drawable.aftercheck) else ContextCompat.getDrawable(this, R.drawable.after_alert)
        drawable?.let {
            val textSizeInPx = editText.textSize.toInt()
            val iconSize = (textSizeInPx * 1.5).toInt()
            it.setBounds(0, 0, iconSize, iconSize)
            editText.setCompoundDrawables(null, null, it, null)
        }
        errorTextView.apply {
            visibility = View.VISIBLE
            text = if (isValid) successMessage else errorMessage
            setTextColor(ContextCompat.getColor(context, if (isValid) R.color.selector_blue else R.color.selector_red))
        }
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
