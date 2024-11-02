package com.example.spoteam_android.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.UserInfo
import com.example.spoteam_android.databinding.ActivityNormalLoginBinding
import com.google.gson.Gson


class NormalLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalLoginBinding
    private val inputStates = mutableMapOf(
        "name" to false,
        "birth" to false,
        "birthSuffix" to false,
        "id" to false,
        "email" to false,
        "password" to false,
        "passwordCheck" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatchers() // 각 입력 필드에 TextWatcher 설정
        binding.activityStartloginLoginwithspotNextBt.setOnClickListener {
            saveAllInputsToSharedPrefs() // 버튼 클릭 시 모든 입력값을 SharedPreferences에 저장
            logUserInfo() // 저장된 정보 로그 출력
        }
    }

    private fun setupTextWatchers() {
        binding.activityNormalLoginNameTextInputEt.addTextChangedListener(createTextWatcher("name", binding.activityNormalLoginNameTextInputEt))
        binding.activityNormalLoginBirthTextInputEt.addTextChangedListener(createTextWatcher("birth", binding.activityNormalLoginBirthTextInputEt))
        binding.activityNormalLoginBirthBehindTextInputEt.addTextChangedListener(createTextWatcher("birthSuffix", binding.activityNormalLoginBirthBehindTextInputEt))
        binding.activityNormalLoginIdTextInputEt.addTextChangedListener(createTextWatcher("id", binding.activityNormalLoginIdTextInputEt) { input ->
            validateId(input)
        })
        binding.activityNormalLoginEmailTextInputEt.addTextChangedListener(createTextWatcher("email", binding.activityNormalLoginEmailTextInputEt) { input ->
            validateEmail(input)
        })
        binding.activityNormalLoginPasswordTextInputEt.addTextChangedListener(createTextWatcher("password", binding.activityNormalLoginPasswordTextInputEt) { input ->
            validatePassword(input)
        })
        binding.activityNormalLoginPasswordCheckTextInputEt.addTextChangedListener(createTextWatcher("passwordCheck", binding.activityNormalLoginPasswordCheckTextInputEt) {
            val password = binding.activityNormalLoginPasswordTextInputEt.text.toString()
            validatePasswordCheck(password, it)
        })
    }

    private fun createTextWatcher(key: String, editText: EditText, afterTextChanged: ((String) -> Unit)? = null) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val input = s?.toString().orEmpty()
            inputStates[key] = input.isNotBlank()
            updateBackground(editText, inputStates[key] ?: false)
            afterTextChanged?.invoke(input)
            updateButtonState()
        }
    }

    private fun updateBackground(editText: EditText, isSelected: Boolean) {
        val background = if (isSelected) R.drawable.edittext_with_text_background else R.drawable.edittext_rounded_corner_rectangle
        editText.setBackgroundResource(background)
    }

    private fun updateButtonState() {
        binding.activityStartloginLoginwithspotNextBt.isEnabled = inputStates.values.all { it }
    }

    private fun validateId(input: String) {
        val existingId = "abc123"
        val minLength = 6
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex()

        val isValid = input.length >= minLength && pattern.matches(input) && input != existingId
        updateValidationUI(isValid, binding.activityNormalLoginIdTextInputEt, binding.activityNormalLoginIdErrorTv, "사용 가능한 아이디입니다.", "사용 불가능한 아이디입니다.")
    }

    private fun validateEmail(input: String) {
        val existingEmail = "fred2379@mju.ac.kr"
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|net|kr)$".toRegex()

        if (input == existingEmail) {
            updateValidationUI(false, binding.activityNormalLoginEmailTextInputEt, binding.activityNormalLoginEmailErrorTv, "", "이미 가입된 이메일입니다.")
        } else {
            val isValid = emailPattern.matches(input)
            updateValidationUI(isValid, binding.activityNormalLoginEmailTextInputEt, binding.activityNormalLoginEmailErrorTv, "사용 가능한 이메일입니다.", "사용 불가능한 이메일입니다.")
        }
    }

    private fun validatePassword(input: String) {
        val minLength = 10
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}$".toRegex()
        val isValid = input.length >= minLength && passwordPattern.matches(input)

        val drawable = if (isValid) ContextCompat.getDrawable(this, R.drawable.beforecheck) else ContextCompat.getDrawable(this, R.drawable.false_check)
        drawable?.let {
            val textSizeInPx = binding.activityNormalLoginPasswordTextInputEt.textSize.toInt()
            val iconSize = (textSizeInPx * 1.5).toInt()
            it.setBounds(0, 0, iconSize, iconSize)
            binding.activityNormalLoginPasswordTextInputEt.setCompoundDrawables(null, null, it, null)
        }
    }

    private fun validatePasswordCheck(password: String, confirmPassword: String) {
        val isMatch = password == confirmPassword
        inputStates["passwordCheck"] = isMatch
        updateButtonState()

        val drawable = if (isMatch) ContextCompat.getDrawable(this, R.drawable.beforecheck) else ContextCompat.getDrawable(this, R.drawable.false_check)
        drawable?.let {
            val textSizeInPx = binding.activityNormalLoginPasswordCheckTextInputEt.textSize.toInt()
            val iconSize = (textSizeInPx * 1.5).toInt()
            it.setBounds(0, 0, iconSize, iconSize)
            binding.activityNormalLoginPasswordCheckTextInputEt.setCompoundDrawables(null, null, it, null)
        }
    }

    private fun updateValidationUI(isValid: Boolean, editText: EditText, errorTextView: TextView, successMessage: String, errorMessage: String) {
        val drawable = if (isValid) ContextCompat.getDrawable(this, R.drawable.beforecheck) else ContextCompat.getDrawable(this, R.drawable.false_check)
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

    private fun saveUserInfoToSharedPreferences(userInfo: UserInfo) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val userInfoJson = Gson().toJson(userInfo)
        editor.putString("USER_INFO", userInfoJson)
        editor.apply()
    }

    private fun saveAllInputsToSharedPrefs() {
        val userInfo = UserInfo(
            name = binding.activityNormalLoginNameTextInputEt.text.toString(),
            nickname = "TEMP_NICKNAME",
            frontRID = binding.activityNormalLoginBirthTextInputEt.text.toString(),
            backRID = binding.activityNormalLoginBirthBehindTextInputEt.text.toString(),
            email = binding.activityNormalLoginEmailTextInputEt.text.toString(),
            loginId = binding.activityNormalLoginIdTextInputEt.text.toString(),
            password = binding.activityNormalLoginPasswordTextInputEt.text.toString(),
            pwCheck = binding.activityNormalLoginPasswordCheckTextInputEt.text.toString(),
            personalInfo = false,
            idInfo = false
        )

        saveUserInfoToSharedPreferences(userInfo)
    }

    private fun logUserInfo() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userInfoJson = sharedPref.getString("USER_INFO", "No Data Found")
        Log.d("NormalLoginActivity", "Saved User Info: $userInfoJson")
    }
}
