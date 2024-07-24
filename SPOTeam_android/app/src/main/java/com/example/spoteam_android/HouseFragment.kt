package com.example.spoteam_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.spoteam_android.SearchFragment

class HouseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_house, container, false)
        val icFindButton: ImageView = view.findViewById(R.id.ic_find)
        icFindButton.setOnClickListener {
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(SearchFragment())
        }
        val icAlarmButton: ImageView = view.findViewById(R.id.ic_alarm)
        icAlarmButton.setOnClickListener {
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(InterestFragment())
        }
        val spoticon: ImageView = view.findViewById(R.id.ic_spot_logo)
        spoticon.setOnClickListener {
            // MainActivity의 switchFragment 메서드를 호출하여 SearchFragment로 전환
            (activity as MainActivity).switchFragment(InterestFilterFragment())
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv_board = view.findViewById<RecyclerView>(R.id.rv_board)
        val rv_board2 = view.findViewById<RecyclerView>(R.id.rv_board2)


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

        boardAdapter.notifyDataSetChanged()
        boardAdapter2.notifyDataSetChanged()

        rv_board.adapter = boardAdapter
        rv_board2.adapter = boardAdapter2

        rv_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv_board2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }



}
