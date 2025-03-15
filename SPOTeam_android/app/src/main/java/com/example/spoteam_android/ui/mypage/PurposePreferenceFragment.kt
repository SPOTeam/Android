package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyReasons
import com.example.spoteam_android.databinding.FragmentPurposePreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import com.google.android.material.chip.Chip
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurposePreferenceFragment : Fragment() {

    private lateinit var binding: FragmentPurposePreferenceBinding
    private val selectedPurpose = mutableListOf<Int>() // 현재 선택된 목적 ID 리스트
    private val previousSelectedPurpose = mutableListOf<Int>() // 취소 시 복원할 원래 선택된 값

    // 서버의 ID와 칩 ID 매핑
    private val chipMap = mapOf(
        R.id.activity_checklist_studypurpose_chip_habit to 1,
        R.id.activity_checklist_studypurpose_chip_feedback to 2,
        R.id.activity_checklist_studypurpose_chip_network to 3,
        R.id.activity_checklist_studypurpose_chip_license to 4,
        R.id.activity_checklist_studypurpose_chip_contest to 5,
        R.id.activity_checklist_studypurpose_chip_opinion to 6
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPurposePreferenceBinding.inflate(inflater, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                fetchReasons() // 서버에서 선택된 이유 가져오기
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }

        binding.fragmentReasonPreferenceBackBt.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.fragmentPurposePreferenceEditBt.setOnClickListener { enterEditMode() }
        binding.editPurposeCancelBt.setOnClickListener { cancelEditMode() }
        binding.editPurposeFinishBt.setOnClickListener { saveSelectedPurposes() }

        // 초기 상태에서는 칩 비활성화
        setChipEnabled(false)

        return binding.root
    }

    private fun fetchReasons() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getReasons().enqueue(object : Callback<ReasonApiResponse> {
            override fun onResponse(call: Call<ReasonApiResponse>, response: Response<ReasonApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val reasons = apiResponse.result.reasons.mapNotNull { it.toIntOrNull() } // 🔥 String → Int 변환
                        if (reasons.isNotEmpty()) {
                            setChipCheckedState(reasons) // ✅ 가져온 데이터로 칩 체크
                        }
                    } else {
                        Log.e("PurposePreferenceFragment", "이유 가져오기 실패: ${apiResponse?.message}")
                    }
                } else {
                    Log.e("PurposePreferenceFragment", "이유 가져오기 실패: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ReasonApiResponse>, t: Throwable) {
                Log.e("PurposePreferenceFragment", "이유 가져오기 오류", t)
            }
        })
    }


    /** ✅ 서버에서 받은 이유와 UI의 칩 매칭 */
    private fun setChipCheckedState(reasons: List<Int>) {
        selectedPurpose.clear()
        previousSelectedPurpose.clear()

        val chipGroup = binding.flexboxLayout
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.let {
                val chipId = it.id
                val reasonId = chipMap[chipId]

                if (reasonId != null && reasons.contains(reasonId)) {
                    it.isChecked = true
                    selectedPurpose.add(reasonId)
                    previousSelectedPurpose.add(reasonId)
                }
            }
        }
    }

    /** ✅ 칩 활성화/비활성화 설정 */
    private fun setChipEnabled(enabled: Boolean) {
        val chipGroup = binding.flexboxLayout
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.isEnabled = enabled
        }
    }

    /** ✅ 수정 버튼 클릭 시 */
    private fun enterEditMode() {
        binding.buttonLayout.visibility = View.VISIBLE
        binding.fragmentPurposePreferenceEditBt.visibility = View.GONE
        setChipEnabled(true)

        val chipGroup = binding.flexboxLayout
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            chip?.setOnCheckedChangeListener(chipCheckedChangeListener)
        }
    }

    /** ✅ 취소 버튼 클릭 시 (이전 상태로 복원) */
    private fun cancelEditMode() {
        binding.buttonLayout.visibility = View.GONE
        binding.fragmentPurposePreferenceEditBt.visibility = View.VISIBLE
        selectedPurpose.clear()
        selectedPurpose.addAll(previousSelectedPurpose)

        setChipEnabled(false)

        val chipGroup = binding.flexboxLayout
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip
            val chipId = chip?.id
            val reasonId = chipMap[chipId]

            if (reasonId != null) {
                chip?.isChecked = previousSelectedPurpose.contains(reasonId)
            }
        }
    }

    /** ✅ 완료 버튼 클릭 시 (서버로 전송) */
    private fun saveSelectedPurposes() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                postReasonsToServer(selectedPurpose)
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** ✅ 선택한 목표 서버로 전송 */
    private fun postReasonsToServer(selectedPurpose: List<Int>) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        val purposePreferences = StudyReasons(selectedPurpose)

        service.postPurposes(purposePreferences).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("PurposePreferenceFragment", "POST 성공")
                    showCompletionDialog()
                    exitEditMode()
                } else {
                    Log.e("PurposePreferenceFragment", "POST 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("PurposePreferenceFragment", "POST 요청 실패", t)
            }
        })
    }

    /** ✅ 수정 완료 후 모드 종료 */
    private fun exitEditMode() {
        binding.buttonLayout.visibility = View.GONE
        binding.fragmentPurposePreferenceEditBt.visibility = View.VISIBLE
        setChipEnabled(false)
    }

    /** ✅ POST 성공 시 Dialog 표시 */
    private fun showCompletionDialog() {
        val dialog = PurposeUploadComplteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }

    /** ✅ 칩 선택 리스너 */
    private val chipCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val chip = buttonView as Chip
        val chipId = chip.id
        val associatedNumber = chipMap[chipId]

        if (isChecked) {
            associatedNumber?.let { selectedPurpose.add(it) }
        } else {
            associatedNumber?.let { selectedPurpose.remove(it) }
        }

        binding.editPurposeFinishBt.isEnabled = selectedPurpose.isNotEmpty()
    }

}
