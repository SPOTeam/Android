package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterInformation : ComponentActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProgressBar()
    }

    private fun setupProgressBar() {
        lifecycleScope.launch {
            val totalSteps = 5
            for (i in 1..totalSteps) {

                delay(1000)
                updateProgressBar(i * 20)
            }
            navigateToMainScreen()
        }
    }

    private fun updateProgressBar(progress: Int) {
        binding.activityRegisterProgressbar.progress = progress
    }

    private fun navigateToMainScreen() {
        // Start the main activity or perform the necessary navigation
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
