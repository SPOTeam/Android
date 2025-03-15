package com.example.spoteam_android.ui.mypage

import StudyApiService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
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
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.StudyResponse
import com.example.spoteam_android.databinding.FragmentMypageBinding
import com.example.spoteam_android.login.StartLoginActivity
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MyPageStudyNumInfo
import com.example.spoteam_android.ui.community.MyPageStudyNumResponse
import com.example.spoteam_android.ui.community.ScrapFragment
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
    private val studyList = mutableListOf<StudyItem>()
    private val studyApiService: StudyApiService by lazy { RetrofitInstance.retrofit.create(StudyApiService::class.java) }

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
        fetchStudyData()

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
            framelayoutVersionApp.setOnClickListener { performNaverLogout() }
            framelayoutDeleteAccount.setOnClickListener { showConfirmationDialog("회원 탈퇴", "정말로 회원 탈퇴를 진행하시겠습니까? 탈퇴 시 모든 데이터가 삭제됩니다.") { performAccountDeletion() } }
            framelayoutLogout.setOnClickListener { performLogout() }
            framelayout1.setOnClickListener { navigateToFragment(ThemePreferenceFragment()) }
            framelayout2.setOnClickListener { navigateToFragment(RegionPreferenceFragment()) }
            framelayout3.setOnClickListener { navigateToFragment(PurposePreferenceFragment()) }

            tvScrab.setOnClickListener { navigateToFragment(ScrapFragment()) }
            tvCountScrab.setOnClickListener { navigateToFragment(ScrapFragment()) }

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
        binding.recyclerViewMyStudies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myStudyAdapter
        }
        binding.recyclerViewMyStudies.setOnTouchListener { _, _ -> true }
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
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Toast.makeText(requireContext(), "로그아웃 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("MyPageFragment", "Logout Failed: ${error.message}")
            } else {
                clearSharedPreferences()
                Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                navigateToLoginScreen()
            }
        }
    }

    private fun fetchStudyData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val memberId = sharedPreferences.getInt("${email}_memberId", -1)

            if (memberId != -1) {
                studyApiService.getStudies(0, 5)
                    .enqueue(object : Callback<StudyResponse> {
                        override fun onResponse(
                            call: Call<StudyResponse>,
                            response: Response<StudyResponse>
                        ) {
                            if (response.isSuccessful) {
                                response.body()?.result?.let { result ->

                                    val content = result.content
                                    Log.d("fetchStudyData", "Received data: $content") // 추가된 로그
                                    if (!content.isNullOrEmpty()) {
                                        requireActivity().runOnUiThread {
                                            myStudyAdapter.updateList(content.map {
                                                StudyItem(
                                                    studyId = it.studyId,
                                                    title = it.title,
                                                    goal = it.goal,
                                                    introduction = it.introduction,
                                                    memberCount = it.memberCount,
                                                    heartCount = it.heartCount,
                                                    hitNum = it.hitNum,
                                                    maxPeople = it.maxPeople,
                                                    studyState = it.studyState,
                                                    themeTypes = it.themeTypes,
                                                    regions = it.regions,
                                                    imageUrl = it.imageUrl,
                                                    liked = it.liked
                                                )
                                            })
                                        }
                                        binding.recyclerViewMyStudies.visibility = View.VISIBLE

                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<StudyResponse>, t: Throwable) {
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





    private fun performNaverLogout() {
        NaverIdLoginSDK.logout()
        clearSharedPreferences()
        Toast.makeText(requireContext(), "네이버 로그아웃 성공", Toast.LENGTH_SHORT).show()
        navigateToLoginScreen()
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
        requireActivity().finish()
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
