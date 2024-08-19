package com.example.spoteam_android.ui.study

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyDetailsResponse
import com.example.spoteam_android.StudyDetailsResult
import com.example.spoteam_android.databinding.FragmentDetailStudyBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투두 리스트")
    private var currentTabPosition: Int = 0
    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailStudyBinding.inflate(inflater, container, false)

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            Log.d("DetailFragment", "Received studyId from ViewModel: $studyId")
            fetchStudyDetails(studyId)
        }


        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")

        binding.fragmentDetailStudyUsernameTv.text = kakaoNickname

        binding.fragmentDetailStudyTl.tabMode = TabLayout.MODE_SCROLLABLE

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        // ViewModel에서 studyId를 옵저빙하고 가져옵니다.
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                // studyId를 DetailStudyVPAdapter에 전달합니다.
                val detailStudyAdapter = DetailStudyVPAdapter(this, studyId)
                binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

                // ViewPager가 미리 모든 탭의 프래그먼트를 생성하고 캐싱하도록 설정
                binding.fragmentDetailStudyVp.offscreenPageLimit = tabList.size

                TabLayoutMediator(binding.fragmentDetailStudyTl, binding.fragmentDetailStudyVp) { tab, position ->
                    tab.text = tabList[position]
                }.attach()

                binding.fragmentDetailStudyTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        currentTabPosition = tab?.position ?: 0
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })

                binding.fragmentDetailStudyVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        currentTabPosition = position
                    }
                })
            } else {
                Log.e("DetailStudyFragment", "studyId is null")
            }
        }
    }



    private fun fetchStudyDetails(studyId: Int) {
        Log.d("DetailStudyFragment","fetchStudyDetails() 호출")
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudyDetails(studyId).enqueue(object : Callback<StudyDetailsResponse> {
            override fun onResponse(call: Call<StudyDetailsResponse>, response: Response<StudyDetailsResponse>) {
                if (response.isSuccessful) {
                    val studyDetailsResponse = response.body()
                    val studyDetails = studyDetailsResponse?.result
                    if (studyDetails != null) {
                        updateUI(studyDetails)
                    } else {
                        Toast.makeText(requireContext(), "Received empty response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch study details: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudyDetailsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun updateUI(studyDetails: StudyDetailsResult) {
        binding.fragmentDetailStudyTitleTv.text = studyDetails.studyName
        binding.fragmentDetailStudyGoalTv.text = studyDetails.goal

        // Member Count
        binding.fragmentDetailStudyMemberTv.text = getString(R.string.member_count_format, studyDetails.memberCount)

        // Heart Count
        binding.fragmentDetailStudyBookmarkTv.text = getString(R.string.heart_count_format, studyDetails.heartCount)

        // Hit Number
        binding.fragmentDetailStudyViewTv.text = getString(R.string.hit_num_format, studyDetails.hitNum)

        // maxpeople 서버 업데이트시 수정예정
        binding.fragmentDetailStudyMemberMaxTv.text = getString(R.string.max_people_format, studyDetails.maxPeople)

        // Study State (Online/Offline)
        binding.fragmentDetailStudyOnlineTv.text = if (studyDetails.isOnline) {
            "온라인"
        } else {
            "오프라인"
        }
        binding.fragmentDetailStudyAgeTv.text = "${studyDetails.maxAge}세 이하"

        // Study Owner Image
        studyViewModel.studyImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            // 이미지 로드
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.fragmentDetailStudyUserIv)
        }

        // Theme Types
        val themes = studyDetails.themes
        val displayedThemes = themes.take(3).joinToString("/") // 최대 2개 항목
        val remainingCount = (themes.size - 2).coerceAtLeast(0) // 2개를 초과한 항목 수
        binding.fragmentDetailStudyChipTv.text = "${displayedThemes}"

//        binding.fragmentDetailStudyChipTv.text = if (remainingCount > 0) {
//            "$displayedThemes/+$remainingCount" // 나머지 항목 수 표시
//        } else {
//            displayedThemes
//        }
    }

    fun getCurrentTabPosition(): Int {
        return currentTabPosition
    }

}
