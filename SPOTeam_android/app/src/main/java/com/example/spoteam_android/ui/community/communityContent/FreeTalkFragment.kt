package com.example.spoteam_android.ui.community.communityContent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.CommunityData
import com.example.spoteam_android.databinding.FragmentCommunityCategoryContentBinding

class FreeTalkFragment : Fragment() {

    lateinit var binding: FragmentCommunityCategoryContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityCategoryContentBinding.inflate(inflater, container, false)

        initRecyclerview()

        return binding.root
    }

    private fun initRecyclerview(){
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataList: ArrayList<CommunityData> = arrayListOf()

        // arrayList 에 데이터 삽입
        dataList.apply{
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            add(CommunityData("첫번째 게시글", "첫번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("두번째 게시글", "두번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("세번째 게시글", "세번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("네번째 게시글", "네번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("다섯번째 게시글", "다섯번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("여섯번째 게시글", "여섯번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("일곱번째 게시글", "일곱번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("여덟번째 게시글", "여덟번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
            add(CommunityData("아홉번째 게시글", "아홉번째 게시글입니다.", "6", "10", "12", "20", "익명", "2024.12.15"))
        }

        val dataRVAdapter = CommunityCategoryContentRVAdapter(dataList)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

    }
}