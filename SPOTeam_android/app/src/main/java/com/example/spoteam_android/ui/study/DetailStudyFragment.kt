package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.spoteam_android.R
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.databinding.FragmentDetailStudyBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")
    private var currentTabPosition: Int = 0
    private lateinit var studyItem: StudyItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailStudyBinding.inflate(inflater, container, false)

        // Arguments에서 StudyItem 가져오기
        arguments?.let {
            studyItem = it.getParcelable("studyItem") ?: StudyItem(
                title = "",
                introduction = "",
                memberCount = 0,
                heartCount = 0,
                hitNum = 0,
                maxPeople = 0,
                studyState = "",
                themeTypes = emptyList(),
                regions = emptyList()
            )
        }

        setupViews()

        return binding.root
    }

    private fun setupViews() {
        val detailStudyAdapter = DetailStudyVPAdapter(this)
        binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

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

        // ViewPager2 페이지 변경 리스너 설정
        binding.fragmentDetailStudyVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentTabPosition = position
            }
        })

        // StudyItem의 데이터로 UI 업데이트
        updateUI()
    }

    private fun updateUI() {
        binding.fragmentDetailStudyTitleTv.text = studyItem.title
        binding.fragmentDetailStudyGoalTv.text = studyItem.introduction

        // Member Count
        binding.fragmentDetailStudyMemberTv.text = getString(R.string.member_count_format, studyItem.memberCount)

        // Heart Count
        binding.fragmentDetailStudyBookmarkTv.text = getString(R.string.heart_count_format, studyItem.heartCount)

        // Hit Number
        binding.fragmentDetailStudyViewTv.text = getString(R.string.hit_num_format, studyItem.hitNum)

        // Max People
        binding.fragmentDetailStudyMemberMaxTv.text = getString(R.string.max_people_format, studyItem.maxPeople)

        // Study State
        binding.fragmentDetailStudyOnlineTv.text = if (studyItem.regions != null && studyItem.regions.isNotEmpty()) {
            "오프라인"
        } else {
            "온라인"
        }
            //maxage구현시 추가 예정
//        binding.fragmentDetailStudyAgeTv.text = getString

        // Theme Types
        val displayedThemes = studyItem.themeTypes.take(2).joinToString("/") // 최대 2개 항목
        val remainingCount = (studyItem.themeTypes.size - 2).coerceAtLeast(0) // 2개를 초과한 항목 수

        binding.fragmentDetailStudyChipTv.text = if (remainingCount > 0) {
            "$displayedThemes/+" + remainingCount // 나머지 항목 수 표시
        } else {
            displayedThemes
        }


        // binding.fragmentDetailStudyImageView.load(studyItem.imageUrl)
    }

    fun getCurrentTabPosition(): Int {
        return currentTabPosition
    }

    companion object {
        fun newInstance(studyItem: StudyItem): DetailStudyFragment {
            return DetailStudyFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("studyItem", studyItem)
                }
            }
        }
    }
}
