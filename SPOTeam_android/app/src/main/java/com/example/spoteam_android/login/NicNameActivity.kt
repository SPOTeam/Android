package com.example.spoteam_android.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityNicNameBinding
import com.example.spoteam_android.databinding.DialogAgreementBinding
import com.example.spoteam_android.databinding.DialogIdentificationAgreementBinding

class NicNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 클릭 시 이전 화면으로 이동
        binding.activityNicknameLoginTb.setOnClickListener {
            val intent = Intent(this, NormalLoginActivity::class.java)
            startActivity(intent)
        }

        // ✅ 체크박스 직접 클릭 방지 (다이얼로그를 통해서만 체크 가능)
        binding.activityStartloginCheckBox1.isClickable = false
        binding.activityStartloginCheckBox1.isFocusable = false

        // ✅ 약관 동의 레이아웃 클릭 시 다이얼로그 표시
        binding.activityStartloginCheckBox1Ll.setOnClickListener {
            showAgreementDialog()
        }
        binding.activityStartloginCheckBox2Ll.setOnClickListener {
            showIdentifyAgreementDialog()
        }
    }

    private fun showAgreementDialog() {
        // ✅ 1. 다이얼로그 생성 후 즉시 requestFeature() 호출
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        // ✅ 2. 다이얼로그를 먼저 띄운 후 크기 및 배경 설정
        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                resources.getDimensionPixelSize(R.dimen.dialog_fixed_height)
            )
        }


        // ✅ 3. "동의" 버튼 클릭 시 체크박스 활성화 및 다이얼로그 닫기
        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox1.isChecked = true
            binding.activityStartloginCheckBox1Ll.setBackgroundResource(R.drawable.agreement_background)
            dialog.dismiss()
        }
    }

    private fun showIdentifyAgreementDialog() {
        // ✅ 1. 다이얼로그 생성 후 즉시 requestFeature() 호출
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogIdentificationAgreementBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)

        // ✅ 2. 다이얼로그를 먼저 띄운 후 크기 및 배경 설정
        dialog.show()

        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                resources.getDimensionPixelSize(R.dimen.dialog_fixed_height)
            )
        }


        // ✅ 3. "동의" 버튼 클릭 시 체크박스 활성화 및 다이얼로그 닫기
        dialogBinding.dialogAgreementAcceptBtn.setOnClickListener {
            binding.activityStartloginCheckBox2.isChecked = true
            binding.activityStartloginCheckBox2Ll.setBackgroundResource(R.drawable.agreement_background)
            dialog.dismiss()
        }
    }


}
