package com.example.tempproject


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tempproject.databinding.FragmentCommunityCategoryBinding
import com.example.tempproject.databinding.FragmentNotificationsBinding
import com.google.android.material.tabs.TabLayoutMediator

class CommunityCategoryFragment : Fragment() {

    lateinit var binding: FragmentCommunityCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryBinding.inflate(inflater, container, false)

//        val storageAdapter = CommunityContentVPAdapter(this)
//        binding.storageContentVp.adapter = storageAdapter
//
//        TabLayoutMediator(binding.categoryTl, )

        return binding.root
    }
}