package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.spoteam_android.R
import com.example.spoteam_android.RegionsPreferences
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentTemporaryRegionBinding
import com.example.spoteam_android.login.LoginApiService
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Response




class TemporaryRegionFragment : Fragment() {
    private lateinit var binding: FragmentTemporaryRegionBinding
    private val selectedRegions = mutableListOf<String>()
    private val selectedRegionsCode = mutableListOf<String>() // 선택된 지역 코드 리스트

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTemporaryRegionBinding.inflate(inflater, container, false)

        // 전달된 데이터 받기
        val receivedRegions = arguments?.getStringArrayList("SELECTED_REGIONS")
        val receivedRegionsCode = arguments?.getStringArrayList("SELECTED_CODES")

        // Regions 로그 출력
        if (receivedRegions != null) {
            selectedRegions.addAll(receivedRegions)
            Log.d("TemporaryRegionFragment", "Received Regions: $receivedRegions")
            displaySelectedRegions()
        }

        // Regions Code 로그 출력
        if (receivedRegionsCode != null) {
            selectedRegionsCode.addAll(receivedRegionsCode)
            Log.d("TemporaryRegionFragment", "Received RegionsCode: $receivedRegionsCode")
        }

        updateFinishButtonState() // 초기 버튼 상태 설정

        binding.editReasonCancelBt.setOnClickListener {
            val fragment = MyPageLocationFragment()
            val bundle = Bundle().apply {
                putStringArrayList("SELECTED_REGIONS", ArrayList(selectedRegions))
                putStringArrayList("SELECTED_CODES", ArrayList(selectedRegionsCode))
            }
            fragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null) // 백스택에 추가하여 뒤로가기 시 원래 Fragment로 복귀 가능
                .commit()
        }

        binding.editReasonFinishBt.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("currentEmail", null)
            val memberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1

            if (memberId == -1) {
                Log.e("TemporaryRegionFragment", "Member ID not found in SharedPreferences")
                return@setOnClickListener
            }

            // POST 요청을 위한 데이터 준비
            val regionsPreferences = RegionsPreferences(selectedRegionsCode)

            // Retrofit 인스턴스 생성 및 API 서비스 호출
            val apiService = RetrofitInstance.retrofit.create(LoginApiService::class.java)

            // Retrofit POST 요청 보내기
            apiService.postRegions(regionsPreferences).enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("TemporaryRegionFragment", "POST 성공")
                        showCompletionDialog()
                        // 성공 시 필요한 동작 수행
                    } else {
                        Log.e("TemporaryRegionFragment", "POST 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("TemporaryRegionFragment", "POST 실패: ${t.message}")
                }
            })
        }

        return binding.root
    }

    private fun updateFinishButtonState() {
        binding.editReasonFinishBt.isEnabled = selectedRegions.isNotEmpty()
    }

    private fun displaySelectedRegions() {
        val chipContainer = binding.chipContainer
        chipContainer.removeAllViews() // 기존 Chips를 모두 제거

        for (regionText in selectedRegions) {
            val chip = createLocationChip(regionText)
            val chipGroup = ChipGroup(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                addView(chip)
            }
            chipContainer.addView(chipGroup)
        }
    }

    private fun createLocationChip(address: String): Chip {
        return Chip(requireContext()).apply {
            text = address
            setTextColor(resources.getColor(R.color.active_blue, null))
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    R.style.CustomChipCloseStyle
                )
            )
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                val chipContainer = binding.chipContainer
                chipContainer.removeView(this.parent as View)

                // 삭제할 주소의 인덱스를 찾음
                val index = selectedRegions.indexOf(address)
                if (index != -1) {
                    // 주소와 코드 리스트에서 각각 해당 항목을 삭제
                    selectedRegions.removeAt(index)
                    selectedRegionsCode.removeAt(index)
                }

                updateFinishButtonState() // 버튼 상태 업데이트
                if (selectedRegions.isEmpty()) {
                    binding.activityChecklistLocationCl.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showCompletionDialog() {
        val dialog = RegionUploadCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }
}
