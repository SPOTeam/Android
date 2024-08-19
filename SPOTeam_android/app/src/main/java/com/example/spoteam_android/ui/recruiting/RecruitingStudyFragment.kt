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

        itemList.add(BoardItem(1,"피아노 스터디", "스터디 목표", "피아노 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(2,"태권도 스터디", "스터디 목표", "태권도 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(3,"프랑스어 스터디", "스터디 목표", "프랑스어 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(4,"토익 스터디", "스터디 목표", "토익 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(5,"C언어 스터디", "스터디 목표", "C언어 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(6,"디자인 스터디", "스터디 목표", "디자인 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(7,"기타 스터디", "스터디 목표", "기타 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))

        val boardAdapter = BoardAdapter(itemList){selectedItem ->}
        boardAdapter.notifyDataSetChanged()

        recruiting_board.adapter = boardAdapter
        recruiting_board.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


    }

}

