package com.example.spoteam_android.ui.study.quiz

import StudyViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentToMakeQuizBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HostMakeQuizFirstFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentToMakeQuizBinding
    val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentToMakeQuizBinding.inflate(inflater, container, false)

        binding.startAttendanceTv.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.child_fragment, HostMakeQuizFragment())
                .commitAllowingStateLoss()
        }

        return binding.root
    }
}