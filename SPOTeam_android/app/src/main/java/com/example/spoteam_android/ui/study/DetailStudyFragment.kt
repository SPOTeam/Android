package com.example.spoteam_android.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentDetailStudyBinding
import com.google.android.material.tabs.TabLayoutMediator


class DetailStudyFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyBinding
    private val tabList = arrayListOf("홈", "캘린더", "게시판", "갤러리", "투표")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailStudyBinding.inflate(inflater,container,false)

        val detailStudyAdapter = DetailStudyVPAdapter(this)
        binding.fragmentDetailStudyVp.adapter = detailStudyAdapter

        TabLayoutMediator(binding.fragmentDetailStudyTl, binding.fragmentDetailStudyVp) {
                tab, position -> tab.text = tabList[position]
        }.attach()

        return binding.root
    }


}