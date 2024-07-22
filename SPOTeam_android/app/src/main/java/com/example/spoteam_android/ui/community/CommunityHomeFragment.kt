package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.categoryData
import com.example.spoteam_android.databinding.FragmentCommunityHomeBinding
import com.example.spoteam_android.indexData

class CommunityHomeFragment : Fragment() {

    lateinit var binding: FragmentCommunityHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityHomeBinding.inflate(inflater, container, false)

        initRecyclerview()

        binding.communityMoveCommunityIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CommunityFragment()
                ).commitAllowingStateLoss()
        }

        return binding.root
    }

    private fun initRecyclerview(){
        binding.communityHomeBestPopularityContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.communityHomeNotificationContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataList1: ArrayList<indexData> = arrayListOf()

        // arrayList 에 데이터 삽입
        dataList1.apply{
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            add(indexData("01", "first", "10"))
            add(indexData("02", "second", "9"))
            add(indexData("03", "third", "8"))
            add(indexData("04", "fourth", "7"))
            add(indexData("05", "fifth", "6"))
            add(indexData("06", "sixth", "5"))
            add(indexData("07", "seventh", "4"))
            add(indexData("08", "eighth", "3"))
            add(indexData("09", "ninth", "2"))
            add(indexData("10", "tenth", "1"))
        }

        val dataRVAdapter1 = CommunityHomeRVAdapterWithIndex(dataList1)

        binding.communityHomeBestPopularityContentRv.adapter = dataRVAdapter1
        binding.communityHomeNotificationContentRv.adapter = dataRVAdapter1


        binding.communityHomeCommunityContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataList2: ArrayList<categoryData> = arrayListOf()

        // arrayList 에 데이터 삽입
        dataList2.apply{
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            add(categoryData("합격후기", "카카오 최종합격 후기", "10"))
            add(categoryData("정보공유", "5월 공모전 추천 zip", "9"))
            add(categoryData("고민상담", "게시글 제목", "8"))
            add(categoryData("취준토크", "스펙 이정도면 괜찮을까", "7"))
            add(categoryData("자유토크", "4학년 취준생들 요즘 뭐해", "6"))
        }

        val dataRVAdapter2 = CommunityHomeRVAdapterWithCategory(dataList2)

        binding.communityHomeCommunityContentRv.adapter = dataRVAdapter2

    }

}