package com.example.spoteam_android.ui.mypage

import StudyApiService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.spoteam_android.FinishedStudyItem
import com.example.spoteam_android.FinishedStudyResponse
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.ReasonApiResponse
import com.example.spoteam_android.RegionApiResponse
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.StudyResponse
import com.example.spoteam_android.ThemeApiResponse
import com.example.spoteam_android.databinding.FragmentMypageBinding
import com.example.spoteam_android.login.LoginApiService
import com.example.spoteam_android.login.StartLoginActivity
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MyPageStudyNumInfo
import com.example.spoteam_android.ui.community.MyPageStudyNumResponse
import com.example.spoteam_android.ui.community.ScrapFragment
import com.example.spoteam_android.ui.interestarea.FinishedStudyApiService
import com.example.spoteam_android.ui.mypage.cancel.CancelSPOTFragment
import com.example.spoteam_android.ui.mypage.rule.CommunityPrivacyPolicyFragment
import com.example.spoteam_android.ui.mypage.rule.CommunityRestrictionsFragment
import com.example.spoteam_android.ui.mypage.rule.CommunityRuleFragment
import com.example.spoteam_android.ui.mypage.rule.CommunityTermsOfUseFragment
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private var memberId: Int = -1
    private lateinit var myStudyAdapter: MyStudyAdapter
    private val studyList = mutableListOf<FinishedStudyItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        setupUI()
        setNickname()
        fetchMyPageInfo()
        setupRecyclerView()
        fetchFinishedStudyData()
        fetchThemes()
        fetchRegions()
        fetchReasons()

        return binding.root
    }

    private fun setupUI() {
        binding.apply {
            tvInProgress.setOnClickListener { navigateToFragment(ParticipatingStudyFragment()) }
            tvInProgressNum.setOnClickListener { navigateToFragment(ParticipatingStudyFragment()) }
            tvRecruiting.setOnClickListener { navigateToFragment(ConsiderAttendanceFragment()) }
            tvRecruitingNum.setOnClickListener { navigateToFragment(ConsiderAttendanceFragment()) }
            tvApplied.setOnClickListener { navigateToFragment(PermissionWaitFragment()) }
            tvAppliedNum.setOnClickListener { navigateToFragment(PermissionWaitFragment()) }
            framelayoutDeleteAccount.setOnClickListener { showConfirmationDialog("회원 탈퇴", "정말로 회원 탈퇴를 진행하시겠습니까? 탈퇴 시 모든 데이터가 삭제됩니다.") { performAccountDeletion() } }
            framelayoutLogout.setOnClickListener {
                val  dialog = LogOutDialog(requireContext()){
                    performLogout()
                }
                dialog.start()
            }
            framelayout1.setOnClickListener { navigateToFragment(ThemePreferenceFragment()) }
            framelayout2.setOnClickListener { navigateToFragment(RegionPreferenceFragment()) }
            framelayout3.setOnClickListener { navigateToFragment(PurposePreferenceFragment()) }

            detailScrapIv.setOnClickListener { navigateToFragment(ScrapFragment()) }

            tvCommunity02.setOnClickListener{navigateToFragment(CommunityRuleFragment())}
            framelayout5.setOnClickListener{navigateToFragment(CommunityRestrictionsFragment())}
            framelayout9.setOnClickListener{navigateToFragment(CommunityPrivacyPolicyFragment())}
            framelayout10.setOnClickListener{navigateToFragment(CommunityTermsOfUseFragment())}

            tvDeleteAccount.setOnClickListener{navigateToFragment(CancelSPOTFragment())}
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupRecyclerView() {
        myStudyAdapter = MyStudyAdapter(studyList)

        val spacingDp = 12 // 오른쪽 마진
        val spacingPx = (spacingDp * Resources.getSystem().displayMetrics.density).toInt()
        binding.recyclerViewMyStudies.addItemDecoration(RightSpaceItemDecoration(spacingPx))

        binding.recyclerViewMyStudies.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = myStudyAdapter
        }
    }


    private fun navigateToFragment(fragment: Fragment) {
        (activity as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun fetchMyPageInfo() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMyPageStudyNum().enqueue(object : Callback<MyPageStudyNumResponse> {
            override fun onResponse(call: Call<MyPageStudyNumResponse>, response: Response<MyPageStudyNumResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.isSuccess == "true") initMyStudyNum(it.result)
                        else showError(it.message)
                    }
                } else {
                    showError("Error Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyPageStudyNumResponse>, t: Throwable) {
                showError("Network Error: ${t.message}")
                Log.e("MyPageFragment", "Failure: ${t.message}", t)
            }
        })
    }

    private fun initMyStudyNum(result: MyPageStudyNumInfo) {
        binding.tvInProgressNum.text = result.ongoingStudies.toString()
        binding.tvAppliedNum.text = result.appliedStudies.toString()
        binding.tvRecruitingNum.text = result.myRecruitingStudies.toString()
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }


    private fun setNickname() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            // SharedPreferences에서 닉네임 가져오기
            val nickname = sharedPreferences.getString("${email}_nickname", null)
            binding.tvNickname.text = nickname ?: "닉네임 없음" // 닉네임이 없는 경우의 기본 값
            binding.tvStudyNickname.text = nickname ?: "닉네임 없음"
            binding.tvEmail.text = email ?: "이메일 없음"

            // 로그인 플랫폼 확인
            val loginPlatform = sharedPreferences.getString("loginPlatform", null)
            val profileImageUrl = when (loginPlatform) {
                "kakao" -> sharedPreferences.getString("${email}_kakaoProfileImageUrl", null)
                "naver" -> sharedPreferences.getString("${email}_naverProfileImageUrl", null)
                else -> null
            }
            binding.tvPhone.text = loginPlatform?.replaceFirstChar { it.uppercaseChar() } ?: "플랫폼 없음"

            // Glide를 사용하여 이미지 로드
            Glide.with(binding.root.context)
                .load(profileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.ivProfile) // 이미지를 로드할 ImageView

        } else {
            binding.tvNickname.text = "이메일 없음" // 이메일이 없는 경우의 기본 값
        }

        // memberId 가져오기
        memberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1

        fetchMyPageInfo()
    }

    private fun performAccountDeletion() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Toast.makeText(requireContext(), "회원탈퇴 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("MyPageFragment", "Account Deletion Failed: ${error.message}")
            } else {
                clearSharedPreferences()
                Toast.makeText(requireContext(), "회원탈퇴 성공", Toast.LENGTH_SHORT).show()
                navigateToLoginScreen()
            }
        }
    }

    private fun performLogout() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val loginPlatform = sharedPreferences.getString("loginPlatform", null)

        when (loginPlatform) { //플랫폼별 로그인 구현. 일반로그인은 아직 구현중
            "kakao" -> logoutFromKakao()
            "naver" -> logoutFromNaver()
            else -> {
                Toast.makeText(requireContext(), "로그인 플랫폼 정보 없음", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logoutFromKakao() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                // 🔸 이미 로그아웃된 상태라면 에러가 나올 수 있음
                Log.w("MyPageFragment", "카카오 로그아웃 실패 또는 이미 로그아웃됨: ${error.message}")
            } else {
                Log.i("MyPageFragment", "카카오 로그아웃 성공")
            }

            com.kakao.sdk.auth.TokenManagerProvider.instance.manager.clear()
            clearSharedPreferences()
            RetrofitInstance.setAuthToken(null)

            Toast.makeText(requireContext(), "로그아웃되었습니다", Toast.LENGTH_SHORT).show()
            navigateToLoginScreen()
        }
    }

    private fun logoutFromNaver() {
        NaverIdLoginSDK.logout()

        clearSharedPreferences()

        RetrofitInstance.setAuthToken(null)

        Toast.makeText(requireContext(), "네이버 로그아웃 성공", Toast.LENGTH_SHORT).show()
        navigateToLoginScreen()
    }

    private fun fetchFinishedStudyData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        val service = RetrofitInstance.retrofit.create(FinishedStudyApiService::class.java)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                service.GetFinshedStudy(0, 5)
                    .enqueue(object : Callback<FinishedStudyResponse> {
                        override fun onResponse(
                            call: Call<FinishedStudyResponse>,
                            response: Response<FinishedStudyResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.result?.studyHistories?.let { studyHistories ->

                                    val content = studyHistories.content
                                    Log.d("fetchFinishedStudyData", "Received data: $content")

                                    if (!content.isNullOrEmpty()) {
                                        requireActivity().runOnUiThread {
                                            val studyItems = content.map {
                                                FinishedStudyItem(
                                                    studyId = it.studyId, // 만약 StudyItem에서 Long이라면 toLong() 해주세요
                                                    title = it.title,
                                                    performance = it.performance,
                                                    createdAt = it.createdAt,
                                                    finishedAt = it.finishedAt
                                                )
                                            }

                                            myStudyAdapter.updateList(studyItems)
                                        }
                                        binding.recyclerViewMyStudies.visibility = View.VISIBLE
                                    }

                                }
                            } else {
                                Toast.makeText(requireContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<FinishedStudyResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchThemes() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)
        service.getThemes().enqueue(object : Callback<ThemeApiResponse> {
            override fun onResponse(call: Call<ThemeApiResponse>, response: Response<ThemeApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val themes = apiResponse.result.themes
                        if (themes.isNotEmpty()) {
                            binding.tvField.text = themes.joinToString(" ")
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
                            if (regions.isNotEmpty()) {
                                val fullRegionNames = regions.joinToString(" ") {
                                    "${it.province} ${it.district} $" + "{it.neighborhood}"
                               }
                                binding.tvRegion.text = fullRegionNames
                            }
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

    private fun fetchReasons() {
        val service = RetrofitInstance.retrofit.create(LoginApiService::class.java)

        service.getReasons().enqueue(object : Callback<ReasonApiResponse> {
            override fun onResponse(call: Call<ReasonApiResponse>, response: Response<ReasonApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.isSuccess) {
                        val reasons = apiResponse.result.reasons // 🔥 API에서 받은 문자열 리스트
                        binding.tvGoal.text = reasons
                            .map { it.replace("_", " ") }
                            .joinToString(" ")
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


    private fun clearSharedPreferences() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(requireActivity(), StartLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { _, _ -> onConfirm() }
            .setNegativeButton("취소", null)
            .show()
    }
}
