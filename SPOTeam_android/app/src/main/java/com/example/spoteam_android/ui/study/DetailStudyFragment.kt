package com.example.spoteam_android.ui.study

import ApplyStudyDialog
import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.IsAppliedResponse
import com.example.spoteam_android.MemberResponse
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
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투두리스트")
    private var currentTabPosition: Int = 0
    val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailStudyBinding.inflate(inflater, container, false)

        val desiredTabPosition = arguments?.getInt("tab_position", 0) ?: 0
        Log.d("DetailStudyFragment","{$desiredTabPosition}")
        currentTabPosition = desiredTabPosition
        val startDateTime = arguments?.getString("startDateTime")

        val OkDialogStudyId=  arguments?.getInt("FromOKToDetailStudy")

        Log.d("OKBUNDLE", OkDialogStudyId.toString())

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            fetchStudyDetails(studyId)
            fetchStudyMembers(studyId)
            fetchIsApplied(studyId)
        }


        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        val kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")

        binding.fragmentDetailStudyUsernameTv.text = kakaoNickname

        binding.fragmentDetailStudyTl.tabMode = TabLayout.MODE_SCROLLABLE

        binding.fragmentDetailStudyVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val fragment = childFragmentManager.findFragmentByTag("f$position")
                if (fragment != null) {
                    updateViewPagerHeight(binding.fragmentDetailStudyVp, fragment)
                }
            }
        })


        setupViews(startDateTime)

        binding.fragmentDetailStudyPreviousBt.setOnClickListener {
            // 현재 Fragment를 백스택에서 제거하고 이전 Fragment로 돌아갑니다.
            parentFragmentManager.popBackStack()
        }

        binding.fragmentDetailStudyHomeRegisterBt.setOnClickListener {
            showCompletionDialog()
        }

        // ViewPager2와 TabLayout 초기화
        setupViews(startDateTime)

        return binding.root
    }

    private fun setupViews(startDateTime: String?) {
        // ViewModel에서 studyId를 옵저빙하고 가져옵니다.

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                // studyId를 DetailStudyVPAdapter에 전달합니다.
                val detailStudyAdapter = DetailStudyVPAdapter(this, studyId,startDateTime)
                binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

                TabLayoutMediator(binding.fragmentDetailStudyTl, binding.fragmentDetailStudyVp) { tab, position ->
                    tab.text = tabList[position]
                }.attach()

                //캘린더 일정 추가 작업 이후 다시 캘린더 tab으로 돌아오게 함
                binding.fragmentDetailStudyVp.setCurrentItem(currentTabPosition, false)

                // ViewPager가 미리 모든 탭의 프래그먼트를 생성하고 캐싱하도록 설정
                binding.fragmentDetailStudyVp.offscreenPageLimit = tabList.size
                binding.fragmentDetailStudyVp.isUserInputEnabled = false

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
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudyDetails(studyId).enqueue(object : Callback<StudyDetailsResponse> {
            override fun onResponse(call: Call<StudyDetailsResponse>, response: Response<StudyDetailsResponse>) {
                if (response.isSuccessful) {
                    val studyDetailsResponse = response.body()
                    val studyDetails = studyDetailsResponse?.result
                    if (studyDetails != null) {

                        studyViewModel.setMaxPeople(studyDetails.maxPeople)
                        studyViewModel.setMemberCount(studyDetails.memberCount)
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
    private fun fetchStudyMembers(studyId: Int){
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentMemberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)
        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse>{
            override fun onResponse(
                call: Call<MemberResponse>,
                response: Response<MemberResponse>
            ) {
                if(response.isSuccessful){
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result.members ?: emptyList()

                        val isMember = members.any{it.memberId == currentMemberId}

                        if (isMember){
                            binding.fragmentDetailStudyFl.visibility = View.VISIBLE
                        }else{
                            binding.fragmentDetailStudyFl.visibility = View. GONE
                            binding.fragmentDetailStudyLl.setPadding(0, 50, 0, 50)
                        }


                        // maxPeople과 memberCount 값을 가져오기 위해 ViewModel을 옵저빙
                        val maxPeople = studyViewModel.maxPeople.value
                        val memberCount = studyViewModel.memberCount.value

                        // 현재 사용자가 멤버인지 확인하거나, 최대 인원을 초과한 경우 버튼 숨김 처리
                        val shouldHideButton = isMember || (maxPeople != null && memberCount != null && memberCount >= maxPeople)
                        binding.fragmentDetailStudyHomeRegisterBt.visibility = if (shouldHideButton) View.GONE else View.VISIBLE
                    }
                } else{
                    Toast.makeText(requireContext(), "Failed to fetch study members", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
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
                .error(R.drawable.sample_img) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.sample_img) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentDetailStudyUserIv)
        }

        // Theme Types
        val themes = studyDetails.themes
        val displayedThemes = themes.take(3).joinToString("/") // 최대 2개 항목
        val remainingCount = (themes.size - 2).coerceAtLeast(0) // 2개를 초과한 항목 수
        studyViewModel.studyOwner.value = studyDetails.studyOwner.ownerName


        binding.fragmentDetailStudyChipTv.text = "${displayedThemes}"

//        binding.fragmentDetailStudyChipTv.text = if (remainingCount > 0) {
//            "$displayedThemes/+$remainingCount" // 나머지 항목 수 표시
//        } else {
//            displayedThemes
//        }
    }

    private fun fetchIsApplied(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        api.getIsApplied(studyId).enqueue(object : Callback<IsAppliedResponse> {
            override fun onResponse(call: Call<IsAppliedResponse>, response: Response<IsAppliedResponse>) {
                if (response.isSuccessful) {
                    val isApplied = response.body()?.result?.applied ?: false
                    updateRegisterButton(isApplied)
                } else {

                    Toast.makeText(requireContext(), "Failed to check application status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<IsAppliedResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRegisterButton(isApplied: Boolean) {
        val button = binding.fragmentDetailStudyHomeRegisterBt


        val backgroundDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE  // 사각형 형태
            cornerRadius = 16f  // 둥근 모서리 반경 (픽셀 단위)
        }

        if (isApplied) {
            button.isEnabled = false
            button.text = "신청완료"

            backgroundDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
            backgroundDrawable.setStroke(3, ContextCompat.getColor(requireContext(), R.color.button_disabled_text)) // 테두리 설정

            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_disabled_text))
        } else {
            button.isEnabled = true
            button.text = "신청하기"

            backgroundDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.white))
            backgroundDrawable.setStroke(3, ContextCompat.getColor(requireContext(), R.color.button_enabled_text)) // 테두리 설정

            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.MainColor_01))
        }

        button.background = backgroundDrawable
    }


    fun getCurrentTabPosition(): Int {
        return currentTabPosition
    }

    private fun showCompletionDialog() {
        val dialog = ApplyStudyDialog(requireContext(), this)
        dialog.start {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm, HouseFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun updateViewPagerHeight(viewPager: ViewPager2, currentFragment: Fragment) {
        viewPager.post {
            val view = currentFragment.view
            view?.post {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
                val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                view.measure(widthSpec, heightSpec)

                val newHeight = view.measuredHeight
                if (viewPager.layoutParams.height != newHeight) {
                    viewPager.layoutParams.height = newHeight
                    viewPager.requestLayout()
                }
            }
        }
    }


}
