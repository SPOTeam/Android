package com.example.spoteam_android.checklist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityCheckListLocationBinding
import com.example.spoteam_android.login.LocationSearchActivity

class CheckListLocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckListLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCheckListLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent = Intent(this, LocationSearchActivity::class.java)
        binding.checklistspotLocationPlusBt.setOnClickListener { startActivity(intent) }
    }
}