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
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.Region
import com.example.spoteam_android.RegionApiResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentRegionPreferenceBinding
import com.example.spoteam_android.login.LoginApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegionPreferenceFragment : Fragment() {
    private lateinit var binding: FragmentRegionPreferenceBinding
    private val selectedRegions = mutableListOf<String>()
    private val selectedCodes = mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentRegionPreferenceBinding.inflate(inflater,container,false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)
            if (memberId != -1) {
                fetchRegions(memberId) // GET 요청으로 이유 리스트 가져오기
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }

//         이유 수정 버튼 클릭 시
        binding.regionEditIv.setOnClickListener {
            val fragment = TemporaryRegionFragment()
            val bundle = Bundle().apply {
                putStringArrayList("SELECTED_REGIONS", ArrayList(selectedRegions))
                putStringArrayList("SELECTED_CODES", ArrayList(selectedCodes)) // 코드 리스트도 추가
                Log.d("Region", "Regions: $selectedRegions, Codes: $selectedCodes")
            }
            fragment.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.editReasonCancelBt.setOnClickListener {
            goToLocationFragment()
        }
        binding.fragmentRegionPreferenceBackBt.setOnClickListener {
            goToPreviusFragment()
        }


        return binding.root
    }

    private fun fetchRegions(memberId: Int) {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getRegion(memberId).enqueue(object : Callback<RegionApiResponse> {
            override fun onResponse(
                call: Call<RegionApiResponse>,
                response: Response<RegionApiResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val regions = apiResponse.result.regions // 서버에서 받아오는 지역 리스트
                        if (regions.isNotEmpty()) {
                            displayRegions(regions) // UI에 지역 리스트 표시
                        }
                    } else {
                        val errorMessage = apiResponse?.message ?: "알 수 없는 오류 발생"
                        Log.e("RegionPreferenceFragment", "지역 가져오기 실패: $errorMessage")
                        Toast.makeText(
                            requireContext(),
                            "지역 가져오기 실패: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "응답 실패"
                    Log.e("RegionPreferenceFragment", "지역 가져오기 실패: $errorMessage")
                    Toast.makeText(requireContext(), "지역 가져오기 실패: $errorMessage", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<RegionApiResponse>, t: Throwable) {
                Log.e("RegionPreferenceFragment", "네트워크 오류: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun displayRegions(regions: List<Region>) {
        val linearLayout = binding.fragmentRegionPreferenceLinearLayout
        linearLayout.removeAllViews() // 기존 TextView를 모두 제거
        selectedRegions.clear() // 새로운 데이터로 채우기 전에 리스트를 초기화
        selectedCodes.clear() // 코드 리스트도 초기화

        for (region in regions) {
            val regionText = "${region.province} ${region.district} ${region.neighborhood}"
            val regionCode = "${region.code}" // region.code를 String으로 변환

            // `selectedRegions`와 `selectedCodes` 리스트에 지역 정보와 코드 추가
            selectedRegions.add(regionText)
            selectedCodes.add(regionCode)

            val textView = TextView(requireContext()).apply {
                text = regionText // 서버에서 받은 지역 데이터를 그대로 사용
                textSize = 16f // 16sp
                setPadding(50, 15, 50, 15) // 패딩 설정
                setTextColor(resources.getColor(R.color.custom_chip_text, null))
                setBackgroundResource(R.drawable.theme_selected_corner) // 커스텀 배경 적용

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


    private fun goToLocationFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyPageLocationFragment())
        transaction.commit()
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyPageFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }

}


