package com.example.spoteam_android.ui.recruiting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentRecruitingStudyBinding

class RecruitingStudyFragment : Fragment() {

    lateinit var binding: FragmentRecruitingStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecruitingStudyBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner: Spinner = binding.filterToggle

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_study,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter

        val filter: ImageView = binding.icFilter
        filter.setOnClickListener{
            (activity as MainActivity).switchFragment(RecruitingStudyFilterFragment())
        }

        val recruiting_board = binding.recruitingStudyReyclerview

        val itemList = ArrayList<BoardItem>()

        itemList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
        itemList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
        itemList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))
        itemList.add(BoardItem("기타 스터디", "스터디 목표", 10, 4, 1, 300))
        itemList.add(BoardItem("롤 스터디", "스터디 목표", 10, 5, 5, 200))
        itemList.add(BoardItem("안드로이드 스터디", "스터디 목표", 10, 5, 200, 200))
        itemList.add(BoardItem("ios 스터디", "스터디 목표", 10, 7, 1, 200))
        itemList.add(BoardItem("Server 스터디", "스터디 목표", 10, 8, 1, 200))
        itemList.add(BoardItem("Kotlin 스터디", "스터디 목표", 10, 5, 1, 200))
        itemList.add(BoardItem("Java 스터디", "스터디 목표", 10, 5, 1, 200))


        val boardAdapter = BoardAdapter(itemList)
        boardAdapter.notifyDataSetChanged()

        recruiting_board.adapter = boardAdapter
        recruiting_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


    }

}
