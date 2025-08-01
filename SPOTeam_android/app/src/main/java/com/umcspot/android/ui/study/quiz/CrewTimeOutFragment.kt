package com.umcspot.android.ui.study.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umcspot.android.databinding.FragmentTimeOutQuizBinding


class CrewTimeOutFragment : Fragment() {

    private lateinit var binding: FragmentTimeOutQuizBinding
    private var wrongCount = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentTimeOutQuizBinding.inflate(inflater, container, false)
        
        return binding.root
    }

}