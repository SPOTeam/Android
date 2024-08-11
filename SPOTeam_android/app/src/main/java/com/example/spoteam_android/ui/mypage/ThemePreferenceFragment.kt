package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentThemePreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import com.example.spoteam_android.ui.study.IntroduceStudyFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response as OkHttpResponse // 'OkHttpResponse'로 변경
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse // 'RetrofitResponse'로 변경
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
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                fetchThemes(memberId)
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }
        binding.fragmentThemePreferenceBackBt.setOnClickListener {
            goToPreviusFragment()
        }
        return binding.root
    }

    private fun fetchThemes(memberId: Int) {
        val retrofit = getRetrofit()
        val service = retrofit.create(LoginApiService::class.java)

        service.getThemes(memberId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: RetrofitResponse<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val themes = apiResponse.result.themes
                        if (themes.isNotEmpty()) {
                            displayThemes(themes)
                        } else {

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
        val flexboxLayout = binding.fragmentThemePreferenceFlexboxLayout
        flexboxLayout.removeAllViews() // 기존 TextView를 모두 제거

        for (theme in themes) {
            // 텍스트 변환 처리
            val processedText = when {
                theme.contains("시사") && theme.contains("뉴스") -> theme.replace("뉴스", "/뉴스")
                theme.contains("전공") && theme.contains("진로") -> theme.replace("및", "/")
                theme.contains("자율") && theme.contains("학습") -> theme.replace("자율학습", "자율 학습")
                else -> theme
            }

            // TextView 객체를 동적으로 생성하고 스타일 적용
            val textView = TextView(requireContext()).apply {
                text = processedText
                textSize = 16f // 16sp
                setPadding(50, 15, 50, 15) // padding: left, top, right, bottom
                setTextColor(resources.getColor(R.color.custom_chip_text, null))
                setBackgroundResource(R.drawable.theme_selected_corner) // 커스텀 배경 적용 (선택적)

                // Optional: Margin 설정
                val params = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 30
                    topMargin = 30
                }
                layoutParams = params
            }

            flexboxLayout.addView(textView)
        }
    }


    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyPageFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }





    private fun getRetrofit(): Retrofit {
        val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NywidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMzc2NDk3LCJleHAiOjE3MjMzODAwOTd9.xxtr97HO-u1VW1dAu8fRyobh8D7KywUk-6akBE4RE1U"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoginchecklistAuthInterceptor(authToken))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class LoginchecklistAuthInterceptor(private val authToken: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): OkHttpResponse { // 'OkHttpResponse'로 변경
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $authToken")
                .build()
            return chain.proceed(request)
        }
    }
}
data class ApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ThemeResult
)

data class ThemeResult(
    val memberId: Int,
    val themes: List<String>
)
