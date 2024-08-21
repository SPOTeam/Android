package com.example.spoteam_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.spoteam_android.data.ApiModels
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.todolist.TodoViewModel
import com.example.spoteam_android.ui.bookMark.BookmarkFragment
//import com.example.spoteam_android.ui.bookMark.BookmarkFragment
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.CategoryNavViewFragment
import com.example.spoteam_android.ui.community.CommunityFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.WriteContentFragment
//import com.example.spoteam_android.ui.mypage.ConsiderAttendanceMemberFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.MyStudyCommunityFragment
import com.example.spoteam_android.ui.study.MyStudyWriteContentFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val todoViewModel: TodoViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val drawerLayout: DrawerLayout = binding.drawerLayout

        setContentView(binding.root)

        // 다른 아무 화면 클릭시 스터디 화면 사라지도록
        binding.root.setOnTouchListener { _, _ ->
            showStudyFrameLayout(false)
            drawerLayout.closeDrawers()
            getCurrentFragment()?.let {
                if(it is CommunityHomeFragment){
                    isOnCommunityHome(it)
                }
            }
            true
        }

        binding.blurOverlayContainer.setOnClickListener{
            hideBlur()
            removeNavViewFragment()
        }

        binding.mainFloatingButton.setOnClickListener {
            val writeCommunityFragment = WriteContentFragment().apply {
                setStyle(
                    BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
            }

            val myStudyWriteCommunityFragment = MyStudyWriteContentFragment().apply {
                setStyle(
                    BottomSheetDialogFragment.STYLE_NORMAL,
                    R.style.AppBottomSheetDialogBorder20WhiteTheme
                )
            }
            getCurrentFragment()?.let {
                if(it is CommunityHomeFragment) {
                    writeCommunityFragment.show(supportFragmentManager, "Write Content")
                }
                if(it is CommunityFragment) {
                    writeCommunityFragment.show(supportFragmentManager, "Write Content")
                }
                if(it is DetailStudyFragment) {
                    myStudyWriteCommunityFragment.show(supportFragmentManager, "My Study Write Content")
                }
            }
        }

        init()
        isOnCommunityHome(HouseFragment())
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.main_frm)
    }

    private fun init() {
        // 초기 화면 설정: 기본으로 HouseFragment를 보이도록 설정
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HouseFragment())
            .commitAllowingStateLoss()

        // BottomNavigationView의 아이템 선택 리스너 설정
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    hideBlur()
                    showFragment(HouseFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(HouseFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_category -> {
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(CategoryFragment())
                    addCategoryFragmentOnTop()
                    showBlur()
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
                    // StudyFragment로의 전환 없이 FrameLayout의 visibility만 변경
                    hideBlur()
                    showStudyFrameLayout(true)
                    isOnCommunityHome(StudyFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_bookmark -> {
                    hideBlur()
                    showFragment(BookmarkFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(BookmarkFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_mypage -> {
                    hideBlur()
                    showFragment(MyPageFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(MyPageFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

        setupButtonListeners()
    }

    fun removeNavViewFragment() {
        supportFragmentManager.beginTransaction()
            .remove(CategoryNavViewFragment())
            .commitAllowingStateLoss()
    }

    private fun showBlur() {
        findViewById<View>(R.id.blur_overlay_container).visibility = View.VISIBLE
    }

    fun hideBlur() {
        findViewById<View>(R.id.blur_overlay_container).visibility = View.GONE
    }

    private fun addCategoryFragmentOnTop() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.blur_overlay_container, CategoryNavViewFragment())
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    // Fragment 교체 메서드
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
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

    fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFrm.id, fragment)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun isOnCommunityHome(fragment: Fragment) {
        if (fragment is CommunityHomeFragment) {
            binding.mainFloatingButton.visibility = View.VISIBLE
        } else if (fragment is MyStudyCommunityFragment){
            binding.mainFloatingButton.visibility = View.VISIBLE
        } else {
            binding.mainFloatingButton.visibility = View.GONE
        }
    }

    private fun readFromSharedPreferences(context: Context): ApiModels.ResponseData? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        println("readFromSharedPreferences()이 성공적으로 수행되었습니다.")

        // JSON 문자열로 변환하여 저장한 데이터 읽기
        val jsonString = sharedPreferences.getString("responseData", null)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, ApiModels.ResponseData::class.java)
        } else {
            null
        }
    }
    private fun logAllSharedPreferences(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = sharedPreferences.all
        for ((key, value) in allEntries) {
            Log.d("showkey", "$key: $value")
        }
    }

}