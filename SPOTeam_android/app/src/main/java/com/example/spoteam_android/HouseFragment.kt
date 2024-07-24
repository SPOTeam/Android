package com.example.spoteam_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentHouseBinding
import com.example.spoteam_android.ui.community.CommunityHomeFragment

class HouseFragment : Fragment() {

    private lateinit var binding: FragmentHouseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseBinding.inflate(inflater, container, false)

        initRecyclerView()

        binding.imgbtnBoard.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityHomeFragment())
                .commitAllowingStateLoss()
            (activity as? MainActivity)?.isOnCommunityHome(CommunityHomeFragment())
        }

        return binding.root
    }

    private fun initRecyclerView() {

        val itemList = ArrayList<BoardItem>()
        val itemList2 = ArrayList<BoardItem>()

        itemList.add(BoardItem("피아노 스터디","스터디 목표",10,1,1,600))
        itemList.add(BoardItem("태권도 스터디","스터디 목표",10,2,1,500))
        itemList.add(BoardItem("보컬 스터디","스터디 목표",10,3,1,400))

        itemList2.add(BoardItem("피아노 스터디","스터디 목표",10,1,1,600))
        itemList2.add(BoardItem("태권도 스터디","스터디 목표",10,2,1,500))
        itemList2.add(BoardItem("보컬 스터디","스터디 목표",10,3,1,400))

        val boardAdapter = BoardAdapter(itemList)
        val boardAdapter2 = BoardAdapter(itemList2)

        binding.rvBoard.adapter = boardAdapter
        binding.rvBoard2.adapter = boardAdapter2

        binding.rvBoard.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBoard2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
}
