package com.example.spoteam_android.presentation.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.presentation.community.communityContent.AllFragment
import com.example.spoteam_android.presentation.community.communityContent.CounselingFragment
import com.example.spoteam_android.presentation.community.communityContent.FreeTalkFragment
import com.example.spoteam_android.presentation.community.communityContent.JobSeekingTalkFragment
import com.example.spoteam_android.presentation.community.communityContent.NotificationFragment
import com.example.spoteam_android.presentation.community.communityContent.PassingReviewFragment
import com.example.spoteam_android.presentation.community.communityContent.ShareInfoFragment

class CommunityCategoryVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return when(position){
            //All
            0 -> AllFragment()
            //PassingReview
            1 -> PassingReviewFragment()
            //ShareInfo
            2 -> ShareInfoFragment()
            //Counseling
            3 -> CounselingFragment()
            //JobSeekingTalk
            4 -> JobSeekingTalkFragment()
            //FreeTalk
            5 -> FreeTalkFragment()
            //Notification
            else -> NotificationFragment()
        }
    }

}