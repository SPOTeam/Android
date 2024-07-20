package com.example.tempproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tempproject.databinding.ActivityCommunityBinding


class CommunityActivity : AppCompatActivity() {

    lateinit var binding: ActivityCommunityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.community_main_frm, CommunityCategoryFragment())
            .commitAllowingStateLoss()
    }
}