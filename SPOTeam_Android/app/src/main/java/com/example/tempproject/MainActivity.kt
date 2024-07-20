package com.example.tempproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tempproject.databinding.ActivityMainBinding
import com.example.tempproject.ui.dashboard.DashboardFragment
import com.example.tempproject.ui.home.HomeFragment
import com.example.tempproject.ui.notifications.NotificationsFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.home_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.dashboard_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, DashboardFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.notifications_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, NotificationsFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}