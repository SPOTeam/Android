package com.example.spoteam_android.ui.study

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.ui.home.HomeFragment

class DetailStudyVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5


    override fun createFragment(position: Int): Fragment {
        return when(position){
            //홈
            0 -> HomeFragment()
            //캘린더
            1 -> HomeFragment()
            //게시판
            2 -> HomeFragment()
            //갤러리
            3 -> HomeFragment()
            //투표
            else -> HomeFragment()
        }
    }

}