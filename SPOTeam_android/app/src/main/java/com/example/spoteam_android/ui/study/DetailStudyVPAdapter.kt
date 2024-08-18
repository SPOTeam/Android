package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.provider.Settings.Global.putInt
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.todolist.TodoListFragment
import com.example.spoteam_android.ui.calendar.CalendarFragment
import com.example.spoteam_android.ui.home.HomeFragment

class DetailStudyVPAdapter(fragment: Fragment, private val studyId: Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 5


    override fun createFragment(position: Int): Fragment {
        return when(position){
            //홈
            0 -> DetailStudyHomeFragment()
            //캘린더
            1 -> CalendarFragment().apply {
                arguments = Bundle().apply {
                    putInt("studyId", studyId)
                }
            }
            //게시판
            2 -> MyStudyCommunityFragment()
            //갤러리
            3 -> MyStudyGalleryFragment()
            //투두리스트
            else -> TodoListFragment()
        }
    }

}