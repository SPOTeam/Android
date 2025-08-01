package com.umcspot.android.ui.study

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.umcspot.android.ui.home.HomeFragment

class MyStudyRegisterPreviewVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 5 // 탭의 개수
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IntroduceStudyShortFragment()
            1 -> HomeFragment()
            2 -> HomeFragment()
            3 -> HomeFragment()
            4 -> HomeFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}