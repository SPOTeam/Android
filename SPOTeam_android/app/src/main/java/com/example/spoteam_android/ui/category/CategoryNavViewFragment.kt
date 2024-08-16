package com.example.spoteam_android.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCategoryBinding
import com.example.spoteam_android.databinding.FragmentCategoryNavigationviewBinding
import com.google.android.material.tabs.TabLayoutMediator


class CategoryNavViewFragment : Fragment() {

    lateinit var binding: FragmentCategoryNavigationviewBinding
    private val tabList = arrayListOf("전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryNavigationviewBinding.inflate(inflater, container, false)

        return binding.root
    }
}
