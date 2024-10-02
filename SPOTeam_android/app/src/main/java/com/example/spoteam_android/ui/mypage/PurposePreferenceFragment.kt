package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.databinding.FragmentPurposePreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import com.example.spoteam_android.ui.study.IntroduceStudyFragment
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurposePreferenceFragment : Fragment() {

    private lateinit var binding: FragmentPurposePreferenceBinding
    private val selectedPurpose = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPurposePreferenceBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                fetchReasons(memberId) // GET 요청으로 이유 리스트 가져오기
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }

        binding.fragmentReasonPreferenceBackBt.setOnClickListener {
            goToPreviusFragment()
        }

        // 이유 수정 버튼 클릭 시
        binding.reasonEditIv.setOnClickListener {
            binding.buttonLayout.visibility = View.VISIBLE
            binding.fragmentReasonPreferenceLinearLayout.visibility = View.GONE
            binding.flexboxLayout.visibility = View.VISIBLE
            setChipGroup() // Chip 그룹 설정
        }

        binding.editReasonCancelBt.setOnClickListener {
            binding.buttonLayout.visibility = View.GONE
            binding.fragmentReasonPreferenceLinearLayout.visibility = View.VISIBLE
            binding.flexboxLayout.visibility = View.GONE
        }


        // POST 요청을 보내는 버튼 클릭 시
        binding.editReasonFinishBt.setOnClickListener {
            if (email != null) {
                val memberId = sharedPreferences.getInt("${email}_memberId", -1)
                if (memberId != -1) {
                    postReasonsToServer(memberId, selectedPurpose) // POST 요청으로 이유 전송
                } else {
                    Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }

    private fun fetchReasons(memberId: Int) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getReasons(memberId).enqueue(object : Callback<ReasonApiResponse> {
            override fun onResponse(
                call: Call<ReasonApiResponse>,
                response: Response<ReasonApiResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val reasons = apiResponse.result.reasons // 서버에서 받아오는 이유 리스트
                        if (reasons.isNotEmpty()) {
                            displayReasons(reasons) // UI에 이유 리스트 표시
                        }
                    } else {
                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                        Log.e("PurposePreferenceFragment", "이유 가져오기 실패: $errorMessage")
                        Toast.makeText(
                            requireContext(),
                            "이유 가져오기 실패: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                    Log.e("PurposePreferenceFragment", "이유 가져오기 실패: $errorMessage")
                    Toast.makeText(requireContext(), "이유 가져오기 실패: $errorMessage", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<ReasonApiResponse>, t: Throwable) {
                Log.e("PurposePreferenceFragment", "이유 가져오기 오류", t)
                Toast.makeText(requireContext(), "이유 가져오기 오류: ${t.message}", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun displayReasons(reasons: List<String>) {
        val linearLayout = binding.fragmentReasonPreferenceLinearLayout
        linearLayout.removeAllViews() // 기존 TextView를 모두 제거

        for (reason in reasons) {
            // 텍스트 변환 처리
            val processedText = if (reason.contains("_")) {
                reason.replace("_", " ")
            } else {
                reason
            }

            val textView = TextView(requireContext()).apply {
                text = processedText // 서버에서 받은 String 데이터를 그대로 사용
                textSize = 16f // 16sp
                setPadding(50, 15, 50, 15) // padding: left, top, right, bottom
                setTextColor(resources.getColor(R.color.custom_chip_text, null))
                setBackgroundResource(R.drawable.theme_selected_corner) // 커스텀 배경 적용

                // 여기서 layout_width를 wrap_content로 설정
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 15
                    topMargin = 30
                }
                layoutParams = params
            }

            linearLayout.addView(textView)
        }
    }


    private fun setChipGroup() {
        // 초기 버튼 비활성화
        binding.editReasonFinishBt.isEnabled = false

        val chipMap = mapOf(
            R.id.activity_checklist_studypurpose_chip_language to 1,
            R.id.activity_checklist_studypurpose_chip_license to 2,
            R.id.activity_checklist_studypurpose_chip_job to 3,
            R.id.activity_checklist_studypurpose_chip_discussion to 4,
            R.id.activity_checklist_studypurpose_chip_news to 5
        )

        // chip 선택 상태 리스너
        val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val chip = buttonView as Chip
            val chipId = chip.id
            val associatedNumber = chipMap[chipId]

            if (isChecked) {
                associatedNumber?.let { selectedPurpose.add(it) }
            } else {
                associatedNumber?.let { selectedPurpose.remove(it) }
            }

            // 버튼 활성화 상태 업데이트
            val isAnyChipChecked = selectedPurpose.isNotEmpty()
            binding.editReasonFinishBt.isEnabled = isAnyChipChecked
        }

        // FlexboxLayout의 각 칩에 리스너 설정
        for (i in 0 until binding.flexboxLayout.childCount) {
            val chip = binding.flexboxLayout.getChildAt(i) as? Chip
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

    private fun postReasonsToServer(memberId: Int, selectedPurpose: List<Int>) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val purposePreferences = StudyReasons(selectedPurpose)

        service.postPurposes(memberId, purposePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PurposePreferenceFragment", "Reasons POST request success")
                    showCompletionDialog()
                } else {
                    Log.e("PurposePreferenceFragment", "Reasons POST request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("PurposePreferenceFragment", "Reasons POST request failure", t)
            }
        })
    }

    private fun showCompletionDialog() {
        val dialog = PurposeUploadComplteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyPageFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }
}
