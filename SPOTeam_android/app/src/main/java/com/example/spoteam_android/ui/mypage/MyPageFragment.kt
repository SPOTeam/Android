package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spoteam_android.MainActivity
import androidx.fragment.app.replace
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMypageBinding
import com.example.spoteam_android.login.StartLoginActivity
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.ContentLikeResponse
import com.example.spoteam_android.ui.community.MyPageStudyNumInfo
import com.example.spoteam_android.ui.community.MyPageStudyNumResponse
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private var memberId : Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
            binding = FragmentMypageBinding.inflate(inflater, container, false)

            setNickname()

            binding.tvInProgress.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, ParticipatingStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }

            binding.tvInProgressNum.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, ParticipatingStudyFragment())
                    .addToBackStack(null)
                    .commit()
            }

            binding.tvMyPage.setOnClickListener{
                parentFragmentManager.popBackStack()
            }

            binding.tvRecruiting.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, ConsiderAttendanceFragment())
                    .addToBackStack(null)
                    .commit()
            }

            binding.tvRecruitingNum.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, ConsiderAttendanceFragment())
                    .addToBackStack(null)
                    .commit()
            }

            binding.tvApplied.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, PermissionWaitFragment())
                    .addToBackStack(null)
                    .commit()
            }

            binding.tvAppliedNum.setOnClickListener{
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, PermissionWaitFragment())
                    .addToBackStack(null)
                    .commit()
            }


            binding.framelayout10.setOnClickListener {
                // 회원 탈퇴 버튼 클릭 시
                performAccountDeletion()
            }

            // 로그아웃 버튼 클릭 시
            binding.framelayout11.setOnClickListener {
                performLogout()
            }

            binding.framelayout1.setOnClickListener {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.main_frm, ThemePreferenceFragment())
                transaction.commit()
            }
            binding.framelayout2.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RegionPreferenceFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }

            binding.framelayout3.setOnClickListener {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.main_frm, PurposePreferenceFragment())
                transaction.commit()
            }

            return binding.root
    }

    private fun fetchMyPageInfo() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getMyPageStudyNum(memberId)
            .enqueue(object : Callback<MyPageStudyNumResponse> {
                override fun onResponse(
                    call: Call<MyPageStudyNumResponse>,
                    response: Response<MyPageStudyNumResponse>
                ) {
                    Log.d("MyPage", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val myPageResponse = response.body()
                        Log.d("MyPage", "responseBody: ${myPageResponse?.isSuccess}")
                        if (myPageResponse?.isSuccess == "true") {
                            initMyStudyNum(myPageResponse.result)
                        } else {
                            showError(myPageResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MyPageStudyNumResponse>, t: Throwable) {
                    Log.e("LikeContent", "Failure: ${t.message}", t)
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
            val nickname = getNicknameFromPreferences(email)
            binding.tvNickname.text = nickname ?: "닉네임 없음"  // 닉네임이 없는 경우의 기본 값

            // SharedPreferences에서 카카오 프로필 이미지 URL 가져오기
            val kakaoProfileImageUrl = sharedPreferences.getString("${email}_kakaoProfileImageUrl", null)

            // Glide를 사용하여 이미지 로드
            Glide.with(binding.root.context)
                .load(kakaoProfileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.ivProfile)  // 이미지를 로드할 ImageView

        } else {
            binding.tvNickname.text = "이메일 없음"  // 이메일이 없는 경우의 기본 값
        }

        // memberId 가져오기
        memberId = if (email != null) sharedPreferences.getInt("${email}_memberId", -1) else -1

        fetchMyPageInfo()
    }


    private fun getNicknameFromPreferences(email: String): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("${email}_nickname", null)
    }

    private fun performAccountDeletion() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                // 탈퇴 실패
                Toast.makeText(requireContext(), "회원탈퇴 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("MyPageFragment", "회원탈퇴 실패: $error")
            } else {
                // 탈퇴 성공
                Toast.makeText(requireContext(), "회원탈퇴 성공", Toast.LENGTH_SHORT).show()

                // SharedPreferences의 사용자 데이터 삭제
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    clear()  // 모든 SharedPreferences 데이터 삭제
                    apply()
                }

                // 로그인 화면으로 이동
                val intent = Intent(requireActivity(), StartLoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // 모든 액티비티 제거 후 새로운 로그인 화면으로 이동
                startActivity(intent)
                requireActivity().finish()  // 현재 액티비티 종료
            }
        }
    }


    private fun performLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                // 로그아웃 실패
                Toast.makeText(requireContext(), "로그아웃 실패: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("MyPageFragment", "로그아웃 실패: $error")
            } else {
                // 로그아웃 성공
                Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()

                // SharedPreferences에서 모든 로그인 관련 정보 삭제
                val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                with(sharedPreferences.edit()) {
                    remove("isLoggedIn")
                    remove("accessToken")
                    remove("memberId")
                    remove("nickname")
                    remove("kakaoProfileImageUrl")
                    remove("currentEmail")
                    apply()
                    }

                // 로그인 화면으로 이동
                val intent = Intent(requireActivity(), StartLoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // 모든 액티비티 제거 후 새로운 로그인 화면으로 이동
                startActivity(intent)
                requireActivity().finish()  // 현재 액티비티 종료
            }
        }
    }

}
