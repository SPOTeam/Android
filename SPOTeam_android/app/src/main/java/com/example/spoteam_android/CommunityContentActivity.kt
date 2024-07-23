package com.example.spoteam_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityCommunityContentBinding

class CommunityContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}