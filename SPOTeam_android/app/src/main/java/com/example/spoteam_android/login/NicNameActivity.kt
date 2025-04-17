package com.example.spoteam_android.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.NickNameRequest
import com.example.spoteam_android.NickNameResponse
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.ActivityNicNameBinding
import com.example.spoteam_android.databinding.DialogAgreementBinding
import com.example.spoteam_android.databinding.DialogIdentificationAgreementBinding
import com.navercorp.nid.oauth.NidOAuthPreferencesManager.accessToken
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

        // Toolbar 클릭 시 이전 화면 이동
        binding.activityNicknameLoginTb.setOnClickListener {
            startActivity(Intent(this, NormalLoginActivity::class.java))
        }

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

        binding.activityNickNameNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = validateNextButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        validateNextButtonState()
    }

    private fun validateNextButtonState() {
        val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
        val isAllChecked = binding.activityStartloginCheckBox1.isChecked && binding.activityStartloginCheckBox2.isChecked
        binding.activityStartloginLoginwithspotNextBt.isEnabled = nickname.isNotEmpty() && isAllChecked
    }

    private fun showAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                resources.getDimensionPixelSize(R.dimen.dialog_fixed_height)
            )
        }
        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox1.isChecked = true
            binding.activityStartloginCheckBox1Ll.setBackgroundResource(R.drawable.agreement_background)
            validateNextButtonState()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showIdentifyAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = DialogIdentificationAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                resources.getDimensionPixelSize(R.dimen.dialog_fixed_height)
            )
        }
        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox2.isChecked = true
            binding.activityStartloginCheckBox2Ll.setBackgroundResource(R.drawable.agreement_background)
            validateNextButtonState()
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

        val api = RetrofitInstance.retrofit.newBuilder()
            .client(
                RetrofitInstance.okHttpClient.newBuilder()
                    .addInterceptor(logging)
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                        chain.proceed(request)
                    }.build()
            )
            .build()
            .create(LoginApiService::class.java)

        api.updateNickName(nicknameRequest)
            .enqueue(object : Callback<NickNameResponse> {
                override fun onResponse(call: Call<NickNameResponse>, response: Response<NickNameResponse>) {
                    Log.d("닉네임 API", "nickname=$nickname, personalInfo=$personalInfo, idInfo=$idInfo")
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val intent = Intent(this@NicNameActivity, RegisterInformation::class.java)
                        intent.putExtra("mode", "PREFERENCE")
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@NicNameActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        val errorMsg = response.errorBody()?.string()
                        Log.e("서버 응답", "code=${response.code()}, error=$errorMsg")
                    }
                }

                override fun onFailure(call: Call<NickNameResponse>, t: Throwable) {
                    Toast.makeText(this@NicNameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("닉네임 API 실패", t.toString())
                }
            })
    }


}
