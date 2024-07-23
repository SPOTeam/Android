package com.example.spoteam_android.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.databinding.FragmentStudyBinding

class StudyFragment : Fragment() {

    private lateinit var binding: FragmentStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 리스트 생성
        val itemList = ArrayList<BoardItem>().apply {
            add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
            add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
            add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))
        }

        // 어댑터 설정
        val boardAdapter = BoardAdapter(itemList)

        // RecyclerView 설정
        binding.fragmentStudyRv.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }
}
