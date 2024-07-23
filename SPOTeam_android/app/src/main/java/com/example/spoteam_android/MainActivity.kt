package com.example.spoteam_android

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.databinding.ActivityMainBinding

import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.StudyFragment
import com.example.spoteam_android.ui.home.HomeFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.mypage.BookmarkFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
//        navController = findNavController(R.id.nav_host_fragment)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNavigationView.setupWithNavController(navController)
    }

    private fun init() {
        val commitAllowingStateLoss = supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HouseFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

//              HomeFragment
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HouseFragment())
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

//    private fun init() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, HomeFragment())
//            .commitAllowingStateLoss()
//
//        binding.mainBnv.setOnItemSelectedListener{ item ->
//            when (item.itemId) {
//
//                R.id.home_fragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, HomeFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.dashboard_fragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, DashboardFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//                R.id.notifications_fragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, NotificationsFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            false
//        }
//    }
}