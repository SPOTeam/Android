package com.example.spoteam_android.presentation.study

import ApplyStudyDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentDetailStudyBinding
import com.example.spoteam_android.presentation.category.CategoryFragment_1
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "일정", "커뮤니티", "갤러리", "투두쉐어링")
    private var currentTabPosition: Int = 0
    private var startDateTime: String? = null

    // 접근 제한자 수정
    internal val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTabPosition = arguments?.getInt("tab_position", 0) ?: 0
        startDateTime = arguments?.getString("startDateTime")
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

        studyViewModel.loadNickname()
        setupViews()
        setupViewPager()
        setupStudyObservers()
        setProfileImageUri()
        tabMargin()
    }

    private fun setupViews() {
        studyViewModel.nickname.observe(viewLifecycleOwner) {
            binding.fragmentDetailStudyUsernameTv.text = "${it}님"
        }

        studyViewModel.studyRequest.observe(viewLifecycleOwner) {
            binding.fragmentDetailStudyTitleTv.text = it?.title ?: ""
            binding.fragmentDetailStudyGoalTv.text = it?.goal ?: ""
        }


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

    private fun setProfileImageUri() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val loginPlatform = sharedPreferences.getString("loginPlatform", null)
            val profileImageUrl = if (!email.isNullOrEmpty() && !loginPlatform.isNullOrEmpty()) {
                sharedPreferences.getString("${email}_${loginPlatform}ProfileImageUrl", null)
            } else {
                null
            }
            Glide.with(binding.root.context)
                .load(profileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo) // URL이 잘못되었거나 404일 경우 기본 이미지 사용
                .fallback(R.drawable.fragment_calendar_spot_logo) // URL이 null일 경우 기본 이미지 사용
                .into(binding.fragmentDetailStudyUserIv)

        }
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
            studyViewModel.fetchStudyDetail(studyId)
            studyViewModel.fetchStudyMembers(studyId, onResult = {})
            studyViewModel.fetchIsApplied(studyId, onResult = { isApplied ->
                updateRegisterButton(isApplied)
            })
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
