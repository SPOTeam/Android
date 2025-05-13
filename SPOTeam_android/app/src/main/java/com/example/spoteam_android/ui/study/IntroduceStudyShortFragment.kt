package com.example.spoteam_android.ui.study

import StudyViewModel
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentIntroduceStudyShortBinding

class IntroduceStudyShortFragment : Fragment() {

    private lateinit var binding: FragmentIntroduceStudyShortBinding
    private val viewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroduceStudyShortBinding.inflate(inflater, container, false)

        updateUIWithData()

        return binding.root
    }


    private fun updateUIWithData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val loginPlatform = sharedPreferences.getString("loginPlatform", null)

        val profileImageUrl = when (loginPlatform) {
            "kakao" -> sharedPreferences.getString("${email}_kakaoProfileImageUrl", null)
            "naver" -> sharedPreferences.getString("${email}_naverProfileImageUrl", null)
            else -> null
        }

        viewModel.studyRequest.value?.let { studyData ->
            binding.fragmentIntroduceStudyShortTv.text = studyData.introduction
            binding.fragmentIntroduceStudyShortTv.setTextColor(Color.parseColor("#2D2D2D"))
        }

        Glide.with(this)
            .load(profileImageUrl)
            .error(R.drawable.ic_preview_profile) // 실패 시 기본 이미지
            .fallback(R.drawable.ic_preview_profile) // null일 때 기본 이미지
            .into(binding.ivProfile)
    }



}
