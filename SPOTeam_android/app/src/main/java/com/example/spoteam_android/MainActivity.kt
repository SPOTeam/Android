package com.example.spoteam_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.spoteam_android.data.ApiModels
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.ui.bookMark.BookmarkFragment
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.WriteContentFragment
//import com.example.spoteam_android.ui.mypage.ConsiderAttendanceMemberFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.MyStudyCommunityFragment
import com.example.spoteam_android.ui.study.MyStudyWriteContentFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
import com.example.spoteam_android.ui.study.quiz.HostFinishMakeQuizFragment
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val requestData = ApiModels.RequestData(
            regions = ApiModels.Regions(regions = listOf("1111054000")),
            themes = ApiModels.Themes(themes = listOf("어학"))
        )
        logAllSharedPreferences(this)
        // API 호출
//        ApiManager.sendRequestData(requestData,
//            onSuccess = { responseData ->
//                // UI 업데이트 등의 작업 수행
//                Toast.makeText(this, "응답 성공: ${responseData.message}", Toast.LENGTH_SHORT).show()
//                // saveToSharedPreferences(this,responseData)
//            },
//            onFailure = { throwable ->
//                // 오류 처리
//                Toast.makeText(this, "요청 실패: ${throwable.message}", Toast.LENGTH_SHORT).show()
//            }
//        )


        // 다른 아무 화면 클릭시 스터디 화면 사라지도록
        binding.root.setOnTouchListener { _, _ ->
            showStudyFrameLayout(false)
            binding.main.closeDrawers()
            getCurrentFragment()?.let {
                if(it is CommunityHomeFragment){
                    isOnCommunityHome(it)
                }
            }
            true
        }

        binding.mainFloatingButton.setOnClickListener {
            getCurrentFragment()?.let {
                if(it is CommunityHomeFragment) {
                    val writeCommunityFragment = WriteContentFragment(this)
                    writeCommunityFragment.show(supportFragmentManager, "Write Content")
                }
                if(it is DetailStudyFragment) {
                    val myStudyWriteCommunityFragment = MyStudyWriteContentFragment()
                    myStudyWriteCommunityFragment.show(supportFragmentManager, "My Study Write Content")
                }
            }
        }

        initCategoryNavigationView()
        init()
        isOnCommunityHome(HouseFragment())
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.main_frm)
    }

    private fun initCategoryNavigationView() {
        val navigationView: NavigationView = findViewById(R.id.mainCategoryBar)

        val languageBtn: TextView = navigationView.findViewById(R.id.language_btn)
        val licenseBtn: TextView = navigationView.findViewById(R.id.license_btn)
        val jobBtn: TextView = navigationView.findViewById(R.id.job_btn)
        val discussBtn: TextView = navigationView.findViewById(R.id.discuss_btn)
        val newsBtn: TextView = navigationView.findViewById(R.id.news_btn)
        val freeStudyBtn: TextView = navigationView.findViewById(R.id.freeStudy_btn)
        val projectBtn: TextView = navigationView.findViewById(R.id.project_btn)
        val competitionBtn: TextView = navigationView.findViewById(R.id.competition_btn)
        val majorStudyBtn: TextView = navigationView.findViewById(R.id.majorStudy_btn)
        val restBtn: TextView = navigationView.findViewById(R.id.rest_btn)

        languageBtn.setOnClickListener { handleCategoryClick("어학") }
        licenseBtn.setOnClickListener { handleCategoryClick("자격증") }
        jobBtn.setOnClickListener { handleCategoryClick("취업") }
        discussBtn.setOnClickListener { handleCategoryClick("토론") }
        newsBtn.setOnClickListener { handleCategoryClick("시사/뉴스") }
        freeStudyBtn.setOnClickListener { handleCategoryClick("자율 학습") }
        projectBtn.setOnClickListener { handleCategoryClick("프로젝트") }
        competitionBtn.setOnClickListener { handleCategoryClick("공모전") }
        majorStudyBtn.setOnClickListener { handleCategoryClick("전공/진로 학습") }
        restBtn.setOnClickListener { handleCategoryClick("기타") }
    }

    private fun handleCategoryClick(s: String) {
        when (s) {
            "어학" -> {

            }
            "자격증" -> {

            }
            "취업" -> {

            }
            "토론" -> {

            }
            "시사/뉴스" -> {

            }
            "자율 학습" -> {

            }
            "프로젝트" -> {

            }
            "공모전" -> {

            }
            "전공/진로 학습" -> {

            }
            "기타" -> {

            }
        }
        showFragment(CategoryFragment())
        binding.main.closeDrawers()
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
                    showFragment(HouseFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(HouseFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_category -> {
//                    showFragment(CategoryFragment()) // appbar 이후에 category frame 보여줌
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    binding.main.openDrawer(GravityCompat.START)
                    isOnCommunityHome(CategoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
                    // StudyFragment로의 전환 없이 FrameLayout의 visibility만 변경
                    showStudyFrameLayout(true)
                    isOnCommunityHome(StudyFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_bookmark -> {
                    showFragment(BookmarkFragment())
                    showStudyFrameLayout(false) // StudyFragment가 아니므로 FrameLayout 숨김
                    isOnCommunityHome(BookmarkFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_mypage -> {
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


