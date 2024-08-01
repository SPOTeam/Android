package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentDetailStudyHomeBinding
import com.example.spoteam_android.DetailStudyHomeAdapter
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.R
import com.example.spoteam_android.SceduleItem

class DetailStudyHomeFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStudyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스케줄 데이터 리스트 생성
        val scheduleList = ArrayList<SceduleItem>().apply {
            add(SceduleItem("D-3", "5/21 (화)", "모의고사 풀이", "5/21 7:00pm", "투썸 플레이스 강남점"))
            add(SceduleItem("D-2", "5/22 (수)", "토익 스피킹", "5/22 5:00pm", "셀렉티드니스 강남점"))
            add(SceduleItem("D-1", "5/23 (목)", "면접 스터디", "5/23 3:00pm", "스타벅스 강남점"))
        }

        // 스케줄 어댑터 설정
        val detailStudyHomeAdapter = DetailStudyHomeAdapter(scheduleList)

        // 스케줄 RecyclerView 설정
        binding.fragmentDetailStudyHomeScheduleRv.apply {
            adapter = detailStudyHomeAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        // 프로필 데이터 리스트 생성
        val profileList = ArrayList<ProfileItem>().apply {
            add(ProfileItem(R.drawable.ic_host_profile, "사용자1"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자2"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자3"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자4"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자5"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자6"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자7"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자8"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자9"))
            add(ProfileItem(R.drawable.ic_host_profile, "사용자10"))

            // 추가 프로필 데이터 추가
        }

        // 프로필 어댑터 설정
        val profileAdapter = DetailStudyHomeProfileAdapter(profileList)

        // 프로필 RecyclerView 설정
        binding.fragmentDetailStudyHomeProfileRv.apply {
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}
