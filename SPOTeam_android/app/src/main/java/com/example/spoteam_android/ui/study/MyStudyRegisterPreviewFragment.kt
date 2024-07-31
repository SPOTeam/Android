package com.example.spoteam_android.ui.study

import StudyViewModel
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.databinding.FragmentMyStudyRegisterPreviewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyStudyRegisterPreviewFragment : Fragment() {

    private lateinit var binding: FragmentMyStudyRegisterPreviewBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")
    private val viewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStudyRegisterPreviewBinding.inflate(inflater, container, false)

        setupViewPagerAndTabs()
        updateUIWithData()
        observeProfileImageUri()

        // 버튼 클릭 이벤트 설정
        binding.fragmentMyStudyRegisterPreviewRegisterBt.setOnClickListener {
            showCompletionDialog()
        }

        return binding.root
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = MyStudyRegisterPreviewVPAdapter(this) // 어댑터를 적절히 설정하세요
        binding.fragmentMyStudyRegisterPreviewVp.adapter = pagerAdapter

        TabLayoutMediator(binding.fragmentMyStudyRegisterPreviewTl, binding.fragmentMyStudyRegisterPreviewVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0)?.select()

        binding.fragmentMyStudyRegisterPreviewTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null && tab.position != 0) {
                    binding.fragmentMyStudyRegisterPreviewTl.selectTab(binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        for (i in 1 until binding.fragmentMyStudyRegisterPreviewTl.tabCount) {
            binding.fragmentMyStudyRegisterPreviewTl.getTabAt(i)?.view?.isEnabled = false
        }
    }

    private fun updateUIWithData() {
        viewModel.studyRequest.value?.let { studyData ->
            binding.fragmentMyStudyRegisterPreviewTitleTv.text = studyData.title
            binding.fragmentMyStudyRegisterPreviewGoalTv.text = studyData.goal
            binding.fragmentMyStudyRegisterPreviewGoalTv.setTextColor(Color.parseColor("#2D2D2D"))
            binding.fragmentMyStudyRegisterPreviewChipTv.text = studyData.themes.joinToString(separator = " / ")
            binding.fragmentMyStudyRegisterPreviewOnlineTv.text = if (studyData.isOnline) "온라인" else "오프라인"
            binding.fragmentMyStudyRegisterPreviewFeeTv.text = if (studyData.fee > 0) "유료" else "무료"
            binding.fragmentMyStudyRegisterPreviewAgeTv.text = "${studyData.minAge}-${studyData.maxAge}세"
        }
    }

    private fun observeProfileImageUri() {
        viewModel.profileImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.fragmentMyStudyRegisterPreviewUserIv.setImageURI(Uri.parse(uri))
            }
        }
    }

    private fun showCompletionDialog() {
        val dialog = StudyRegisterCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }
}
