package com.example.spoteam_android.ui.alert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCheckAppliedStudyBinding
import com.example.spoteam_android.ui.community.AcceptedAlertStudyResponse
import com.example.spoteam_android.ui.community.AlertStudyDetail
import com.example.spoteam_android.ui.community.AlertStudyResponse
import com.example.spoteam_android.ui.community.CommunityAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckAppliedStudyFragment : Fragment() {

    lateinit var binding: FragmentCheckAppliedStudyBinding
    private var page : Int = 0
    private var size : Int = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckAppliedStudyBinding.inflate(inflater, container, false)

        (context as MainActivity).isOnAlertFragment(CheckAppliedStudyFragment())

        binding.communityPrevIv.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            requireActivity().supportFragmentManager.popBackStack()
            (context as MainActivity).isOnAlertFragment(AlertFragment())
        }

        fetchStudyAlert()

        return binding.root
    }

    private fun fetchStudyAlert() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyAlert(page, size)
            .enqueue(object : Callback<AlertStudyResponse> {
                override fun onResponse(
                    call: Call<AlertStudyResponse>,
                    response: Response<AlertStudyResponse>
                ) {
//                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
//                        Log.d("MyStudyAttendance", "responseBody: ${studyAlertResponse?.isSuccess}")
                        if (studyAlertResponse?.isSuccess == "true") {
                            val studyAlertInfo = studyAlertResponse.result.notifications
                            initRecyclerview(studyAlertInfo)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<AlertStudyResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initRecyclerview(studyAlertInfo: List<AlertStudyDetail>) {
        binding.communityCategoryContentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dataRVAdapter = CheckAppliedStudyFragmentRVAdapter(studyAlertInfo)

        binding.communityCategoryContentRv.adapter = dataRVAdapter

        dataRVAdapter.setItemClickListener(object :CheckAppliedStudyFragmentRVAdapter.OnItemClickListener{
            override fun onOKClick(data : AlertStudyDetail) {

                postStudyAccept(data.studyId)
                val dlgOK = OkDialog(requireContext())
                dlgOK.setStudyId(data.studyId)
                dlgOK.start()
            }

            override fun onRefuseClick(data: AlertStudyDetail) {
                val dlgRefuse = RefuseDialog(requireContext())
                dlgRefuse.setStudyId(data.studyId)
                dlgRefuse.start()
            }

        })

    }

    private fun postStudyAccept(studyId : Int) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postAcceptedStudyAlert(studyId, true)
            .enqueue(object : Callback<AcceptedAlertStudyResponse> {
                override fun onResponse(
                    call: Call<AcceptedAlertStudyResponse>,
                    response: Response<AcceptedAlertStudyResponse>
                ) {
//                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
//                        Log.d("MyStudyAttendance", "responseBody: ${studyAlertResponse?.isSuccess}")
                        if (studyAlertResponse?.isSuccess == "true") {
                            //add method
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<AcceptedAlertStudyResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }
}