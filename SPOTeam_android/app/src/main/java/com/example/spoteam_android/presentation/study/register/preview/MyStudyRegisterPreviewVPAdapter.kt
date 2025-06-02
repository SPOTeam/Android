package com.example.spoteam_android.presentation.study.register.preview

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.presentation.study.register.introduce.IntroduceStudyFragment
import com.example.spoteam_android.presentation.study.register.introduce.IntroduceStudyShortFragment

class MyStudyRegisterPreviewVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 5 // 탭의 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroduceStudyShortFragment()
            1 -> IntroduceStudyFragment()
            2 -> IntroduceStudyFragment()
            3 -> IntroduceStudyFragment()
            4 -> IntroduceStudyFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}