package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentThemePreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ThemePreferenceFragment : Fragment() {

    private lateinit var binding: FragmentThemePreferenceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentThemePreferenceBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            // 이메일 기반으로 memberId 가져오기
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                fetchThemes(memberId)
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    private fun fetchThemes(memberId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginApiService::class.java)

        service.getThemes(memberId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val themes = apiResponse.result.themes
                        if (themes.isNotEmpty()) {
                            displayThemes(themes)
                        } else {
                            binding.themeTv.text = "선택된 테마가 없습니다."
                        }
                    } else {
                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                        Log.e("ThemePreferenceFragment", "테마 가져오기 실패: $errorMessage")
                        Toast.makeText(requireContext(), "테마 가져오기 실패: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                    Log.e("ThemePreferenceFragment", "테마 가져오기 실패: $errorMessage")
                    Toast.makeText(requireContext(), "테마 가져오기 실패: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("ThemePreferenceFragment", "테마 가져오기 오류", t)
                Toast.makeText(requireContext(), "테마 가져오기 오류: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayThemes(themes: List<String>) {
        val themesText = themes.joinToString(", ")
        binding.themeTv.text = "선택된 테마: $themesText"
    }
}

data class ThemeResult(
    val themes: List<String>
)
// 서버 응답 전체를 담을 데이터 클래스
data class ApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ThemeResult
)
