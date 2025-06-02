package com.example.spoteam_android.ui.community

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCommunityBinding
import com.example.spoteam_android.presentation.mypage.scrap.ScrapVPAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScrapFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding
    private val tabList = arrayListOf("전체", "합격후기", "정보공유", "고민상담", "취준토크", "자유토크", "SPOT공지")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        val scrapAdapter = ScrapVPAdapter(this)
        binding.categoryContentVp.adapter = scrapAdapter

        TabLayoutMediator(binding.categoryTl, binding.categoryContentVp) { tab, position ->
            val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_text, null)
            val textView = tabView.findViewById<TextView>(R.id.tabText)
            textView.text = tabList[position]
            textView.setTextColor(Color.BLACK) // 초기엔 모두 검정색
            tab.customView = tabView
        }.attach()

        binding.categoryTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(R.id.tabText)
                textView?.setTextColor(Color.BLACK) // 선택돼도 검정
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val textView = tab?.customView?.findViewById<TextView>(R.id.tabText)
                textView?.setTextColor(Color.BLACK) // 해제돼도 검정
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Bundle로부터 특정 조건을 체크
        val showSpotNotice = arguments?.getBoolean("showSpotNotice", false) ?: false
        if (showSpotNotice) {
            val spotNoticeTabPosition = tabList.indexOf("SPOT공지")
            if (spotNoticeTabPosition != -1) {
                binding.categoryContentVp.setCurrentItem(spotNoticeTabPosition, true)
            }
        }

        binding.communityTitle.text = "스크랩한 글"

        binding.communityPrevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}
