package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentDetailStudyBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")
    private var currentTabPosition: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailStudyBinding.inflate(inflater,container,false)

        val detailStudyAdapter = DetailStudyVPAdapter(this)
        binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

        TabLayoutMediator(binding.fragmentDetailStudyTl, binding.fragmentDetailStudyVp) {
                tab, position -> tab.text = tabList[position]
        }.attach()

        binding.fragmentDetailStudyTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
               currentTabPosition = tab?.position ?:0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        // ViewPager2 페이지 변경 리스너 설정
        binding.fragmentDetailStudyVp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentTabPosition = position
            }
        })

        return binding.root
    }

    fun getCurrentTabPosition(): Int {
        return currentTabPosition
    }

}