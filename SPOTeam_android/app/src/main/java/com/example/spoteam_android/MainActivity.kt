//package com.example.spoteam_android
//
//import android.R
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.navigation.NavController
//import com.example.spoteam_android.databinding.ActivityMainBinding
//import com.example.spoteam_android.ui.category.CategoryFragment
//import com.example.spoteam_android.ui.category.StudyFragment
//import com.example.spoteam_android.ui.mypage.BookmarkFragment
//import com.example.spoteam_android.ui.mypage.MyPageFragment
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var navController: NavController
//    private lateinit var recentSearchAdapter: RecentSearchAdapter
//    private lateinit var recentSearchList: MutableList<String>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        init()
//        switchFragment()
//    }
//    private fun init() {
//        val commitAllowingStateLoss = supportFragmentManager.beginTransaction()
//            .replace(R.id.main_frm, HouseFragment())
//            .commitAllowingStateLoss()
//
//        binding.mainBnv.setOnItemSelectedListener{ item ->
//            when (item.itemId) {
//
//    //              HomeFragment
//                R.id.navigation_home -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, SearchFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//    //              categoryFragment
//                R.id.navigation_category -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, CategoryFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//    //              StudyFragment
//                R.id.navigation_study -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, StudyFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//
//    //              찜Fragment
//                R.id.navigation_bookmark -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, BookmarkFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//
//    //              MypageFragment
//                R.id.navigation_mypage -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frm, MyPageFragment())
//                        .commitAllowingStateLoss()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            false
//            }
//
//    }
//    fun switchFragment() {
//        // Fragment 전환
//        val searchFragment: Fragment = SearchFragment()
//        val args = Bundle()
//        args.putString("key", "value")
//        searchFragment.arguments = args
//
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.main_frm, searchFragment)
////        transaction.addToBackStack(null) // 뒤로 가기 버튼을 누를 때 이전 Fragment로 돌아가려면 추가
//        transaction.commit()
//    }
//
//}

package com.example.spoteam_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
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
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var recentSearchList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrm.id, HouseFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainFrm.id, HouseFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.navigation_category -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainFrm.id, CategoryFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.navigation_study -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainFrm.id, StudyFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.navigation_bookmark -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainFrm.id, BookmarkFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.navigation_mypage -> {
                    supportFragmentManager.beginTransaction()
                        .replace(binding.mainFrm.id, MyPageFragment())
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }
    }

    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrm.id, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}
