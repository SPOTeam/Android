package com.example.spoteam_android.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCategoryBinding
import com.google.android.material.tabs.TabLayoutMediator


class CategoryFragment : Fragment() {

    lateinit var binding: FragmentCategoryBinding
    private val tabList = arrayListOf("전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        val categoryAdapter = CategoryVPAdapter(this)
        binding.categoryContentVp.adapter = categoryAdapter

        TabLayoutMediator(binding.categoryTl, binding.categoryContentVp) {
                tab, position -> tab.text = tabList[position]
        }.attach()

        val categoryType = arguments?.getInt("categoryType", 0) ?: 0

        binding.categoryContentVp.setCurrentItem(categoryType, false)

        return binding.root
    }
}
