package com.umcspot.android.ui.study.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umcspot.android.databinding.FragmentCrewCorrectQuizBinding


class CrewCorrectQuizFragment : Fragment() {

    private lateinit var binding: FragmentCrewCorrectQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCrewCorrectQuizBinding.inflate(inflater, container, false)

        return binding.root
    }

}