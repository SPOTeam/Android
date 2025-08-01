package com.umcspot.android.ui.mypage

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
import androidx.core.content.res.ResourcesCompat
import com.umcspot.android.R
import com.umcspot.android.Region
import com.umcspot.android.RegionApiResponse
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentRegionPreferenceBinding
import com.umcspot.android.login.LoginApiService
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
                fetchRegions() // GET 요청으로 이유 리스트 가져오기
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }

        binding.checklistspotLocationEditBt.setOnClickListener {
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

        binding.checklistspotLocationPlusBt.setOnClickListener {
            goToLocationFragment()
        }
        binding.fragmentRegionPreferenceBackBt.setOnClickListener {
            goToPreviusFragment()
        }


        return binding.root
    }

    private fun fetchRegions() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getRegion().enqueue(object : Callback<RegionApiResponse> {
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
        linearLayout.removeAllViews()
        selectedRegions.clear()
        selectedCodes.clear()

        for (region in regions) {
            val regionText = "${region.province} ${region.district} ${region.neighborhood}"
            val regionCode = "${region.code}"

            selectedRegions.add(regionText)
            selectedCodes.add(regionCode)

            val textView = TextView(requireContext()).apply {
                text = regionText
                textSize = 14.4f
                setPadding(30, 35, 50, 35) // 패딩 설정
                setTextColor(resources.getColor(R.color.b500, null))
                setBackgroundResource(R.drawable.button_background) // 커스텀 배경 적용
                typeface = ResourcesCompat.getFont(requireContext(), R.font.suit_semi_bold)


                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 20
                    marginEnd = 20
                    topMargin = 25
                }
                layoutParams = params
            }

            linearLayout.addView(textView)
        }
    }
    private fun createStyledRegionView(regionText: String): TextView {
        return TextView(requireContext()).apply {
            text = regionText
            textSize = 14.4f
            setPadding(30, 35, 50, 35)
            setTextColor(resources.getColor(R.color.b500, null))
            setBackgroundResource(R.drawable.button_background)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.suit_semi_bold)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 20
                marginEnd = 20
                topMargin = 25
            }
            layoutParams = params
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


