package com.example.spoteam_android.login

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
import com.example.spoteam_android.NickNameRequest
import com.example.spoteam_android.NickNameResponse
import com.example.spoteam_android.NicknameCheckResponse
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityNicNameBinding
import com.example.spoteam_android.databinding.DialogAgreementBinding
import com.example.spoteam_android.databinding.DialogIdentificationAgreementBinding
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityNicknameLoginTb.setOnClickListener {
            startActivity(Intent(this, StartLoginActivity::class.java))
        }
        binding.activityNickNameNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nickname = s.toString().trim()
                checkNicknameDuplicate(nickname)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


        binding.activityStartloginCheckBox1.isClickable = false
        binding.activityStartloginCheckBox1.isFocusable = false
        binding.activityStartloginCheckBox2.isClickable = false
        binding.activityStartloginCheckBox2.isFocusable = false

        binding.activityStartloginCheckBox1Ll.setOnClickListener {
            showAgreementDialog()
        }
        binding.activityStartloginCheckBox2Ll.setOnClickListener {
            showIdentifyAgreementDialog()
        }

        binding.activityStartloginLoginwithspotNextBt.setOnClickListener {
            val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
            val personalInfo = binding.activityStartloginCheckBox1.isChecked
            val idInfo = binding.activityStartloginCheckBox2.isChecked

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendNicknameToServer(nickname, personalInfo, idInfo)
        }


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

    private fun sendNicknameToServer(nickname: String, personalInfo: Boolean, idInfo: Boolean) {
        val nicknameRequest = NickNameRequest(
            nickname = nickname,
            personalInfo = personalInfo,
            idInfo = idInfo
        )
        val token = TokenManager(this).getAccessToken()
        Log.d("토큰 체크", "token=$token")
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "로그인이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        RetrofitInstance.setAuthToken(token)
        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        api.updateNickName(nicknameRequest)
            .enqueue(object : Callback<NickNameResponse> {
                override fun onResponse(call: Call<NickNameResponse>, response: Response<NickNameResponse>) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val intent = Intent(this@NicNameActivity, RegisterInformation::class.java)
                        intent.putExtra("mode", "START")
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        Log.e("서버 응답", "code=${response.code()}, error=$errorMsg")

                        if (errorMsg?.contains("만료된 JWT token") == true) {
                            val tokenManager = TokenManager(this@NicNameActivity)
                            tokenManager.clearTokens()
                            Toast.makeText(this@NicNameActivity, "로그인이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@NicNameActivity, StartLoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@NicNameActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<NickNameResponse>, t: Throwable) {
                    Toast.makeText(this@NicNameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("닉네임 API 실패", t.toString())
                }
            })
    }
    private fun checkNicknameDuplicate(nickname: String) {
        if (nickname.isBlank()) {
            binding.activityNickNameValidMessageTv.apply {
                text = getString(R.string.nick_name) // 원래 문구 복원
                setTextColor(ContextCompat.getColor(this@NicNameActivity, R.color.g400))
                visibility = View.VISIBLE
            }
            binding.activityNickNameNicknameEt.setCompoundDrawables(null, null, null, null)
            binding.activityNickNameNicknameEt.background =
                ContextCompat.getDrawable(this, R.drawable.normal_login_edittext_corner_rectangle)
            binding.activityStartloginLoginwithspotNextBt.isEnabled = false
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
                    call: Call<NicknameCheckResponse>,
                    response: Response<NicknameCheckResponse>
                ) {
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
                    Log.e("닉네임 중복 확인 실패", t.toString())
                }
            })
    }
    private fun updateValidationUI(
        isValid: Boolean,
        editText: EditText,
        errorTextView: TextView,
        successMessage: String,
        errorMessage: String
    ) {
        val icon = if (isValid) R.drawable.aftercheck else R.drawable.after_alert
        val background = if (isValid) R.drawable.normal_login_edittext_corner_rectangle else R.drawable.error_edittext_background


        // 아이콘 설정
        ContextCompat.getDrawable(this, icon)?.let { drawable ->
            val iconDrawable = ContextCompat.getDrawable(this, icon)
            val insetDrawable = InsetDrawable(iconDrawable, 0, 0, 30, 0)
            val iconSize = (binding.activityNickNameNicknameEt.textSize * 2).toInt()
            insetDrawable.setBounds(0, 0, iconSize, iconSize)
            binding.activityNickNameNicknameEt.setCompoundDrawables(null, null, insetDrawable, null)

        }

        // 텍스트 메시지 설정
        errorTextView.apply {
            visibility = View.VISIBLE
            text = if (isValid) successMessage else errorMessage
            setTextColor(ContextCompat.getColor(context, if (isValid) R.color.b500 else R.color.r500))
        }

        // EditText 배경 변경
        editText.background = ContextCompat.getDrawable(this, background)

        // 다음 버튼 활성화 여부 갱신
        val allAgreed = binding.activityStartloginCheckBox1.isChecked && binding.activityStartloginCheckBox2.isChecked
        binding.activityStartloginLoginwithspotNextBt.isEnabled = isValid && allAgreed
    }

    private val nicknameRegex = Regex("^[가-힣a-zA-Z0-9_/]{1,8}$")
    private fun isNicknameValidFormat(nickname: String): Boolean {
        return nicknameRegex.matches(nickname)
    }






}
