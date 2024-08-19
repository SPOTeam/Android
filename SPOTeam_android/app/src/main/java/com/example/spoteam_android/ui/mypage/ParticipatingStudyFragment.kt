package com.example.spoteam_android.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.databinding.FragmentParticipatingStudyBinding
import com.example.spoteam_android.databinding.ItemRecyclerViewPlusToggleBinding


class ParticipatingStudyFragment : Fragment(){

    lateinit var binding: FragmentParticipatingStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParticipatingStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val participatingboard = binding.participatingStudyReyclerview

        val itemList = ArrayList<BoardItem>()

        itemList.add(BoardItem(1,"피아노 스터디", "스터디 목표", "피아노 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(1,"태권도 스터디", "스터디 목표", "태권도 스터디입니다", 2, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(1,"보컬 스터디", "스터디 목표", "보컬 스터디입니다", 3, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))

        val boardAdapter = BoardAdapter(itemList){selectedItem ->}



        participatingboard.post {
            for (i in 0 until boardAdapter.itemCount) {
                val holder = participatingboard.findViewHolderForAdapterPosition(i) as? BoardAdapter.BoardViewHolder
                holder?.binding?.toggle?.visibility = View.VISIBLE
            }
        }



        participatingboard.adapter = boardAdapter
        participatingboard.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        boardAdapter.notifyDataSetChanged()
    }
}

