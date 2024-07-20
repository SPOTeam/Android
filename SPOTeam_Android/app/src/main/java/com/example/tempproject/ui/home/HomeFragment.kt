package com.example.tempproject.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tempproject.Data
import com.example.tempproject.databinding.FragmentHomeBinding
import com.example.tempproject.ui.cummunityHome.CommunityHomeContentRVAdapter

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initRecyclerview()

        return binding.root
    }

    private fun initRecyclerview(){
        binding.communityHomeBestPopularityContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.communityHomeCommunityContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.communityHomeNotificationContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataList: ArrayList<Data> = arrayListOf()

        // arrayList 에 데이터 삽입
        dataList.apply{
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            add(Data("01", "first", "10"))
            add(Data("02", "second", "9"))
            add(Data("03", "third", "8"))
            add(Data("04", "fourth", "7"))
            add(Data("05", "fifth", "6"))
            add(Data("06", "sixth", "5"))
            add(Data("07", "seventh", "4"))
            add(Data("08", "eighth", "3"))
            add(Data("09", "ninth", "2"))
            add(Data("10", "tenth", "1"))
        }

        val dataRVAdapter = CommunityHomeContentRVAdapter(dataList)
        //리스너 객체 생성 및 전달

        binding.communityHomeBestPopularityContentRv.adapter = dataRVAdapter
        binding.communityHomeCommunityContentRv.adapter = dataRVAdapter
        binding.communityHomeNotificationContentRv.adapter = dataRVAdapter
    }
}