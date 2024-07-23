package com.example.spoteam_android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.StudyFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.mypage.BookmarkFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        //다른 아무 화면 클릭시 스터디 화면 사라지도록
        binding.root.setOnTouchListener { _, _ ->
            showStudyFrameLayout(false)
            true
        }
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 초기 화면 설정: 기본으로 HomeFragment를 보이도록 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HouseFragment())
            .commitAllowingStateLoss()

        // BottomNavigationView의 아이템 선택 리스너 설정
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showFragment(HouseFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_category -> {
                    showFragment(CategoryFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
                    // StudyFragment로의 전환 없이 FrameLayout의 visibility만 변경
                    showStudyFrameLayout(true)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_bookmark -> {
                    showFragment(BookmarkFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_mypage -> {
                    showFragment(MyPageFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

        setupButtonListeners()
    }

    // Fragment 교체 메서드
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }

    // activity_main_study_fl FrameLayout의 visibility를 설정하는 메서드
    private fun showStudyFrameLayout(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        findViewById<View>(R.id.activity_main_study_fl).visibility = visibility
    }

    private fun setupButtonListeners() {
        // ImageButton 클릭 리스너 설정
        findViewById<View>(R.id.activity_main_mystudy_ib).setOnClickListener {
            showFragment(StudyFragment())
            showStudyFrameLayout(false) // StudyFragment를 보이도록 하되 FrameLayout은 숨김
        }

        findViewById<View>(R.id.activity_main_registerstudy_ib).setOnClickListener {
            showFragment(RegisterStudyFragment())
            showStudyFrameLayout(false) // RegisterFragment를 보이도록 하되 FrameLayout은 숨김
        }
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