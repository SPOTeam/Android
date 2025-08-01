package com.umcspot.android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.ThemeApiResponse
import com.umcspot.android.ThemePreferences
import com.umcspot.android.databinding.FragmentThemePreferenceBinding
import com.umcspot.android.login.LoginApiService
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class ThemePreferenceFragment : Fragment() {

    private lateinit var binding: FragmentThemePreferenceBinding
    private val selectedThemes = mutableListOf<String>() // 현재 선택된 테마 리스트
    private val previousSelectedThemes = mutableListOf<String>() // 취소 시 복원할 원래 테마 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentThemePreferenceBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                fetchThemes()
            } else {
                Toast.makeText(requireContext(), "멤버를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "이메일이 없습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.fragmentThemePreferenceEditBt.setOnClickListener { enterEditMode() }
        binding.editThemeFinishBt.setOnClickListener { saveSelectedThemes() }
        binding.editThemeCancelBt.setOnClickListener { cancelEditMode() }
        binding.fragmentThemePreferenceBackBt.setOnClickListener { parentFragmentManager.popBackStack() }

        //맨 처음 기본 화면에서는 칩이 비활성화 상태
        setChipEnabled(false)

        return binding.root
    }

    // 테마 가져오기
    private fun fetchThemes() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getThemes().enqueue(object : Callback<ThemeApiResponse> {
            override fun onResponse(call: Call<ThemeApiResponse>, response: RetrofitResponse<ThemeApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val themes = apiResponse.result.themes
                        if (themes.isNotEmpty()) {
                            setChipCheckedState(themes) //테마와 UI 비교 매칭
                        }
                    }
                } else {
                    Log.e("ThemePreferenceFragment", "테마 가져오기 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ThemeApiResponse>, t: Throwable) {
                Log.e("ThemePreferenceFragment", "테마 가져오기 오류", t)
            }
        })
    }

    private fun setChipCheckedState(themes: List<String>) {
        selectedThemes.clear()
        previousSelectedThemes.clear()

        val chipGroup = binding.fragmentRegisterStudyChipgroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.let {
                val normalizedChipText = normalizeText(it.text.toString()) //ui칩 텍스트 변환
                val normalizedServerTextList = themes.map { theme -> normalizeServerText(theme) }//서버 변환

                if (normalizedServerTextList.contains(normalizedChipText)) { //비교
                    it.isChecked = true
                    selectedThemes.add(it.text.toString())
                    previousSelectedThemes.add(it.text.toString()) // 선택한 값 저장
                }
            }
        }
    }



    // 칩 활성화 및 비활성화
    private fun setChipEnabled(enabled: Boolean) {
        val chipGroup = binding.fragmentRegisterStudyChipgroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.isEnabled = enabled
        }
    }
    // 칩 선택 이벤트
    private val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val chip = buttonView as Chip
        if (isChecked) selectedThemes.add(chip.text.toString()) else selectedThemes.remove(chip.text.toString())
        binding.editThemeFinishBt.isEnabled = selectedThemes.isNotEmpty()
    }

    // 수정 버튼 클릭시 발생 이벤트
    private fun enterEditMode() {
        binding.buttonLayout.visibility = View.VISIBLE // 취소 & 완료 버튼 보이기
        binding.fragmentThemePreferenceEditBt.visibility = View.GONE // 수정 버튼 숨기기

        setChipEnabled(true) // 수정 버튼 클릭시에는 칩 선택 활성화

        val chipGroup = binding.fragmentRegisterStudyChipgroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

    // 취소 버튼 클릭하면 이미 선택했던 것들 clear 및 기존상태 복구
    private fun cancelEditMode() {
        binding.buttonLayout.visibility = View.GONE // 취소 & 완료 버튼 숨기기
        binding.fragmentThemePreferenceEditBt.visibility = View.VISIBLE // 수정 버튼 보이기

        selectedThemes.clear()
        selectedThemes.addAll(previousSelectedThemes) //기존 상태 복구

        setChipEnabled(false) //수정 취소 시 Chip 비활성화

        val chipGroup = binding.fragmentRegisterStudyChipgroup
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.isChecked = previousSelectedThemes.contains(chip?.text.toString())

        }
    }

    // 완료 버튼을 클릭하면 서버로 테마 전송
    private fun saveSelectedThemes() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                postThemesToServer(selectedThemes)
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postThemesToServer(themes: List<String>) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        // 서버가 요구하는 대로 정규식 변경
        val processedThemes = themes.map { theme ->
            when {
                theme.contains("전공") -> theme.replace("/", "및")  // ✅ "전공/진로학습" → "전공 및 진로학습"
                else -> theme.replace("/", "").replace(" ", "") // ✅ "/" 제거, 공백 제거
            }
        }

        val themePreferences = ThemePreferences(processedThemes)

        service.postThemes(themePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: RetrofitResponse<Void>) {
                if (response.isSuccessful) {
                    Log.d("ThemePreferenceFragment", "Themes POST request success")
                    showCompletionDialog() // ✅ Dialog 표시
                    exitEditMode()
                } else {
                    Log.e("ThemePreferenceFragment", "Themes POST request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ThemePreferenceFragment", "Themes POST request failure", t)
            }
        })
    }


    private fun exitEditMode() {
        binding.buttonLayout.visibility = View.GONE
        binding.fragmentThemePreferenceEditBt.visibility = View.VISIBLE
        setChipEnabled(false)
    }

    private fun showCompletionDialog() {
        val dialog = ThemeUploadCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }


    //ui에서 선택하는 칩
    private fun normalizeText(text: String): String {
        return text
            .replace(" ", "")
            .replace("및", "/")
    }

    //서버에서 전송받은 정규식을 ui에 다시 적용하기
    private fun normalizeServerText(text: String): String {
        return text
            .replace("전공및진로학습", "전공/진로학습")
            .replace("시사뉴스", "시사/뉴스")
            .replace("자율학습", "자율 학습")
            .replace(" ", "")
    }



}


