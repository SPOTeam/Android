package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCommunityBinding
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment() {

    lateinit var binding: FragmentCommunityBinding
    private val tabList = arrayListOf("전체", "합격후기", "정보공유", "고민상담", "취준토크", "자유토크", "SPOT공지")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        val communityCategoryAdapter = CommunityCategoryVPAdapter(this)
        binding.categoryContentVp.adapter = communityCategoryAdapter

        TabLayoutMediator(binding.categoryTl, binding.categoryContentVp) {
                tab, position -> tab.text = tabList[position]
        }.attach()

        // Bundle로부터 특정 조건을 체크
        val showSpotNotice = arguments?.getBoolean("showSpotNotice", false) ?: false
        if (showSpotNotice) {
            val spotNoticeTabPosition = tabList.indexOf("SPOT공지")
            if (spotNoticeTabPosition != -1) {
                binding.categoryContentVp.setCurrentItem(spotNoticeTabPosition, true)
            }
        }

        binding.communityPrevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}
