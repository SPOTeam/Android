package com.example.spoteam_android.presentation.login.nickname

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityNickNameBinding
import com.example.spoteam_android.databinding.DialogAgreementBinding
import com.example.spoteam_android.databinding.DialogIdentificationAgreementBinding
import com.example.spoteam_android.presentation.login.checklist.RegisterInformation
import com.example.spoteam_android.presentation.login.StartLoginActivity
import com.example.spoteam_android.presentation.nickname.NicknameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NicknameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNickNameBinding
    private val viewModel: NicknameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNickNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityNicknameLoginTb.setOnClickListener {
            startActivity(Intent(this, StartLoginActivity::class.java))
        }

        binding.activityNickNameNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nickname = s.toString().trim()
                if (nickname.isNotBlank()) viewModel.checkNickname(nickname)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.activityStartloginCheckBox1.isClickable = false
        binding.activityStartloginCheckBox1.isFocusable = false
        binding.activityStartloginCheckBox2.isClickable = false
        binding.activityStartloginCheckBox2.isFocusable = false

        binding.activityStartloginCheckBox1Ll.setOnClickListener { showAgreementDialog() }
        binding.activityStartloginCheckBox2Ll.setOnClickListener { showIdentifyAgreementDialog() }

        binding.activityStartloginLoginwithspotNextBt.setOnClickListener {
            val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
            val personalInfo = binding.activityStartloginCheckBox1.isChecked
            val idInfo = binding.activityStartloginCheckBox2.isChecked

            if (nickname.isEmpty()) {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateNickname(nickname, personalInfo, idInfo)
        }

        observeState()
    }

    private fun observeState() {
        viewModel.nicknameState.observe(this) { state ->
            when (state) {
                NicknameState.Valid -> updateValidationUI(true, "사용 가능한 닉네임입니다.", "")
                NicknameState.Error -> updateValidationUI(false, "", "사용 불가능한 닉네임입니다.")
                else -> resetValidationUI()
            }
        }

        viewModel.nicknameUpdateResult.observe(this) { result ->
            result.onSuccess {
                val nickname = binding.activityNickNameNicknameEt.text.toString().trim()
                viewModel.saveNickname(nickname)

                startActivity(Intent(this, RegisterInformation::class.java).apply {
                    putExtra("mode", "START")
                })
                finish()
            }.onFailure {
                Toast.makeText(this, "오류 발생: ${it.message}", Toast.LENGTH_SHORT).show()
                viewModel.clearAll()
                val intent = Intent(this, StartLoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
    }


    private fun showAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout((325 * resources.displayMetrics.density).toInt(), (400 * resources.displayMetrics.density).toInt())

        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox1.isChecked = true
            binding.activityStartloginCheckBox1Ll.setBackgroundResource(R.drawable.agreement_background)
            updateNextButtonState()
            dialog.dismiss()
        }
        dialogBinding.dialogCloseBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showIdentifyAgreementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogIdentificationAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout((325 * resources.displayMetrics.density).toInt(), (400 * resources.displayMetrics.density).toInt())

        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox2.isChecked = true
            binding.activityStartloginCheckBox2Ll.setBackgroundResource(R.drawable.agreement_background)
            updateNextButtonState()
            dialog.dismiss()
        }
        dialogBinding.dialogCloseBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun updateValidationUI(isValid: Boolean, successMessage: String, errorMessage: String) {
        val icon = if (isValid) R.drawable.aftercheck else R.drawable.after_alert
        val background = if (isValid) R.drawable.normal_login_edittext_corner_rectangle else R.drawable.error_edittext_background

        ContextCompat.getDrawable(this, icon)?.let { drawable ->
            val inset = InsetDrawable(drawable, 0, 0, 30, 0)
            val size = (binding.activityNickNameNicknameEt.textSize * 2).toInt()
            inset.setBounds(0, 0, size, size)
            binding.activityNickNameNicknameEt.setCompoundDrawables(null, null, inset, null)
        }

        binding.activityNickNameValidMessageTv.apply {
            visibility = View.VISIBLE
            text = if (isValid) successMessage else errorMessage
            setTextColor(ContextCompat.getColor(context, if (isValid) R.color.b500 else R.color.r500))
        }

        binding.activityNickNameNicknameEt.background = ContextCompat.getDrawable(this, background)

        updateNextButtonState()
    }


    private fun updateNextButtonState() {
        val isNicknameValid = viewModel.isNicknameValid()
        val allAgreed = binding.activityStartloginCheckBox1.isChecked && binding.activityStartloginCheckBox2.isChecked
        binding.activityStartloginLoginwithspotNextBt.isEnabled = isNicknameValid && allAgreed
    }

    private fun resetValidationUI() {
        binding.activityNickNameValidMessageTv.text = getString(R.string.nick_name)
        binding.activityNickNameValidMessageTv.setTextColor(ContextCompat.getColor(this, R.color.g400))
        binding.activityNickNameValidMessageTv.visibility = View.VISIBLE
        binding.activityNickNameNicknameEt.setCompoundDrawables(null, null, null, null)
        binding.activityNickNameNicknameEt.background = ContextCompat.getDrawable(this, R.drawable.normal_login_edittext_corner_rectangle)
        binding.activityStartloginLoginwithspotNextBt.isEnabled = false
    }
}
