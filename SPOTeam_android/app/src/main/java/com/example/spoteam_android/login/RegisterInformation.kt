package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemePreferences
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterInformation : ComponentActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")
        val selectedPurpose = intent.getStringArrayListExtra("selectedPurpose")
        val selectedLocations = intent.getStringArrayListExtra("selectedLocations")


        // 서버에 POST 요청 보내기
        postPreferencesToServer(
            themes = selectedThemes ?: listOf(),
            purposes = selectedPurpose ?: listOf(),
            regions = selectedLocations ?: listOf()
        )

        setupProgressBar()
    }

    private fun setupProgressBar() {
        lifecycleScope.launch {
            val totalSteps = 5
            for (i in 1..totalSteps) {
                delay(1000)
                updateProgressBar(i * 20)
            }
            navigateToMainScreen()
        }
    }

    private fun updateProgressBar(progress: Int) {
        binding.activityRegisterProgressbar.progress = progress
    }

    private fun navigateToMainScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun postPreferencesToServer(
        themes: List<String>,
        purposes: List<String>,
        regions: List<String>
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginApiService::class.java)
        val memberId = 12345 // 실제 멤버아이디로 변경

        val themePreferences = ThemePreferences(themes)
        val purposePreferences = StudyReasons(purposes)
        val regionsPreferences = RegionsPreferences(regions)

        // Gson 인스턴스 생성
        val gson = Gson()

        // 데이터 JSON으로 변환하여 로그에 출력
        val themePreferencesJson = gson.toJson(themePreferences)
        val purposePreferencesJson = gson.toJson(purposePreferences)
        val regionsPreferencesJson = gson.toJson(regionsPreferences)

        Log.d("RegisterInformation", "Theme Preferences JSON: $themePreferencesJson")
        Log.d("RegisterInformation", "Reasons Preferences JSON: $purposePreferencesJson")
        Log.d("RegisterInformation", "Regions Preferences JSON: $regionsPreferencesJson")

        // POST 요청 보내기 성공 실패 로그
        service.postThemes(memberId, themePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Themes POST request success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Themes POST request failure", t)
            }
        })

        service.postPurposes(memberId, purposePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Purposes POST request success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Purposes POST request failure", t)
            }
        })

        service.postRegions(memberId, regionsPreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Regions POST request success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Regions POST request failure", t)
            }
        })
    }

}
//package com.example.spoteam_android.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.lifecycle.lifecycleScope
//import com.example.spoteam_android.MainActivity
//import com.example.spoteam_android.R
//import com.example.spoteam_android.RegionsPreferences
//import com.example.spoteam_android.StudyPurpose
//import com.example.spoteam_android.ThemePreferences
//import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
//import com.google.gson.Gson
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class RegisterInformation : ComponentActivity() {
//    private lateinit var binding: ActivityRegisterInformationBinding
//    private val totalRequests = 3 // 총 요청 수 (themes, purposes, regions)
//    private var completedRequests = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")
//        val selectedPurpose = intent.getStringArrayListExtra("selectedPurpose")
//        val selectedLocations = intent.getStringArrayListExtra("selectedLocations")
//
//        // 서버에 POST 요청 보내기
//        postPreferencesToServer(
//            themes = selectedThemes ?: listOf(),
//            purposes = selectedPurpose ?: listOf(),
//            regions = selectedLocations ?: listOf()
//        )
//    }
//
//    private fun setupProgressBar() {
//        lifecycleScope.launch {
//            val totalSteps = 5 // 총 단계
//            while (completedRequests < totalRequests) {
//                delay(1000) // 1초마다 체크
//                // 프로그래스바를 업데이트합니다.
//                val progress = (completedRequests * 100) / totalRequests
//                updateProgressBar(progress)
//            }
//            // 마지막 단계까지 진행된 후, 100%로 설정합니다.
//            updateProgressBar(100)
//            navigateToMainScreen()
//        }
//    }
//
//    private fun updateProgressBar(progress: Int) {
//        binding.activityRegisterProgressbar.progress = progress
//    }
//
//    private fun navigateToMainScreen() {
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
//    }
//
//    private fun postPreferencesToServer(
//        themes: List<String>,
//        purposes: List<String>,
//        regions: List<String>
//    ) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://www.teamspot.site/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val service = retrofit.create(LoginApiService::class.java)
//        val memberId = 12345 // 실제 멤버아이디로 변경
//
//        val themePreferences = ThemePreferences(themes)
//        val purposePreferences = StudyPurpose(purposes)
//        val regionsPreferences = RegionsPreferences(regions)
//
//        // Gson 인스턴스 생성
//        val gson = Gson()
//
//        // 데이터 JSON으로 변환하여 로그에 출력
//        val themePreferencesJson = gson.toJson(themePreferences)
//        val purposePreferencesJson = gson.toJson(purposePreferences)
//        val regionsPreferencesJson = gson.toJson(regionsPreferences)
//
//        Log.d("RegisterInformation", "Theme Preferences JSON: $themePreferencesJson")
//        Log.d("RegisterInformation", "Purpose Preferences JSON: $purposePreferencesJson")
//        Log.d("RegisterInformation", "Regions Preferences JSON: $regionsPreferencesJson")
//
//        // 요청을 리스트로 만듭니다.
//        val requests = listOf(
//            service.postThemes(memberId, themePreferences),
//            service.postPurposes(memberId, purposePreferences),
//            service.postRegions(memberId, regionsPreferences)
//        )
//
//        // 각 요청에 대해 콜백을 설정합니다.
//        requests.forEachIndexed { index, call ->
//            call.enqueue(object : Callback<Void> {
//                override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                    Log.d("RegisterInformation", "POST request success for index $index")
//                    completedRequests++
//                    // 요청이 완료된 후 프로그래스바를 업데이트합니다.
//                    val progress = (completedRequests * 100) / totalRequests
//                    updateProgressBar(progress)
//
//                    // 모든 요청이 완료되면 프로그래스바를 업데이트하고, 화면을 전환합니다.
//                    if (completedRequests == totalRequests) {
//                        setupProgressBar()
//                    }
//                }
//
//                override fun onFailure(call: Call<Void>, t: Throwable) {
//                    Log.e("RegisterInformation", "POST request failure for index $index", t)
//                    completedRequests++
//                    // 실패한 경우에도 진행 상태를 업데이트합니다.
//                    val progress = (completedRequests * 100) / totalRequests
//                    updateProgressBar(progress)
//
//                    // 모든 요청이 완료되면 프로그래스바를 업데이트하고, 화면을 전환합니다.
//                    if (completedRequests == totalRequests) {
//                        setupProgressBar()
//                    }
//                }
//            })
//        }
//    }
//}
