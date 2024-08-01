package com.example.spoteam_android.ui.study

import StudyViewModel
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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
        viewModel.studyRequest.value?.let { studyData ->
            binding.fragmentIntroduceStudyShortTv.text = studyData.introduction
            binding.fragmentIntroduceStudyShortTv.setTextColor(Color.parseColor("#2D2D2D"))

        }


    }

}
