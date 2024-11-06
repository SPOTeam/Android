package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityNicNameBinding

class NicNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNicNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNicNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityNickNameTb.setOnClickListener {
            val intent = Intent(this, NormalLoginActivity::class.java)
            startActivity(intent)
        }
    }
}