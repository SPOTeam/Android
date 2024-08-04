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

        itemList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 100))
        itemList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 100))
        itemList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 100))

        val boardAdapter = BoardAdapter(itemList)

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

