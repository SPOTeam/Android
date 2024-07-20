package com.example.spoteam_android.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding

class RegisterInformation : AppCompatActivity() {
    lateinit var binding: ActivityRegisterInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}