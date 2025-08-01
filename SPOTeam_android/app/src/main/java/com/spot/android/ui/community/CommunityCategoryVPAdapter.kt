package com.spot.android.ui.community

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.spot.android.ui.community.communityContent.AllFragment
//import com.example.spoteam_android.ui.community.communityContent.AllFragment
import com.spot.android.ui.community.communityContent.CounselingFragment
import com.spot.android.ui.community.communityContent.FreeTalkFragment
import com.spot.android.ui.community.communityContent.JobSeekingTalkFragment
import com.spot.android.ui.community.communityContent.PassingReviewFragment
import com.spot.android.ui.community.communityContent.ShareInfoFragment
import com.spot.android.ui.community.communityContent.NotificationFragment

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