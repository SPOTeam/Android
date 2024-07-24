package com.example.spoteam_android

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.spoteam_android.ui.community.WriteContentFragment
import com.example.spoteam_android.ui.mypage.BookmarkFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetView = layoutInflater.inflate(R.layout.fragment_write_content, null)
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)

        binding.mainFloatingButton.setOnClickListener {
            val bottomSheetDialog = WriteContentFragment()
            bottomSheetDialog.show(supportFragmentManager, bottomSheetDialog.tag)
        }

        init()
        isOnCommunityHome(HouseFragment())
    }

    fun isOnCommunityHome(fragment : Fragment){
        if (fragment is CommunityHomeFragment) {
            binding.mainFloatingButton.visibility = View.VISIBLE
        } else {
            binding.mainFloatingButton.visibility = View.GONE
        }
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
                    isOnCommunityHome(HouseFragment())
                    return@setOnItemSelectedListener true
                }
//              categoryFragment
                R.id.navigation_category -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, CategoryFragment())
                        .commitAllowingStateLoss()
                    isOnCommunityHome(CategoryFragment())
                    return@setOnItemSelectedListener true
                }
//              StudyFragment
                R.id.navigation_study -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, StudyFragment())
                        .commitAllowingStateLoss()
                    isOnCommunityHome(StudyFragment())
                    return@setOnItemSelectedListener true
                }

//              찜Fragment
                R.id.navigation_bookmark -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, BookmarkFragment())
                        .commitAllowingStateLoss()
                    isOnCommunityHome(BookmarkFragment())
                    return@setOnItemSelectedListener true
                }

//              MypageFragment
                R.id.navigation_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MyPageFragment())
                        .commitAllowingStateLoss()
                    isOnCommunityHome(MyPageFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}