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

        val dataList :  ArrayList<BoardItem> = arrayListOf()

        dataList.apply {
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            dataList.add(BoardItem("피아노 스터디", "스터디 목표", 10, 1, 1, 600))
            dataList.add(BoardItem("태권도 스터디", "스터디 목표", 10, 2, 1, 500))
            dataList.add(BoardItem("보컬 스터디", "스터디 목표", 10, 3, 1, 400))
        }

        val dataRVAdapter = BookMarkRVAdapter(dataList)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

    }
}