package com.example.spoteam_android.ui.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.AlertInfo
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentAlertBinding
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.ENROLL
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.LIVE
import com.example.spoteam_android.ui.alert.AlertMultiViewRVAdapter.Companion.UPDATE

class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        initMultiViewRecyclerView()

        binding.communityPrevIv.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun initMultiViewRecyclerView() {
        binding.alertContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val dataList :  ArrayList<AlertInfo> = arrayListOf()

        dataList.apply {
            add(AlertInfo("First", ENROLL))
            add(AlertInfo("Second", LIVE))
            add(AlertInfo("Third", UPDATE))
            add(AlertInfo("Fourth", UPDATE))
            add(AlertInfo("Fifth", LIVE))
            add(AlertInfo("Sixth", ENROLL))
            add(AlertInfo("Seventh", LIVE))
            add(AlertInfo("Eight", UPDATE))
            add(AlertInfo("Ninth", UPDATE))
            add(AlertInfo("Tenth", LIVE))
        }

        val dataRVAdapter = AlertMultiViewRVAdapter(dataList)
        binding.alertContentRv.adapter = dataRVAdapter

        dataRVAdapter.itemClick = object : AlertMultiViewRVAdapter.ItemClick {
            override fun onItemClick(view: View, position: Int) {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CheckAppliedStudyFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }
        }
    }
}
