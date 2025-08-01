package com.spot.android.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.spot.android.NickNameRequest
import com.spot.android.NickNameResponse
import com.spot.android.NicknameCheckResponse
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.ActivityNicNameBinding
import com.spot.android.databinding.DialogAgreementBinding
import com.spot.android.databinding.DialogIdentificationAgreementBinding
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicNameBinding
    private var lastBackPressedTime = 0L
    private val nicknameRegex = Regex("^[가-힣a-zA-Z0-9_/]{1,8}$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        binding.activityNicknameLoginTb.setOnClickListener {
            startActivity(Intent(this, StartLoginActivity::class.java))
        }
        binding.activityNickNameNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkNicknameDuplicate(s.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        initCheckBoxesAndDialogs()
        initNextButton()
    }

    private fun initCheckBoxesAndDialogs() {
        binding.activityStartloginCheckBox1Ll.setOnClickListener { showAgreementDialog() }
        binding.activityStartloginCheckBox2Ll.setOnClickListener { showIdentifyAgreementDialog() }
    }

    private fun initNextButton() {
        binding.activityStartloginLoginwithspotNextBt.setOnClickListener {
            val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendNicknameToServer(
                nickname,
                personalInfo = binding.activityStartloginCheckBox1.isChecked,
                idInfo = binding.activityStartloginCheckBox2.isChecked
            )
        }
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime <= 2000) finishAffinity() else {
            lastBackPressedTime = currentTime
            Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /** 공통 토큰 만료 처리 */
    private fun handleTokenError() {
        TokenManager(this).clearTokens()
        Toast.makeText(this, "로그인이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, StartLoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun sendNicknameToServer(nickname: String, personalInfo: Boolean, idInfo: Boolean) {
        val token = TokenManager(this).getAccessToken()
        if (token.isNullOrEmpty()) {
            handleTokenError()
            return
        }

        val request = NickNameRequest(nickname, personalInfo, idInfo)
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        RetrofitInstance.setAuthToken(token)
        RetrofitInstance.retrofit.create(LoginApiService::class.java)
            .updateNickName(request)
            .enqueue(object : Callback<NickNameResponse> {
                override fun onResponse(
                    call: Call<NickNameResponse>,
                    response: Response<NickNameResponse>
                ) {
                    Log.d(
                        "NicNameActivity",
                        "닉네임 등록 응답: ${response.code()} body: ${response.body()}"
                    )
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        navigateToRegisterInfo()
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        Log.e("NicNameActivity", "서버 오류: $errorMsg")
                        if (errorMsg?.contains("만료된 JWT token") == true) {
                            handleTokenError()
                        } else {
                            Toast.makeText(
                                this@NicNameActivity,
                                "서버 오류: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<NickNameResponse>, t: Throwable) {
                    Log.e("NicNameActivity", "닉네임 API 실패: ${t.message}", t)
                    Toast.makeText(
                        this@NicNameActivity,
                        "네트워크 오류: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToRegisterInfo() {
        val intent = Intent(this, RegisterInformation::class.java).apply {
            putExtra("mode", "START")
        }
        startActivity(intent)
        finish()
    }

    private fun checkNicknameDuplicate(nickname: String) {
        if (nickname.isBlank()) {
            resetNicknameFieldUI()
            return
        }
        if (!isNicknameValidFormat(nickname)) {
            updateValidationUI(
                isValid = false,
                editText = binding.activityNickNameNicknameEt,
                errorTextView = binding.activityNickNameValidMessageTv,
                successMessage = "",
                errorMessage = "사용 가능한 닉네임입니다."
            )
            return
        }

        RetrofitInstance.retrofit.create(LoginApiService::class.java)
            .checkNickname(nickname)
            .enqueue(object : Callback<NicknameCheckResponse> {
                override fun onResponse(
                    call: Call<NicknameCheckResponse>, response: Response<NicknameCheckResponse>
                ) {
                    Log.d(
                        "NicNameActivity",
                        "중복 검사 응답: ${response.code()} body: ${response.body()}"
                    )
                    val isDuplicate = response.body()?.result?.duplicate ?: false
                    if (isDuplicate) {
                        updateValidationUI(
                            isValid = false,
                            editText = binding.activityNickNameNicknameEt,
                            errorTextView = binding.activityNickNameValidMessageTv,
                            successMessage = "",
                            errorMessage = "사용 불가능한 닉네임입니다."
                        )
                    } else {
                        updateValidationUI(
                            isValid = true,
                            editText = binding.activityNickNameNicknameEt,
                            errorTextView = binding.activityNickNameValidMessageTv,
                            successMessage = "사용 가능한 닉네임입니다.",
                            errorMessage = ""
                        )
                    }
                }

                override fun onFailure(call: Call<NicknameCheckResponse>, t: Throwable) {
                    Log.e("NicNameActivity", "닉네임 중복 검사 실패: ${t.message}", t)
                }
            })
    }

    private fun resetNicknameFieldUI() {
        binding.activityNickNameValidMessageTv.apply {
            text = getString(R.string.nick_name)
            setTextColor(ContextCompat.getColor(this@NicNameActivity, R.color.g400))
        }
        binding.activityNickNameNicknameEt.apply {
            setCompoundDrawables(null, null, null, null)
            background = ContextCompat.getDrawable(
                this@NicNameActivity,
                R.drawable.normal_login_edittext_corner_rectangle
            )
        }
        binding.activityStartloginLoginwithspotNextBt.isEnabled = false
    }

    private fun isNicknameValidFormat(nickname: String): Boolean = nicknameRegex.matches(nickname)

    private fun updateValidationUI(
        isValid: Boolean,
        editText: EditText,
        errorTextView: TextView,
        successMessage: String,
        errorMessage: String
    ) {
        val icon = if (isValid) R.drawable.aftercheck else R.drawable.after_alert
        val background =
            if (isValid) R.drawable.normal_login_edittext_corner_rectangle else R.drawable.error_edittext_background

        ContextCompat.getDrawable(this, icon)?.let { iconDrawable ->
            val insetDrawable = InsetDrawable(iconDrawable, 0, 0, 30, 0).apply {
                val iconSize = (editText.textSize * 2).toInt()
                setBounds(0, 0, iconSize, iconSize)
            }
            editText.setCompoundDrawables(null, null, insetDrawable, null)
        }

        errorTextView.apply {
            visibility = View.VISIBLE
            text = if (isValid) successMessage else errorMessage
            setTextColor(
                ContextCompat.getColor(
                    this@NicNameActivity,
                    if (isValid) R.color.b500 else R.color.r500
                )
            )
        }

        editText.background = ContextCompat.getDrawable(this, background)
        val allAgreed =
            binding.activityStartloginCheckBox1.isChecked && binding.activityStartloginCheckBox2.isChecked
        binding.activityStartloginLoginwithspotNextBt.isEnabled = isValid && allAgreed
    }

    private fun showAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            (325 * resources.displayMetrics.density).toInt(),
            (400 * resources.displayMetrics.density).toInt()
        )


        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox1.isChecked = true
            binding.activityStartloginCheckBox1Ll.setBackgroundResource(R.drawable.agreement_background)
            val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
            checkNicknameDuplicate(nickname)
            dialog.dismiss()
        }
        dialogBinding.dialogCloseBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showIdentifyAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogIdentificationAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(
            (325 * resources.displayMetrics.density).toInt(),
            (400 * resources.displayMetrics.density).toInt()
        )


        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox2.isChecked = true
            binding.activityStartloginCheckBox2Ll.setBackgroundResource(R.drawable.agreement_background)
            val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
            checkNicknameDuplicate(nickname)
            dialog.dismiss()
        }
        dialogBinding.dialogCloseBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
