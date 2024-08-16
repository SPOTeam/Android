package com.example.spoteam_android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.ThemePreferences
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
import com.example.spoteam_android.login.LoginApiService
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Random

class RegisterInformation : ComponentActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedThemes = intent.getStringArrayListExtra("selectedThemes")
        val selectedPurpose = intent.getStringArrayListExtra("selectedPurpose")
        val selectedLocations = intent.getStringArrayListExtra("selectedLocations")

        // SharedPreferences에서 현재 이메일을 가져오기
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email == null) {
            Log.e("RegisterInformation", "Current email not found in SharedPreferences")
            return
        }

        val memberId = sharedPreferences.getInt("${email}_memberId", -1)

        if (memberId == -1) {
            Log.e("RegisterInformation", "Member ID not found in SharedPreferences")
            return
        }

        // 닉네임 생성 및 저장
        val randomNickname = generateRandomNickname()
        saveNicknameToPreferences(email, randomNickname)

        // 서버에 POST 요청 보내기
        postPreferencesToServer(
            memberId = memberId,
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
                delay(500)
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
        memberId: Int,
        themes: List<String>,
        purposes: List<String>,
        regions: List<String>
    ) {

        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

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


    private fun generateRandomNickname(): String {
        val letters = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val random = Random()

        val letterPart = (1..4).map { letters[random.nextInt(letters.length)] }.joinToString("")
        val numberPart = (1..2).map { numbers[random.nextInt(numbers.length)] }.joinToString("")

        return "$letterPart$numberPart"
    }

    private fun saveNicknameToPreferences(email: String, randomNickname: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("${email}_randomNickname", randomNickname)
            apply()  // 비동기 저장
        }
    }

}
