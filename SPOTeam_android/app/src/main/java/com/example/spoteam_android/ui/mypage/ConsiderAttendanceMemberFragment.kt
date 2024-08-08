package com.example.spoteam_android.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.ProfileTemperatureItem
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentConsiderAttendanceMemberBinding

class ConsiderAttendanceMemberFragment : Fragment() {

    private lateinit var binding: FragmentConsiderAttendanceMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentConsiderAttendanceMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 데이터 리스트 생성
        val profileList = arrayListOf(
            ProfileTemperatureItem(R.drawable.ic_host_profile, "사용자1", "36.6°C"),
            ProfileTemperatureItem(R.drawable.ic_host_profile, "사용자2", "37.0°C"),
            ProfileTemperatureItem(R.drawable.ic_host_profile, "사용자3", "37.5°C"),
            ProfileTemperatureItem(R.drawable.ic_host_profile, "사용자4", "38.0°C")
            // Add more items here
        )

        // 프로필 어댑터 설정
        val profileAdapter = ConsiderAttendanceMemberVPAdapter(profileList)

        // 프로필 RecyclerView 설정
        binding.fragmentConsiderAttendanceMemberRv.apply {
            adapter = profileAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }
}
