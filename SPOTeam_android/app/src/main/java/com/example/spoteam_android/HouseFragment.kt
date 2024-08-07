package com.example.spoteam_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentHouseBinding
import com.example.spoteam_android.ui.calendar.CalendarAddEventFragment
import com.example.spoteam_android.ui.alert.AlertFragment
import com.example.spoteam_android.ui.community.CommunityHomeFragment

class HouseFragment : Fragment() {

    lateinit var binding: FragmentHouseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentHouseBinding.inflate(inflater, container, false)

        val icFindButton: ImageView = binding.root.findViewById(R.id.ic_find)
        icFindButton.setOnClickListener {
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(SearchFragment())
        }

        val icAlarmButton: ImageView = binding.root.findViewById(R.id.ic_alarm)
        icAlarmButton.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlertFragment()).addToBackStack(null)
                .commitAllowingStateLoss()
        }

        val register = StudyRegisterPopupFragment(requireContext())

        val spoticon: ImageView = binding.root.findViewById(R.id.ic_spot_logo)
        spoticon.setOnClickListener {
            //스터디 참여하기 팝업으로 이동
            (activity as MainActivity).switchFragment(RecruitingStudyFragment())
        }

        val txintereststudy: TextView = binding.root.findViewById(R.id.tx_interest_study)
        txintereststudy.setOnClickListener {
            //스터디 참여하기 팝업으로 이동
            (activity as MainActivity).switchFragment(InterestFragment())
        }

        val icgointerest: ImageView = binding.root.findViewById(R.id.ic_go_interest)
        icgointerest.setOnClickListener {
            //스터디 참여하기 팝업으로 이동
            (activity as MainActivity).switchFragment(InterestFragment())
        }


        binding.imgbtnBoard.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityHomeFragment())
                .commitAllowingStateLoss()
            (activity as? MainActivity)?.isOnCommunityHome(CommunityHomeFragment())
        }

        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        val itemList = ArrayList<BoardItem>()
        val itemList2 = ArrayList<BoardItem>()

        itemList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
        itemList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
        itemList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))

        itemList2.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
        itemList2.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
        itemList2.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))

        val boardAdapter = BoardAdapter(itemList)
        val boardAdapter2 = BoardAdapter(itemList2)

        boardAdapter.notifyDataSetChanged()
        boardAdapter2.notifyDataSetChanged()

        binding.rvBoard.adapter = boardAdapter
        binding.rvBoard2.adapter = boardAdapter2

        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBoard2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}
