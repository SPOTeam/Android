package com.example.spoteam_android.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        "password" to false,
        "passwordCheck" to false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clearSharedPreferences()
        clearInputFields()
        setupTextWatchers()

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

        binding.activityNormalLoginPasswordInputEt.addTextChangedListener(createTextWatcher("password", binding.activityNormalLoginPasswordInputEt))
        binding.activityNormalLoginPasswordCheckInputEt.addTextChangedListener(createTextWatcher("passwordCheck", binding.activityNormalLoginPasswordCheckInputEt))
    }

    private val showPasswordFieldsWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val name = binding.activityNormalLoginNameInputEt.text.toString().trim()
            val birth = binding.activityNormalLoginBirthTextInputEt.text.toString().trim()
            val birthSuffix = binding.activityNormalLoginBirthBehindTextInputEt.text.toString().trim()

            // 이름과 생년월일 입력 완료 시 비밀번호 필드 표시
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
        val isPasswordValid = inputStates["password"] == true
        val isPasswordCheckValid = inputStates["passwordCheck"] == true

        val isPasswordFieldsVisible = binding.activityNormalLoginPasswordInputEt.visibility == View.VISIBLE

        binding.activityStartloginLoginwithspotNextBt.isEnabled =
            (isNameValid && isBirthValid && isBirthSuffixValid) && (!isPasswordFieldsVisible || (isPasswordValid && isPasswordCheckValid))
    }

    private fun saveAllInputsToSharedPrefs() {
        val userInfo = UserInfo(
            name = binding.activityNormalLoginNameInputEt.text.toString(),
            nickname = "TEMP_NICKNAME",
            frontRID = binding.activityNormalLoginBirthTextInputEt.text.toString(),
            backRID = binding.activityNormalLoginBirthBehindTextInputEt.text.toString(),
            email = "", // 이메일은 전 화면에서 이미 사용
            loginId = "", // 이메일이 아이디 역할을 함
            password = binding.activityNormalLoginPasswordInputEt.text.toString(),
            pwCheck = binding.activityNormalLoginPasswordCheckInputEt.text.toString(),
            personalInfo = false,
            idInfo = false
        )
        saveUserInfoToSharedPreferences(userInfo)
    }

    private fun saveUserInfoToSharedPreferences(userInfo: UserInfo) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val userInfoJson = Gson().toJson(userInfo)
        editor.putString("USER_INFO", userInfoJson)
        editor.apply()
    }

    private fun logUserInfo() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userInfoJson = sharedPref.getString("USER_INFO", "No Data Found")
        Log.d("NormalLoginActivity", "Saved User Info: $userInfoJson")
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

        inputStates.forEach { key, _ -> inputStates[key] = false }
        updateButtonState()
    }
}
