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
import com.example.spoteam_android.checklist.CheckListCategoryActivity
import com.example.spoteam_android.databinding.ActivityRegisterInformationBinding
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterInformation : ComponentActivity() {
    private lateinit var binding: ActivityRegisterInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mode = intent.getStringExtra("mode") ?: "START"

        if (mode == "START") {
            navigateToCheckList()
            return
        }

        if (mode == "FINAL") {
            val selectedThemes = intent.getStringArrayListExtra("selectedThemes") ?: listOf()
            val selectedPurpose = intent.getIntegerArrayListExtra("selectedPurpose") ?: listOf()
            val selectedLocations = intent.getStringArrayListExtra("selectedLocations") ?: listOf()

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

            setupProgressBar()
        }
    }

    private fun navigateToCheckList() {
        val intent = Intent(this, CheckListCategoryActivity::class.java)
        startActivity(intent)
        finish()
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
