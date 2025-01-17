package com.example.spoteam_android.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spoteam_android.EmailResponse
import com.example.spoteam_android.IdResponse
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.UserInfo
import com.example.spoteam_android.databinding.ActivityNormalLoginBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private fun setupFocusListeners() {
        binding.activityNormalLoginIdTextInputEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val loginId = binding.activityNormalLoginIdTextInputEt.text.toString()
                if (loginId.isNotBlank()) {
                    checkIdDuplicate(loginId)
                }
            }
        }

        binding.activityNormalLoginEmailTextInputEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = binding.activityNormalLoginEmailTextInputEt.text.toString()
                if (email.isNotBlank()) {
                    if (isEmailFormatValid(email)) { // 정규식 검증
                        checkEmailDuplicate(email) // 정규식 통과 시 API 호출
                    } else {
                        // 정규식 검증 실패 시 메시지 표시
                        updateValidationUI(
                            isValid = false,
                            binding.activityNormalLoginEmailTextInputEt,
                            binding.activityNormalLoginEmailErrorTv,
                            successMessage = "",
                            errorMessage = "이메일 형식이 올바르지 않습니다."
                        )
                    }
                }
            }
        }

        binding.activityNormalLoginPasswordTextInputEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = binding.activityNormalLoginPasswordTextInputEt.text.toString()
                validatePassword(password)
            }
        }

        binding.activityNormalLoginPasswordCheckTextInputEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val password = binding.activityNormalLoginPasswordTextInputEt.text.toString()
                val confirmPassword = binding.activityNormalLoginPasswordCheckTextInputEt.text.toString()
                validatePasswordCheck(password, confirmPassword)
            }
        }
    }

    private fun isEmailFormatValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|net|kr)$".toRegex()
        return emailPattern.matches(email)
    }

    //ID APi
    private fun checkIdDuplicate(loginId: String) {
        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val call = api.checkID(loginId)

        call.enqueue(object : Callback<IdResponse> {
            override fun onResponse(call: Call<IdResponse>, response: Response<IdResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    if (result != null) {
                        val isAvailable = result.available
                        inputStates["id"] = isAvailable
                        updateValidationUI(
                            isAvailable,
                            binding.activityNormalLoginIdTextInputEt,
                            binding.activityNormalLoginIdErrorTv,
                            "사용 가능한 아이디입니다.",
                            "사용 불가능한 아이디입니다."
                        )
                    } else {
                        Log.e("NormalLoginActivity", "결과가 null입니다.")
                    }
                } else {
                    Log.e("NormalLoginActivity", "아이디 중복 확인 실패: ${response.code()}")
                    Toast.makeText(this@NormalLoginActivity, "아이디 확인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
                updateButtonState()
            }

            override fun onFailure(call: Call<IdResponse>, t: Throwable) {
                Log.e("NormalLoginActivity", "아이디 중복 확인 요청 실패", t)
            }
        })
    }

    //Email Api
    private fun checkEmailDuplicate(email: String) {
        val api = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val call = api.checkEmail(email)

        call.enqueue(object : Callback<EmailResponse> {
            override fun onResponse(call: Call<EmailResponse>, response: Response<EmailResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.result
                    if (result != null) {
                        val isAvailable = result.available
                        val errorMessage = when (result.reason) {
                            "NOT_MATCH_INPUT_CONDITION" -> "사용 불가능한 이메일입니다."
                            "EMAIL_ALREADY_EXISTS" -> "이미 가입된 이메일입니다."
                            else -> "사용 불가능한 이메일입니다."
                        }
                        updateValidationUI(
                            isAvailable,
                            binding.activityNormalLoginEmailTextInputEt,
                            binding.activityNormalLoginEmailErrorTv,
                            " ",
                            errorMessage
                        )
                    } else {
                        Log.e("NormalLoginActivity", "결과가 null입니다.")
                    }
                } else {
                    Log.e("NormalLoginActivity", "이메일 중복 확인 실패: ${response.code()}")
                    Toast.makeText(this@NormalLoginActivity, "이메일 확인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
                updateButtonState()
            }

            override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                Log.e("NormalLoginActivity", "이메일 중복 확인 요청 실패", t)
            }
        })
    }



    private fun setupTextWatchers() {
        binding.activityNormalLoginNameTextInputEt.addTextChangedListener(createTextWatcher("name", binding.activityNormalLoginNameTextInputEt))
        binding.activityNormalLoginBirthTextInputEt.addTextChangedListener(createTextWatcher("birth", binding.activityNormalLoginBirthTextInputEt))
        binding.activityNormalLoginBirthBehindTextInputEt.addTextChangedListener(createTextWatcher("birthSuffix", binding.activityNormalLoginBirthBehindTextInputEt))
        binding.activityNormalLoginEmailTextInputEt.addTextChangedListener(createTextWatcher("email", binding.activityNormalLoginEmailTextInputEt))
        binding.activityNormalLoginPasswordTextInputEt.addTextChangedListener(createTextWatcher("password", binding.activityNormalLoginPasswordTextInputEt))
        binding.activityNormalLoginPasswordCheckTextInputEt.addTextChangedListener(createTextWatcher("passwordCheck", binding.activityNormalLoginPasswordCheckTextInputEt))
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
        val minLength = 6
        val pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex()
        val isValid = input.length >= minLength && pattern.matches(input)
        inputStates["id"] = isValid
        updateValidationUI(isValid, binding.activityNormalLoginIdTextInputEt, binding.activityNormalLoginIdErrorTv, "사용 가능한 아이디입니다.", "사용 불가능한 아이디입니다.")
        updateButtonState()
    }

    private fun validateEmail(input: String) {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|net|kr)$".toRegex()
        val isValid = emailPattern.matches(input)
        inputStates["email"] = isValid
        updateValidationUI(isValid, binding.activityNormalLoginEmailTextInputEt, binding.activityNormalLoginEmailErrorTv, "", "사용 불가능한 이메일입니다.")
        updateButtonState()
    }

    private fun validatePassword(input: String) {
        val minLength = 10
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{10,}$".toRegex()
        val isValid = input.length >= minLength && passwordPattern.matches(input)
        inputStates["password"] = isValid

        updateValidationUI(
            isValid,
            binding.activityNormalLoginPasswordTextInputEt,
            binding.activityNormalLoginPasswordErrorTv, // 올바른 Error TextView를 지정
            successMessage = "",
            errorMessage = "사용 불가능한 비밀번호입니다."
        )
        updateButtonState()
    }


    private fun validatePasswordCheck(password: String, confirmPassword: String) {
        val isMatch = password == confirmPassword
        inputStates["passwordCheck"] = isMatch

        updateValidationUI(
            isMatch,
            binding.activityNormalLoginPasswordCheckTextInputEt,
            binding.activityNormalLoginPasswordErrorTv, // 올바른 Error TextView를 지정
            successMessage = "",
            errorMessage = "비밀번호가 일치하지 않습니다."
        )
        updateButtonState()
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

    // 입력 필드 초기화
    private fun clearInputFields() {
        binding.activityNormalLoginNameTextInputEt.text.clear()
        binding.activityNormalLoginBirthTextInputEt.text.clear()
        binding.activityNormalLoginBirthBehindTextInputEt.text.clear()
        binding.activityNormalLoginEmailTextInputEt.text.clear()
        binding.activityNormalLoginIdTextInputEt.text.clear()
        binding.activityNormalLoginPasswordTextInputEt.text.clear()
        binding.activityNormalLoginPasswordCheckTextInputEt.text.clear()

        // 모든 입력 상태 초기화
        inputStates.forEach { key, _ ->
            inputStates[key] = false
        }
        updateButtonState()
    }
}
