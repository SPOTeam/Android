package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.ThemePreferences
import com.example.spoteam_android.databinding.FragmentThemePreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import com.example.spoteam_android.ui.study.StudyRegisterCompleteDialog
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class ThemePreferenceFragment : Fragment() {

    private lateinit var binding: FragmentThemePreferenceBinding
    private val selectedThemes = mutableListOf<String>() // 선택된 테마를 저장할 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentThemePreferenceBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                fetchThemes() // GET 요청으로 테마 가져오기
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }

        // 테마 수정 버튼 클릭 시
        binding.themeEditIv.setOnClickListener {
            binding.buttonLayout.visibility = View.VISIBLE
            binding.fragmentThemePreferenceFlexboxLayout.visibility = View.GONE
            binding.activityChecklistChipGroup.visibility = View.VISIBLE
            setChipGroup() // Chip 그룹 설정
        }

        // POST 요청을 보내는 버튼 클릭 시
        binding.editThemeFinishBt.setOnClickListener {
            if (email != null) {
                val memberId = sharedPreferences.getInt("${email}_memberId", -1)
                if (memberId != -1) {
                    postThemesToServer(memberId, selectedThemes) // POST 요청으로 테마 전송

                } else {
                    Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        binding.editThemeCancelBt.setOnClickListener {
            binding.buttonLayout.visibility = View.GONE
            binding.fragmentThemePreferenceFlexboxLayout.visibility = View.VISIBLE
            binding.activityChecklistChipGroup.visibility = View.GONE
        }

        binding.fragmentThemePreferenceBackBt.setOnClickListener {
            goToPreviusFragment()
        }

        return binding.root
    }

        private fun fetchThemes() {
            val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

            service.getThemes().enqueue(object : Callback<ThemeApiResponse> {
                override fun onResponse(
                    call: Call<ThemeApiResponse>,
                    response: RetrofitResponse<ThemeApiResponse>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.isSuccess) {
                            val themes = apiResponse.result.themes // 서버에서 받아오는 테마 리스트
                            if (themes.isNotEmpty()) {
                                displayThemes(themes)
                            }
                        } else {
                            val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                            Log.e("ThemePreferenceFragment", "테마 가져오기 실패: $errorMessage")
                            Toast.makeText(
                                requireContext(),
                                "테마 가져오기 실패: $errorMessage",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                        Log.e("ThemePreferenceFragment", "테마 가져오기 실패: $errorMessage")
                        Toast.makeText(requireContext(), "테마 가져오기 실패: $errorMessage", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onFailure(call: Call<ThemeApiResponse>, t: Throwable) {
                    Log.e("ThemePreferenceFragment", "테마 가져오기 오류", t)
                    Toast.makeText(requireContext(), "테마 가져오기 오류: ${t.message}", Toast.LENGTH_LONG)
                        .show()
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

    private fun setChipGroup() {
        // 초기 버튼 비활성화
        binding.editThemeFinishBt.isEnabled = false

        // Chip 선택 상태 리스너
        val chipCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                val chip = buttonView as Chip
                val chipText = chip.text.toString()

                // Text 처리 로직
                val processedText = when {
                    chipText.contains("전공") -> chipText.replace("/", "및")
                    else -> chipText.replace("/", "").replace(" ", "") // 공백 및 슬래시를 제거
                }

                if (isChecked) {
                    if (processedText !in selectedThemes) { // processedText가 리스트에 없을 때만 추가
                        selectedThemes.add(processedText)
                    }
                } else {
                    selectedThemes.remove(processedText) // 선택 해제 시 리스트에서 제거
                }

                // 선택된 칩이 하나라도 있으면 버튼 활성화
                binding.editThemeFinishBt.isEnabled = selectedThemes.isNotEmpty()

                Log.d("ThemePreferenceFragment", "Selected Themes: $selectedThemes")
            }

        // Chip 그룹의 모든 칩에 리스너 설정
        for (i in 0 until binding.activityChecklistChipGroup.childCount) {
            val chip = binding.activityChecklistChipGroup.getChildAt(i) as? Chip
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }



    private fun postThemesToServer(memberId: Int, themes: List<String>) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val themePreferences = ThemePreferences(themes) // 서버에 보낼 테마 리스트

        service.postThemes(themePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: RetrofitResponse<Void>) {
                if (response.isSuccessful) {
                    Log.d("ThemePreferenceFragment", "Themes POST request success")
                    showCompletionDialog()
                } else {
                    Log.e("ThemePreferenceFragment", "Themes POST request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ThemePreferenceFragment", "Themes POST request failure", t)
            }
        })
    }

    private fun showCompletionDialog() {
        val dialog = ThemeUploadCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyPageFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }

}
