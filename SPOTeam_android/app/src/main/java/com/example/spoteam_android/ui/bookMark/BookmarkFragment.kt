package com.example.spoteam_android.ui.bookMark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.databinding.FragmentBookmarkBinding
import com.example.spoteam_android.ui.bookMark.BookMarkRVAdapter

class BookmarkFragment : Fragment() {

    lateinit var binding: FragmentBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        initRecyclerview()

        return binding.root
    }

    private fun initRecyclerview(){
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val itemList = ArrayList<BoardItem>()
        itemList.add(BoardItem(1,"피아노 스터디", "스터디 목표", "피아노 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(1,"태권도 스터디", "스터디 목표", "태권도 스터디입니다", 2, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))
        itemList.add(BoardItem(1,"보컬 스터디", "스터디 목표", "보컬 스터디입니다", 3, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String"))

        val dataRVAdapter = BookMarkRVAdapter(itemList)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

    }
}