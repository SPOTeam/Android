package com.example.spoteam_android.ui.alert

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.CommunityData
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.StudyInfo
import com.example.spoteam_android.databinding.FragmentCheckAppliedStudyBinding
import com.example.spoteam_android.ui.community.CommunityContentActivity

class CheckAppliedStudyFragment : Fragment() {

    lateinit var binding: FragmentCheckAppliedStudyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckAppliedStudyBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            requireActivity().supportFragmentManager.popBackStack()
        }

        initRecyclerview()

        return binding.root
    }

    private fun initRecyclerview(){
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        val dataList: ArrayList<StudyInfo> = arrayListOf()

        // arrayList 에 데이터 삽입
        dataList.apply{
            // arrayList 타입이 Data 객체이다. | 데이터 삽입 시 Data 객체 타입으로 넣어줌.
            add(StudyInfo("JLPT"))
            add(StudyInfo("토익"))
            add(StudyInfo("코딩"))
            add(StudyInfo("한국사"))
            add(StudyInfo("요리"))
        }

        val dataRVAdapter = CheckAppliedStudyFragmentRVAdapter(dataList)
        //리스너 객체 생성 및 전달

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object :CheckAppliedStudyFragmentRVAdapter.OnItemClickListener{
            override fun onOKClick() {
                val dlgOK = OkDialog(requireContext())

                dlgOK.start()
            }

            override fun onRefuseClick() {
                val dlgRefuse = RefuseDialog(requireContext())
                dlgRefuse.start()
            }

        })

    }
}