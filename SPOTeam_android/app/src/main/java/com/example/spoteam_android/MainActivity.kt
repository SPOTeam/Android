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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)


//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            var selectedFragment: Fragment? = null
//            when (item.itemId) {
//                R.id.navigation_home -> {
//                    selectedFragment = HouseFragment()
//                }
//                R.id.navigation_category -> {
//                    selectedFragment = CategoryFragment()
//                }
//                R.id.navigation_study -> {
//                    selectedFragment = StudyFragment()
//                }
//                R.id.navigation_bookmark -> {
//                    selectedFragment = BookmarkFragment()
//                }
//                R.id.navigation_mypage -> {
//                    selectedFragment = MyPageFragment()
//                }
//            }
//            if (selectedFragment != null) {
//                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
//            }
//            true
//        }
//
//        if (savedInstanceState == null) {
//            bottomNavigationView.selectedItemId = R.id.navigation_home
//        }


    }



}