package com.example.spoteam_android.presentation.login.checklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterInformation : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding
    private val viewModel: CheckListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mode = intent.getStringExtra("mode") ?: "START"

        if (mode == "FINAL") {
            val themes = intent.getStringArrayListExtra("selectedThemes") ?: listOf()
            val purposes = intent.getIntegerArrayListExtra("selectedPurposes") ?: listOf()
            val regions = intent.getStringArrayListExtra("selectedRegions") ?: listOf()

            binding.registerMsgTv.text = "체크리스트 등록 중.."
            observeSubmitResult(themes, purposes, regions)
        }

        setupProgressBar(mode)
    }

    private fun observeSubmitResult(
        themes: List<String>,
        purposes: List<Int>,
        regions: List<String>
    ) {
        viewModel.submitPreferences(themes, purposes, regions).observe(this) { result ->
            result.onSuccess {
                Log.d("RegisterInformation", "모든 항목 등록 성공")
            }.onFailure { e ->
                Log.e("RegisterInformation", "등록 실패", e)
                Toast.makeText(this, "등록 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupProgressBar(mode: String) {
        lifecycleScope.launch {
            val totalSteps = 5
            for (i in 1..totalSteps) {
                delay(500)
                updateProgressBar(i * 20)
            }

            if (mode == "FINAL") {
                binding.registerMsgTv.text = "내 정보 등록 완료!"
                delay(500)
                navigateToMainScreen()
            } else if (mode == "START") {
                navigateToCheckList()
            }
        }
    }

    private fun updateProgressBar(progress: Int) {
        binding.activityRegisterProgressbar.progress = progress
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun navigateToCheckList() {
        startActivity(Intent(this, CheckListCategoryActivity::class.java))
        finish()
    }
}
