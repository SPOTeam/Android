package com.example.tempproject.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tempproject.CommunityActivity
import com.example.tempproject.MainActivity
import com.example.tempproject.R
import com.example.tempproject.databinding.FragmentDashboardBinding
import com.example.tempproject.databinding.FragmentHomeBinding

class DashboardFragment : Fragment() {

    lateinit var binding: FragmentDashboardBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.textDashboardTv.setOnClickListener() {
            startActivity(Intent(activity, CommunityActivity::class.java))
        }

        return binding.root
    }

}