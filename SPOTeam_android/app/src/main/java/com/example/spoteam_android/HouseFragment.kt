package com.example.spoteam_android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HouseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_house, container, false)
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




//        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            var selectedFragment: Fragment? = null
//            when (item.itemId) {
//                R.id.navigation_home -> {
//                    selectedFragment = HouseFragment()
//                }
//                R.id.navigation_category -> {
//                    selectedFragment = CategoryFragment()
//                }
//                R.id.navigation_study -> {
//                    selectedFragment = StudyFragment()
//                }
//                R.id.navigation_bookmark -> {
//                    selectedFragment = BookmarkFragment()
//                }
//                R.id.navigation_mypage -> {
//                    selectedFragment = MyPageFragment()
//                }
//            }
//            if (selectedFragment != null) {
//                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
//            }
//            true
//        }
//
//        if (savedInstanceState == null) {
//            bottomNavigationView.selectedItemId = R.id.navigation_home
//        }
    }



}
