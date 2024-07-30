package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.databinding.FragmentMyStudyRegisterPreviewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyStudyRegisterPreviewFragment : Fragment() {

    lateinit var binding: FragmentMyStudyRegisterPreviewBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMyStudyRegisterPreviewBinding.inflate(inflater, container, false)

        // ViewPager2에 어댑터 설정
        val pagerAdapter = MyStudyRegisterPreviewVPAdapter(this) // 어댑터를 적절히 설정하세요
        binding.fragmentMyStudyRegisterPreviewVp.adapter = pagerAdapter

        // ViewPager와 TabLayout을 연결
        TabLayoutMediator(binding.fragmentMyStudyRegisterPreviewTl, binding.fragmentMyStudyRegisterPreviewVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        // 기본으로 "홈" 탭을 선택
        binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0)?.select()

        // 탭 선택 리스너 추가하여 특정 탭을 클릭할 수 없도록 설정
        binding.fragmentMyStudyRegisterPreviewTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // "홈" 탭을 제외한 다른 탭이 선택되었을 때 다시 "홈" 탭으로 이동
                if (tab != null && tab.position != 0) {
                    binding.fragmentMyStudyRegisterPreviewTl.selectTab(binding.fragmentMyStudyRegisterPreviewTl.getTabAt(0))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 아무 작업도 하지 않음
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 아무 작업도 하지 않음
            }
        })

        // 나머지 탭 비활성화
        for (i in 1 until binding.fragmentMyStudyRegisterPreviewTl.tabCount) {
            binding.fragmentMyStudyRegisterPreviewTl.getTabAt(i)?.view?.isEnabled = false
        }

        return binding.root
    }
}

