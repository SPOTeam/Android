package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.ActivityCommunityContentBinding
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.ENROLL
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.LIVE
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.UPDATE
import com.example.spoteam_android.ui.alert.CheckAppliedStudyFragment

class CommunityContentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCommunityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.community_content_comment_frm, CommunityContentInfoFragment())
            .commitAllowingStateLoss()


    }
}