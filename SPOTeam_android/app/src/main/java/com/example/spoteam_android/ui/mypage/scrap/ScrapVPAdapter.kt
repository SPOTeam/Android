package com.example.spoteam_android.ui.mypage.scrap

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spoteam_android.ui.community.communityContent.AllScrapFragment
//import com.example.spoteam_android.ui.community.communityContent.AllFragment
import com.example.spoteam_android.ui.community.communityContent.CounselingScrapFragment
import com.example.spoteam_android.ui.community.communityContent.FreeTalkScrapFragment
import com.example.spoteam_android.ui.community.communityContent.JobSeekingTalkScrapFragment
import com.example.spoteam_android.ui.community.communityContent.PassingReviewScrapFragment
import com.example.spoteam_android.ui.community.communityContent.ShareInfoScrapFragment
import com.example.spoteam_android.ui.community.communityContent.NotificationScrapFragment

class ScrapVPAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 7

    override fun createFragment(position: Int): Fragment {
        return when(position){
            //All
            0 -> AllScrapFragment()
            //PassingReview
            1 -> PassingReviewScrapFragment()
            //ShareInfo
            2 -> ShareInfoScrapFragment()
            //Counseling
            3 -> CounselingScrapFragment()
            //JobSeekingTalk
            4 -> JobSeekingTalkScrapFragment()
            //FreeTalk
            5 -> FreeTalkScrapFragment()
            //Notification
            else -> NotificationScrapFragment()
        }
    }

}