package com.spot.android.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.spot.android.MainActivity
import com.spot.android.R
import com.spot.android.databinding.FragmentCategoryBinding
import com.spot.android.search.SearchFragment
import com.spot.android.ui.alert.AlertFragment
import com.spot.android.ui.home.HomeFragment


class CategoryFragment : Fragment() {

    lateinit var binding: FragmentCategoryBinding
    private val tabList = arrayListOf("전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

//        val categoryAdapter = CategoryVPAdapter(this)
//        binding.categoryContentVp.adapter = categoryAdapter

//        TabLayoutMediator(binding.categoryTl, binding.categoryContentVp) { tab, position ->
//            val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_text, null)
//            val textView = tabView.findViewById<TextView>(R.id.tabText)
//            textView.text = tabList[position]
//            textView.setTextColor(Color.BLACK) // 초기엔 모두 검정색
//            tab.customView = tabView
//        }.attach()

        val tabLayout = binding.categoryTl
        val tabStrip = tabLayout.getChildAt(0) as ViewGroup

        for (i in 0 until tabStrip.childCount) {
            val tabView = tabStrip.getChildAt(i)
            val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.marginEnd = 15   // 원하는 간격 (예: 20dp)
            layoutParams.marginStart = 15
            tabView.layoutParams = layoutParams
            tabView.requestLayout()
        }

        binding.icFind.setOnClickListener{
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(SearchFragment())
        }

        binding.icAlarm.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
            (context as MainActivity).isOnCommunityHome(HomeFragment())
        }

        val categoryType = arguments?.getInt("categoryType", 0) ?: 0

//        binding.categoryContentVp.setCurrentItem(categoryType, false)

        return binding.root
    }
}
