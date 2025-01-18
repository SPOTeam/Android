package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityNicNameBinding

class NicNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 글자 수 업데이트를 위한 TextWatcher 설정
        setupNicknameTextWatcher()

        // Toolbar 클릭 시 이전 화면으로 이동
        binding.activityNickNameTb.setOnClickListener {
            val intent = Intent(this, NormalLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupNicknameTextWatcher() {
        binding.activityNickNameNicknameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val inputLength = s?.length ?: 0
                binding.activityNickNameValidMessageMinTv.text = inputLength.toString()

            }
        })
    }
}
