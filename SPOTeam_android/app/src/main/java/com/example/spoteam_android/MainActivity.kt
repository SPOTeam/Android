package com.example.spoteam_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.StudyFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.mypage.BookmarkFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val commitAllowingStateLoss = supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, CommunityHomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

//              HomeFragment
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, CommunityHomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
//              categoryFragment
                R.id.navigation_category -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, CategoryFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
//              StudyFragment
                R.id.navigation_study -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, StudyFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

//              찜Fragment
                R.id.navigation_bookmark -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, BookmarkFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

//              MypageFragment
                R.id.navigation_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MyPageFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}