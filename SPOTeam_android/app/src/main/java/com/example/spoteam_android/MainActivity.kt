package com.example.spoteam_android

import StudyViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.spoteam_android.data.ApiModels
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.login.LoginApiService
import com.example.spoteam_android.login.TokenUtil
import com.example.spoteam_android.ui.study.todolist.TodoViewModel
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.bookMark.BookmarkFragment
//import com.example.spoteam_android.ui.bookMark.BookmarkFragment
//import com.example.spoteam_android.ui.bookMark.BookmarkFragment
import com.example.spoteam_android.ui.category.CategoryFragment
import com.example.spoteam_android.ui.category.CategoryNavViewFragment
import com.example.spoteam_android.ui.community.CommunityFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment
import com.example.spoteam_android.ui.community.WriteContentFragment
import com.example.spoteam_android.ui.interestarea.BottomNavVisibilityController
import com.example.spoteam_android.ui.interestarea.InterestFilterFragment
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFilterFragment
import com.example.spoteam_android.ui.myinterest.MyInterestStudyFragment
//import com.example.spoteam_android.ui.mypage.ConsiderAttendanceMemberFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.recruiting.RecruitingStudyFilterFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.MyStudyCommunityFragment
import com.example.spoteam_android.ui.study.MyStudyWriteContentFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
import com.example.spoteam_android.weather.WeatherViewModel
import com.example.spoteam_android.weather.parseCsv
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), BottomNavVisibilityController {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private val weatherViewModel: WeatherViewModel by viewModels() // Hilt 사용
    private val viewModel: StudyViewModel by viewModels()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val baseDate = getUpdatedDate()
        val baseTime = getUpdatedTime()

        fetchRegions(baseDate.toInt(), baseTime)



        binding.blurOverlayContainer.setOnClickListener{
            hideBlur()
            removeNavViewFragment()
        }

        binding.mainFloatingButtonToUp.setOnClickListener{
            val currentFragment = getCurrentFragment()
            if (currentFragment is AlertFragment) {
                currentFragment.scrollToTop()
            }
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

    override fun onResume() {
        super.onResume()

        val (accessToken, refreshToken) = TokenUtil.getStoredTokens(this)

        if (TokenUtil.isExpired(accessToken)) {
            val newToken = TokenUtil.refreshTokenSync(this, refreshToken ?: "")

            if (newToken == null) {
                Log.e("MainActivity", "토큰 갱신 실패. 로그인 화면으로 이동.")
                return // 이미 이동 처리됨
            } else {
                Log.d("MainActivity", "토큰 갱신 성공. 최신 토큰으로 유지됨.")
            }
        } else {
            Log.d("MainActivity", "토큰 유효함.")
        }


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
                    isOnCommunityHome(HouseFragment())
                    isOnAlertFragment(HouseFragment())
                    logTokens(this)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_category -> {
//                    isOnCommunityHome(CategoryFragment())
//                    addCategoryFragmentOnTop()
//                    showBlur()
                    switchFragment(CategoryFragment()) // categoryFragment 바로 이동
                    isOnCommunityHome(CategoryFragment())
                    isOnAlertFragment(CategoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_study -> {
                    // StudyFragment로의 전환 없이 FrameLayout의 visibility만 변경
                    hideBlur()
                    showFragment(StudyFragment())
                    isOnCommunityHome(StudyFragment())
                    isOnAlertFragment(StudyFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_bookmark -> {
                    hideBlur()
                    showFragment(BookmarkFragment())
                    isOnCommunityHome(BookmarkFragment())
                    isOnAlertFragment(BookmarkFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_mypage -> {
                    hideBlur()
                    showFragment(MyPageFragment())
                    isOnCommunityHome(MyPageFragment())
                    isOnAlertFragment(MyPageFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

    }

    fun removeNavViewFragment() {
        supportFragmentManager.beginTransaction()
            .remove(CategoryNavViewFragment())
            .commitAllowingStateLoss()
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


        when (fragment) {
            is InterestFilterFragment,
            is MyInterestStudyFilterFragment,
            is RecruitingStudyFilterFragment-> {
                binding.mainBnv.visibility = View.GONE
            }
            else -> {
                binding.mainBnv.visibility = View.VISIBLE
            }
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
        } else {
            binding.mainFloatingButton.visibility = View.GONE
        }
    }

    fun isOnAlertFragment(fragment : Fragment) {
        if(fragment is AlertFragment) {
            binding.mainFloatingButtonToUp.visibility = View.VISIBLE
        } else {
            binding.mainFloatingButtonToUp.visibility = View.GONE
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

//    override fun onBackPressed() {
//        // 아무 동작도 하지 않도록 설정
//        // super.onBackPressed()를 호출하지 않으면 기본 동작(뒤로 가기)이 수행되지 않습니다.
//    }

    private fun logTokens(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val accessToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
        val refreshToken = email?.let { sharedPreferences.getString("${it}_refreshToken", null) }

        Log.d("TokenLogger", "Current AccessToken: $accessToken")
        Log.d("TokenLogger", "Current RefreshToken: $refreshToken")
    }

    private fun fetchRegions(myDate: Int, myTime: String) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getRegion().enqueue(object : Callback<RegionApiResponse> {
            override fun onResponse(call: Call<RegionApiResponse>, response: Response<RegionApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val regions = apiResponse.result.regions
                        if (regions.isNotEmpty()) {
                            Log.d("region", "Received regions: $regions")

                            val regionDataList = parseCsv(this@MainActivity)
                            regions.firstOrNull()?.let { region ->
                                val gridCoordinates = findGridByCode(region.code, regionDataList)
                                if (gridCoordinates != null) {
                                    val (gridX, gridY) = gridCoordinates
                                    Log.d("RegionGrid", "Region: ${region.neighborhood}, GridX: $gridX, GridY: $gridY")

                                    // WeatherViewModel을 통해 날씨 데이터 요청
                                    weatherViewModel.getWeather(
                                        dataType = "JSON",
                                        numOfRows = 14,
                                        pageNo = 1,
                                        baseDate = myDate,
                                        baseTime = myTime,
                                        nx = gridX.toString(),
                                        ny = gridY.toString()
                                    )
                                } else {
                                    Log.e("RegionGrid", "Grid coordinates not found for region code: ${region.code}")
                                }
                            }
                        }
                    } else {
                        Log.e("RegionAPI", "Failed to get region data")
                    }
                } else {
                    Log.e("RegionAPI", "API Response failed")
                }
            }

            override fun onFailure(call: Call<RegionApiResponse>, t: Throwable) {
                Log.e("RegionAPI", "Network error: ${t.message}")
            }
        })
    }

    fun findGridByCode(regionCode: String, regionDataList: List<RegionData>): Pair<Int, Int>? {
        val regionData = regionDataList.find { it.administrativeCode == regionCode }
        return regionData?.let { Pair(it.gridX, it.gridY) }
    }

    fun getUpdatedDate(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // 00:00 ~ 02:59 사이라면 baseDate를 하루 전으로 설정
        if (hour < 3) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)  // 하루 빼기
        }

        // yyyyMMdd 형식의 날짜 반환
        return SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(calendar.time)
    }

    fun getUpdatedTime(): String {
        // 현재 시간 가져오기
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // 기상청에서 지원하는 갱신 시간 목록 (3시간 간격)
        val updateTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)

        // 현재 시간에 맞는 TIME 계산 (API 제공 시간이 지난 경우, 현재 시간보다 작은 가장 가까운 값 선택)
        val previousTime = updateTimes.lastOrNull { hour >= it } ?: updateTimes.last()

        val formattedTime = String.format("%02d00", previousTime)
        return formattedTime
    }

    fun getWeatherBackground(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        // 기상청 갱신 시간 목록 (3시간 간격)
        val updateTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)
        val previousTime = updateTimes.lastOrNull { hour >= it } ?: updateTimes.last()

        // 🟢 밤일 경우에만 ic_weather_night_background 반환
        return if (previousTime in listOf(2, 5, 17, 20, 23)) {
            R.drawable.ic_weather_night_background
        } else {
            R.drawable.ic_weather_background // 낮일 경우 기본값 유지
        }
    }

    override fun hideBottomNav() {
        binding.mainBnv.visibility = View.GONE
    }

    override fun showBottomNav() {
        binding.mainBnv.visibility = View.VISIBLE
    }

}