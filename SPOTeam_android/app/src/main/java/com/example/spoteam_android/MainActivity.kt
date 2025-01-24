package com.example.spoteam_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.spoteam_android.data.ApiModels
import com.example.spoteam_android.databinding.ActivityMainBinding
import com.example.spoteam_android.login.LoginApiService
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
//import com.example.spoteam_android.ui.mypage.ConsiderAttendanceMemberFragment
import com.example.spoteam_android.ui.mypage.MyPageFragment
import com.example.spoteam_android.ui.study.DetailStudyFragment
import com.example.spoteam_android.ui.study.MyStudyCommunityFragment
import com.example.spoteam_android.ui.study.MyStudyWriteContentFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
import com.example.spoteam_android.weather.WeatherViewModel
import com.example.spoteam_android.weather.parseCsv
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Time
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val todoViewModel: TodoViewModel by viewModels()
    private val viewModel by viewModels<WeatherViewModel>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val calendar = Calendar.getInstance()
        val year =  String.format("%04d", calendar.get(Calendar.YEAR))
        val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1) // 두 자리로 변환
        val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) // 두 자리로 변환
        val baseDate = year+month+day
        val baseTime = getUpdatedTime()

        fetchRegions(baseDate.toInt(), baseTime.toInt())

        // 다른 아무 화면 클릭시 스터디 화면 사라지도록
        binding.root.setOnTouchListener{_, _ ->
            showStudyFrameLayout(false)
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
                    logTokens(this)
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
    fun showStudyFrameLayout(visible: Boolean) {
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

    override fun onBackPressed() {
        // 아무 동작도 하지 않도록 설정
        // super.onBackPressed()를 호출하지 않으면 기본 동작(뒤로 가기)이 수행되지 않습니다.
    }
    private fun logTokens(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val accessToken = email?.let { sharedPreferences.getString("${it}_accessToken", null) }
        val refreshToken = email?.let { sharedPreferences.getString("${it}_refreshToken", null) }

        Log.d("TokenLogger", "Current AccessToken: $accessToken")
        Log.d("TokenLogger", "Current RefreshToken: $refreshToken")
    }

    private fun fetchRegions(myDate: Int, myTime: Int) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getRegion().enqueue(object : Callback<RegionApiResponse> {
            override fun onResponse(call: Call<RegionApiResponse>, response: Response<RegionApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val regions = apiResponse.result.regions // 서버에서 받아오는 지역 리스트
                        if (regions.isNotEmpty()) {
                            Log.d("region", "Received regions: $regions")

                            // CSV 파일에서 데이터를 로드
                            val regionDataList = parseCsv(this@MainActivity)

                            // 첫 번째 Region의 그리드 값을 계산
                            regions.firstOrNull()?.let { region ->
                                val gridCoordinates = findGridByCode(region.code, regionDataList)
                                if (gridCoordinates != null) {
                                    val (gridX, gridY) = gridCoordinates
                                    Log.d("RegionGrid", "Region: ${region.neighborhood}, GridX: $gridX, GridY: $gridY")

                                    Log.d(
                                        "WeatherParams",
                                        "baseDate: $myDate, baseTime: $myTime, nx: ${gridX}, ny: $gridY"
                                    )

                                    // 동적으로 viewModel.getWeather 호출
                                    viewModel.getWeather(
                                        dataType = "JSON",
                                        numOfRows = 14,
                                        pageNo = 1,
                                        baseDate = myDate,
                                        baseTime = myTime,
                                        nx = gridX.toString(),
                                        ny = gridY.toString()
                                    )

                                    // Observe하여 결과 처리
                                    viewModel.weatherResponse.observe(this@MainActivity) { weatherResponse ->
                                        Log.d("WeatherResponseDebug", "Observer triggered")

                                        if (weatherResponse.body() != null) {
                                            val responseBody = weatherResponse.body()?.response
                                            if (responseBody?.body != null) {
                                                val items = responseBody.body.items
                                                if (!items?.item.isNullOrEmpty()) {
                                                    Log.d("WeatherResponseDebug", "Items count: ${items.item.size}")

                                                    // TMP와 SKY 데이터만 필터링
                                                    val tmpItem = items.item.find { it.category == "TMP" }
                                                    val skyItem = items.item.find { it.category == "SKY" }
                                                    val ptyItem = items.item.find { it.category == "PTY" }

                                                    if (tmpItem != null && skyItem != null && ptyItem != null) {
                                                        val temperature = tmpItem.fcstValue
                                                        val skyCondition = when (skyItem.fcstValue.toInt()) {
                                                            1 -> "맑음"
                                                            3 -> "구름많음"
                                                            4 -> "흐림"
                                                            else -> "알 수 없음"
                                                        }
                                                        val precipitationType = when (ptyItem.fcstValue.toInt()) {
                                                            0 -> "없음"
                                                            1 -> "비"
                                                            2 -> "비/눈"
                                                            3 -> "눈"
                                                            4 -> "소나기"
                                                            else -> "알 수 없음"
                                                        }

                                                        // 지역명과 함께 로그 출력
                                                        Log.d(
                                                            "WeatherInfo",
                                                            "지역명: ${region.neighborhood}, 기온: $temperature°C, 하늘 상태: $skyCondition, 강수 형태: $precipitationType"
                                                        )
                                                    } else {
                                                        Log.e("WeatherInfo", "TMP or SKY data not found")
                                                    }
                                                } else {
                                                    Log.e("WeatherResponseDebug", "Items are null or empty")
                                                }
                                            } else {
                                                Log.e("WeatherResponseDebug", "Response body is null or empty")
                                            }
                                        } else {
                                            Log.e("WeatherResponseDebug", "weatherResponse.body() is null")
                                        }
                                    }

                                } else {
                                    Log.e("RegionGrid", "Grid coordinates not found for region code: ${region.code}")
                                }
                            }
                        }
                    } else {
                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                        Log.e("RegionPreferenceFragment", "지역 가져오기 실패: $errorMessage")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                    Log.e("RegionPreferenceFragment", "지역 가져오기 실패: $errorMessage")
                }
            }

            override fun onFailure(call: Call<RegionApiResponse>, t: Throwable) {
                Log.e("RegionPreferenceFragment", "네트워크 오류: ${t.message}")
            }
        })
    }



    fun findGridByCode(regionCode: String, regionDataList: List<RegionData>): Pair<Int, Int>? {
        val regionData = regionDataList.find { it.administrativeCode == regionCode }
        return regionData?.let { Pair(it.gridX, it.gridY) }
    }

    fun getUpdatedTime(): String {
        // 현재 시간 가져오기
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // 현재 시간의 분을 기준으로 조정
        val adjustedHour = if (minute > 0) hour + 1 else hour

        // 기상청에서 지원하는 갱신 시간 목록 (3시간 간격)
        val updateTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)

        // 현재 시간에 맞는 TIME 계산
        val nextTime = updateTimes.firstOrNull { adjustedHour <= it }
            ?: updateTimes.first() // 23:01 이후라면 다음 날 첫 타임(02:00) 설정

        // 두 자리 문자열로 변환하여 반환
        return String.format("%02d00", nextTime)
    }




}