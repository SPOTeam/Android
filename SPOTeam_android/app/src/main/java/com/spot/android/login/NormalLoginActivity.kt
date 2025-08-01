package com.spot.android.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.spot.android.R
import com.spot.android.UserInfo
import com.spot.android.databinding.ActivityNormalLoginBinding
import com.google.gson.Gson

class NormalLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalLoginBinding
    private val inputStates = mutableMapOf(
        "name" to false,
        "birth" to false,
        "birthSuffix" to false,
        "password" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 데이터 초기화
        clearSharedPreferences()
        clearInputFields()
        setupTextWatchers()
        setupFocusListeners()

        binding.activityStartloginLoginwithspotNextBt.setOnClickListener {
            saveAllInputsToSharedPrefs()
            logUserInfo()
            startActivity(Intent(this, NicNameActivity::class.java))
        }
    }

    private fun setupTextWatchers() {
        binding.activityNormalLoginNameInputEt.addTextChangedListener(createTextWatcher("name", binding.activityNormalLoginNameInputEt))
        binding.activityNormalLoginBirthTextInputEt.addTextChangedListener(createTextWatcher("birth", binding.activityNormalLoginBirthTextInputEt))
        binding.activityNormalLoginBirthBehindTextInputEt.addTextChangedListener(createTextWatcher("birthSuffix", binding.activityNormalLoginBirthBehindTextInputEt))

        // 이름 & 생년월일 입력 후 비밀번호 필드 표시
        binding.activityNormalLoginNameInputEt.addTextChangedListener(showPasswordFieldsWatcher)
        binding.activityNormalLoginBirthTextInputEt.addTextChangedListener(showPasswordFieldsWatcher)
        binding.activityNormalLoginBirthBehindTextInputEt.addTextChangedListener(showPasswordFieldsWatcher)

        // ✅ 비밀번호는 먼저 입력하고, 비밀번호 확인 필드가 실시간 감지되도록 함
        binding.activityNormalLoginPasswordInputEt.addTextChangedListener(passwordWatcher)
        binding.activityNormalLoginPasswordCheckInputEt.addTextChangedListener(passwordConfirmWatcher)
    }
    // ✅ 비밀번호 입력 감지 (비밀번호 확인 X)
    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            // 🔥 비밀번호 입력 후에도 버튼 활성화는 안 함 (비밀번호 확인을 기다림)
            updateButtonState()
        }
    }

    // ✅ 비밀번호 확인 필드 실시간 감지 (비밀번호와 비교)
    private val passwordConfirmWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateButtonState() // ✅ 비밀번호 확인이 변경될 때만 버튼 활성화 검토
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun setupFocusListeners() {
        binding.activityNormalLoginPasswordCheckInputEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) validatePassword()
        }
    }

    private val showPasswordFieldsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val name = binding.activityNormalLoginNameInputEt.text.toString().trim()
            val birth = binding.activityNormalLoginBirthTextInputEt.text.toString().trim()
            val birthSuffix = binding.activityNormalLoginBirthBehindTextInputEt.text.toString().trim()

            if (name.isNotEmpty() && birth.length == 6 && birthSuffix.isNotEmpty()) {
                showPasswordFields()
            } else {
                hidePasswordFields()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun showPasswordFields() {
        binding.activityNormalLoginPasswordTv.visibility = View.VISIBLE
        binding.activityNormalLoginPasswordInputEt.visibility = View.VISIBLE
        binding.activityNormalLoginPasswordErrorTv.visibility = View.VISIBLE
        binding.activityNormalLoginPasswordCheckTv.visibility = View.VISIBLE
        binding.activityNormalLoginPasswordCheckInputEt.visibility = View.VISIBLE
    }

    private fun hidePasswordFields() {
        binding.activityNormalLoginPasswordTv.visibility = View.GONE
        binding.activityNormalLoginPasswordInputEt.visibility = View.GONE
        binding.activityNormalLoginPasswordErrorTv.visibility = View.GONE
        binding.activityNormalLoginPasswordCheckTv.visibility = View.GONE
        binding.activityNormalLoginPasswordCheckInputEt.visibility = View.GONE
    }

    private fun createTextWatcher(key: String, editText: EditText) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            val input = s?.toString().orEmpty()
            inputStates[key] = input.isNotBlank()
            updateBackground(editText, inputStates[key] ?: false)
            updateButtonState()
        }
    }

    private fun updateBackground(editText: EditText, isSelected: Boolean) {
        val background = if (isSelected) R.drawable.edittext_with_text_background else R.drawable.edittext_rounded_corner_rectangle
        editText.setBackgroundResource(background)
    }

    private fun updateButtonState() {
        val isNameValid = inputStates["name"] == true
        val isBirthValid = inputStates["birth"] == true
        val isBirthSuffixValid = inputStates["birthSuffix"] == true
        val isPasswordValid = binding.activityNormalLoginPasswordInputEt.text.toString().length >= 10
        val isPasswordMatch = binding.activityNormalLoginPasswordInputEt.text.toString() ==
                binding.activityNormalLoginPasswordCheckInputEt.text.toString()

        // 🔥 비밀번호 확인 필드가 입력 중일 때만 버튼 활성화 검토
        binding.activityStartloginLoginwithspotNextBt.isEnabled =
            isNameValid && isBirthValid && isBirthSuffixValid && isPasswordValid && isPasswordMatch
    }

    private fun validatePassword() {
        val password = binding.activityNormalLoginPasswordInputEt.text.toString().trim()
        val confirmPassword = binding.activityNormalLoginPasswordCheckInputEt.text.toString().trim()

        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}$".toRegex()
        val isPasswordValid = passwordPattern.matches(password)
        val isMatch = password == confirmPassword

        inputStates["password"] = isPasswordValid && isMatch

        updateValidationUI(
            isValid = isPasswordValid,
            editText = binding.activityNormalLoginPasswordInputEt,
            errorTextView = binding.activityNormalLoginPasswordErrorTv,
            successMessage = "사용 가능한 비밀번호입니다.",
            errorMessage = "비밀번호는 영문+숫자+특수문자 포함 10자 이상 입력해야 합니다."
        )

        updateButtonState()
    }

    private fun updateValidationUI(
        isValid: Boolean,
        editText: EditText,
        errorTextView: TextView,
        successMessage: String,
        errorMessage: String
    ) {
        val drawable = if (isValid) ContextCompat.getDrawable(this, R.drawable.aftercheck)
        else ContextCompat.getDrawable(this, R.drawable.after_alert)

        drawable?.let {
            val textSizeInPx = editText.textSize.toInt()
            val iconSize = (textSizeInPx * 1.5).toInt()
            it.setBounds(0, 0, iconSize, iconSize)
            editText.setCompoundDrawables(null, null, it, null)
        }

        editText.setBackgroundResource(if (isValid) R.drawable.edittext_with_text_background else R.drawable.error_background)

        errorTextView.apply {
            visibility = View.VISIBLE
            text = if (isValid) successMessage else errorMessage
            setTextColor(ContextCompat.getColor(context, if (isValid) R.color.selector_blue else R.color.selector_red))
        }
    }

    private fun clearSharedPreferences() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
    }

    private fun clearInputFields() {
        binding.activityNormalLoginNameInputEt.text.clear()
        binding.activityNormalLoginBirthTextInputEt.text.clear()
        binding.activityNormalLoginBirthBehindTextInputEt.text.clear()
        binding.activityNormalLoginPasswordInputEt.text.clear()
        binding.activityNormalLoginPasswordCheckInputEt.text.clear()
    }

    private fun saveAllInputsToSharedPrefs() {
        val userInfo = UserInfo(
            name = "Temp",
            nickname = "",
            frontRID = "",
            backRID = "",
            email = "",
            loginId = "",
            password = "",
            pwCheck = "",
            personalInfo = false, // Boolean 값
            idInfo = false        // Boolean 값
        )

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val userInfoJson = Gson().toJson(userInfo)

        editor.putString("USER_INFO", userInfoJson) // JSON 형태로 저장
        editor.apply()
    }

    private fun logUserInfo() {
        Log.d("NormalLoginActivity", "Saved User Info: ${getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("USER_INFO", "No Data")}")
    }
}
