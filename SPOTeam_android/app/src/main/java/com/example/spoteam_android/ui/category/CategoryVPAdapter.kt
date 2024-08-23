package com.example.spoteam_android.ui.category

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.ui.category.category_tabs.AllCategoryFragment
import com.example.spoteam_android.ui.category.category_tabs.ContestFragment
import com.example.spoteam_android.ui.category.category_tabs.DiscussionFragment
import com.example.spoteam_android.ui.category.category_tabs.FreeStudyFragment
import com.example.spoteam_android.ui.category.category_tabs.JobFragment
import com.example.spoteam_android.ui.category.category_tabs.LanguageFragment
import com.example.spoteam_android.ui.category.category_tabs.LicenseFragment
import com.example.spoteam_android.ui.category.category_tabs.MajorFragment
import com.example.spoteam_android.ui.category.category_tabs.NewsFragment
import com.example.spoteam_android.ui.category.category_tabs.ProjectFragment
import com.example.spoteam_android.ui.category.category_tabs.RestFragment

class CategoryVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 10

    // "전체", "어학", "자격증", "취업", "토론", "시사/뉴스", "자율학습", "프로젝트", "공모전", "전공/진로", "기타"
    override fun createFragment(position: Int): Fragment {
        return when(position){
            //All
            0 -> AllCategoryFragment()
            //어학
            1 -> LanguageFragment()
            //자격증
            2 -> LicenseFragment()
            //취업
            3 -> JobFragment()
            //토론
            4 -> DiscussionFragment()
            //시사/뉴스
            5 -> NewsFragment()
            //자율학습
            6 -> FreeStudyFragment()
            //프로젝트
            7-> ProjectFragment()
            //공모전
            8 -> ContestFragment()
            //전공/진로
            9 -> MajorFragment()
            //기타
            else -> RestFragment()
        }
    }

}