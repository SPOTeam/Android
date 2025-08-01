package com.spot.android.ui.study

import ApplyStudyDialog
import StudyApiService
import StudyViewModel
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.spot.android.*
import com.spot.android.databinding.FragmentDetailStudyBinding
import com.spot.android.ui.category.CategoryFragment_1
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "일정", "커뮤니티", "갤러리", "투두쉐어링")
    private var currentTabPosition: Int = 0
    private var startDateTime: String? = null
    private var kakaoNickname: String? = null

    val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTabPosition = arguments?.getInt("tab_position", 0) ?: 0
        startDateTime = arguments?.getString("startDateTime")

        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)
        kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupViewPager()
        setupStudyObservers()
        tabMargin()
    }

    private fun setupViews() {
        binding.fragmentDetailStudyUsernameTv.text = "$kakaoNickname" + "님"

        binding.fragmentDetailStudyPreviousBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.fragmentDetailStudyHomeRegisterBt.setOnClickListener {
            showCompletionDialog()
        }

        binding.fragmentDetailStudyTl.tabMode = TabLayout.MODE_SCROLLABLE

        binding.fragmentDetailStudyVp.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val fragment = childFragmentManager.findFragmentByTag("f$position")
                fragment?.let { updateViewPagerHeight(binding.fragmentDetailStudyVp, it) }
            }
        })
    }

    private fun setupViewPager() {
        studyViewModel.studyId.value?.let { studyId ->
            val detailStudyAdapter = DetailStudyVPAdapter(this, studyId, startDateTime)
            binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

            TabLayoutMediator(
                binding.fragmentDetailStudyTl,
                binding.fragmentDetailStudyVp
            ) { tab, position ->
                tab.text = tabList[position]
            }.attach()

            binding.fragmentDetailStudyVp.setCurrentItem(currentTabPosition, false)
            binding.fragmentDetailStudyVp.offscreenPageLimit = tabList.size
            binding.fragmentDetailStudyVp.isUserInputEnabled = false

            binding.fragmentDetailStudyTl.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    currentTabPosition = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun setupStudyObservers() {
        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            fetchStudyDetails(studyId)
            fetchStudyMembers(studyId)
            fetchIsApplied(studyId)
        }
    }

    private fun fetchStudyDetails(studyId: Int) {
        RetrofitInstance.retrofit.create(StudyApiService::class.java)
            .getStudyDetails(studyId)
            .enqueue(object : Callback<StudyDetailsResponse> {
                override fun onResponse(
                    call: Call<StudyDetailsResponse>,
                    response: Response<StudyDetailsResponse>
                ) {
                    response.body()?.result?.let {
                        studyViewModel.setMaxPeople(it.maxPeople)
                        studyViewModel.setMemberCount(it.memberCount)
                        updateUI(it)
                    }
                }

                override fun onFailure(call: Call<StudyDetailsResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun fetchStudyMembers(studyId: Int) {
        val sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentMemberId = sharedPreferences.getInt(
            "${sharedPreferences.getString("currentEmail", "")}_memberId",
            -1
        )

        RetrofitInstance.retrofit.create(StudyApiService::class.java)
            .getStudyMembers(studyId)
            .enqueue(object : Callback<MemberResponse> {
                override fun onResponse(
                    call: Call<MemberResponse>,
                    response: Response<MemberResponse>
                ) {
                    val members = response.body()?.result?.members ?: emptyList()
                    val isMember = members.any { it.memberId == currentMemberId }

                    binding.fragmentDetailStudyFl.visibility =
                        if (isMember) View.VISIBLE else View.GONE
                    if (!isMember) binding.fragmentDetailStudyLl.setPadding(0, 50, 0, 50)

                    val shouldHideButton = isMember || (studyViewModel.maxPeople.value != null &&
                            studyViewModel.memberCount.value != null &&
                            studyViewModel.memberCount.value!! >= studyViewModel.maxPeople.value!!)
                    binding.fragmentDetailStudyHomeRegisterBt.visibility =
                        if (shouldHideButton) View.GONE else View.VISIBLE
                }

                override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun fetchIsApplied(studyId: Int) {
        RetrofitInstance.retrofit.create(StudyApiService::class.java)
            .getIsApplied(studyId)
            .enqueue(object : Callback<IsAppliedResponse> {
                override fun onResponse(
                    call: Call<IsAppliedResponse>,
                    response: Response<IsAppliedResponse>
                ) {
                    val isApplied = response.body()?.result?.applied ?: false
                    updateRegisterButton(isApplied)
                }

                override fun onFailure(call: Call<IsAppliedResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun updateUI(details: StudyDetailsResult) {
        with(binding) {
            fragmentDetailStudyTitleTv.text = details.studyName
            fragmentDetailStudyGoalTv.text = details.goal
            fragmentDetailStudyMemberTv.text =
                getString(R.string.member_count_format, details.memberCount)
            fragmentDetailStudyBookmarkTv.text =
                getString(R.string.heart_count_format, details.heartCount)
            fragmentDetailStudyViewTv.text = getString(
                R.string.hit_num_format,
                if (details.hitNum > 999) "999+" else details.hitNum.toString()
            )
            fragmentDetailStudyMemberMaxTv.text =
                getString(R.string.max_people_format, details.maxPeople)
            fragmentDetailStudyOnlineTv.text = if (details.isOnline) "온라인" else "오프라인"
            fragmentDetailStudyFeeTv.text = if (details.fee > 0) "유료" else "무료"
            fragmentDetailStudyAgeTv.text = "${details.maxAge}세 이하"
            fragmentDetailStudyChipTv.text = details.themes.take(3).joinToString("/")

            studyViewModel.setStudyOwner(details.studyOwner.ownerName)
            val sharedPreferences =
                requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            val email = sharedPreferences.getString("currentEmail", null)
            val loginPlatform = sharedPreferences.getString("loginPlatform", null)

            val profileImageUrl = when (loginPlatform) {
                "kakao" -> sharedPreferences.getString("${email}_kakaoProfileImageUrl", null)
                "naver" -> sharedPreferences.getString("${email}_naverProfileImageUrl", null)
                else -> R.drawable.fragment_calendar_spot_logo
            }

            Glide.with(this@DetailStudyFragment)
                .load(profileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.fragmentDetailStudyUserIv)
        }
    }

    private fun updateRegisterButton(isApplied: Boolean) {
        val button = binding.fragmentDetailStudyHomeRegisterBt
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(ContextCompat.getColor(requireContext(), R.color.white))
            setStroke(
                3,
                ContextCompat.getColor(
                    requireContext(),
                    if (isApplied) R.color.button_disabled_text else R.color.button_enabled_text
                )
            )
        }

        button.apply {
            isEnabled = !isApplied
            text = if (isApplied) "신청완료" else "신청하기"
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isApplied) R.color.button_disabled_text else R.color.MainColor_01
                )
            )
            background = drawable
        }
    }

    private fun showCompletionDialog() {
        val dialog = ApplyStudyDialog(requireContext(), this)
        dialog.start {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CategoryFragment_1())
                .commit()
        }
    }

    private fun updateViewPagerHeight(viewPager: ViewPager2, currentFragment: Fragment) {
        viewPager.post {
            val view = currentFragment.view
            view?.post {
                val widthSpec =
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
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

    private fun tabMargin() {
        val tabLayout = binding.fragmentDetailStudyTl
        val tabStrip = tabLayout.getChildAt(0) as ViewGroup

        for (i in 0 until tabStrip.childCount) {
            val tab = tabStrip.getChildAt(i)
            val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginEnd = 3
            layoutParams.marginStart = 0
            tab.layoutParams = layoutParams
            tab.requestLayout()
        }
    }
}
