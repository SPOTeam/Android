package com.umcspot.android.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.umcspot.android.MainActivity
import com.umcspot.android.RegionsPreferences
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.StudyReasons
import com.umcspot.android.ThemePreferences
import com.umcspot.android.checklist.CheckListCategoryActivity
import com.umcspot.android.databinding.ActivityRegisterInformationBinding
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterInformation : ComponentActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding
    private var lastBackPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mode = intent.getStringExtra("mode") ?: "START"

        if (mode == "FINAL") {
            val selectedThemes = intent.getStringArrayListExtra("selectedThemes") ?: listOf()
            val selectedPurpose = intent.getIntegerArrayListExtra("selectedPurpose") ?: listOf()
            val selectedLocations = intent.getStringArrayListExtra("selectedLocations") ?: listOf()

            binding.registerMsgTv.text = "체크리스트 등록 중.."

            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val email = sharedPreferences.getString("currentEmail", null)
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (email == null || memberId == -1) {
                Log.e("RegisterInformation", "SharedPreferences에 사용자 정보 없음")
                return
            }

            postPreferencesToServer(
                memberId = memberId,
                themes = selectedThemes,
                purposes = selectedPurpose,
                regions = selectedLocations
            )
        }

        setupProgressBar(mode)
    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressedTime <= 2000) {
            // 2초 안에 연속 두 번 누른 경우 앱 종료
            finishAffinity() // 앱의 모든 액티비티 종료
        } else {
            lastBackPressedTime = currentTime
            Toast.makeText(this, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun navigateToCheckList() {
        val intent = Intent(this, CheckListCategoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupProgressBar(mode: String) {
        lifecycleScope.launch {
            val totalSteps = 5
            for (i in 1..totalSteps) {
                delay(500)
                updateProgressBar(i * 20)
            }

            if (mode == "FINAL") {
                binding.registerMsgTv.text = "내 정보 등록 완료!"
                delay(500)
                navigateToMainScreen()
            } else if (mode == "START") {
                navigateToCheckList()
            }
        }
    }



    private fun updateProgressBar(progress: Int) {
        binding.activityRegisterProgressbar.progress = progress
    }

    private fun navigateToMainScreen() {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val email = sharedPreferences.getString("currentEmail", null)

        // isSpotMember 상태 true로 저장
        if (!email.isNullOrEmpty()) {
            editor.putBoolean("${email}_isSpotMember", true)
        }
        editor.apply()

        // 이제 메인 화면으로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }



    private fun postPreferencesToServer(
        memberId: Int,
        themes: List<String>,
        purposes: List<Int>,
        regions: List<String>
    ) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        val themePreferences = ThemePreferences(themes)
        val purposePreferences = StudyReasons(purposes)
        val regionsPreferences = RegionsPreferences(regions)

        val gson = Gson()
        Log.d("RegisterInformation", "Theme: ${gson.toJson(themePreferences)}")
        Log.d("RegisterInformation", "Purpose: ${gson.toJson(purposePreferences)}")
        Log.d("RegisterInformation", "Regions: ${gson.toJson(regionsPreferences)}")

        service.postThemes(themePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Themes POST success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Themes POST failed", t)
            }
        })

        service.postPurposes(purposePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Purposes POST success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Purposes POST failed", t)
            }
        })

        service.postRegions(regionsPreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("RegisterInformation", "Regions POST success")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("RegisterInformation", "Regions POST failed", t)
            }
        })
    }
}
