package com.example.spoteam_android.presentation.study.register.introduce

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
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel

class IntroduceStudyShortFragment : Fragment() {

    private lateinit var binding: FragmentIntroduceStudyShortBinding
    private val viewModel: StudyRegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroduceStudyShortBinding.inflate(inflater, container, false)

        updateUIWithData()

        return binding.root
    }

    private fun updateUIWithData() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        if (email != null) {
            val nickname = sharedPreferences.getString("${email}_nickname", null)
            binding.profileNickname.text="${nickname}"

            val loginPlatform = sharedPreferences.getString("loginPlatform", null)
            val profileImageUrl = if (!email.isNullOrEmpty() && !loginPlatform.isNullOrEmpty()) {
                sharedPreferences.getString("${email}_${loginPlatform}ProfileImageUrl", null)
            } else {
                null
            }

            viewModel.studyRequest.value?.let { studyData ->
                binding.fragmentIntroduceStudyShortTv.text = studyData.introduction
                binding.fragmentIntroduceStudyShortTv.setTextColor(Color.parseColor("#2D2D2D"))
            }

            Glide.with(binding.root.context)
                .load(profileImageUrl)
                .error(R.drawable.fragment_calendar_spot_logo)
                .fallback(R.drawable.fragment_calendar_spot_logo)
                .into(binding.fragmentDetailStudyHomeHostuserIv)

        }
    }




}
