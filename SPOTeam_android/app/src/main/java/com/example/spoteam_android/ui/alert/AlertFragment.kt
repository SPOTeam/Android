package com.example.spoteam_android.ui.alert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentAlertBinding
import com.example.spoteam_android.ui.community.AlertDetail
import com.example.spoteam_android.ui.community.AlertResponse
import com.example.spoteam_android.ui.community.AlertStudyDetail
import com.example.spoteam_android.ui.community.AlertStudyResponse
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import com.example.spoteam_android.ui.community.NotificationStateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding
    private var page : Int = 0
    private var size : Int = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.studyAlertCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CheckAppliedStudyFragment())  // fragment 변수로 대체
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        fetchAlert()
        fetchStudyAlert()

        return binding.root
    }

    private fun fetchAlert() {
        CommunityRetrofitClient.instance.getAlert(page, size)
            .enqueue(object : Callback<AlertResponse> {
                override fun onResponse(
                    call: Call<AlertResponse>,
                    response: Response<AlertResponse>
                ) {
//                    Log.d("MyAlert", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val alertResponse = response.body()
//                        Log.d("MyAlert", "responseBody: ${alertResponse?.isSuccess}")
                        if (alertResponse?.isSuccess == "true") {
//                            Log.d("MyAlert", "responseBody: ${alertResponse?.result?.notifications}")
                            val alertInfo = alertResponse.result.notifications
                            initMultiViewRecyclerView(alertInfo)
                        } else {
                            showError(alertResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<AlertResponse>, t: Throwable) {
                    Log.e("MyAlert", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchStudyAlert() {
        CommunityRetrofitClient.instance.getStudyAlert(page, size)
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

//                            Log.d("MyStudyAttendance", "responseBody: ${studyAlertResponse?.result?.notifications}")
                            binding.studyAlertCl.visibility = View.VISIBLE

                        } else {
                            binding.studyAlertCl.visibility = View.GONE
                            showError(studyAlertResponse?.message)
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

    private fun initMultiViewRecyclerView(alertInfo: List<AlertDetail>) {
        binding.alertContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val reversedAlertInfo = alertInfo.reversed()

        val dataRVAdapter = AlertMultiViewRVAdapter(reversedAlertInfo)

        binding.alertContentRv.adapter = dataRVAdapter

        dataRVAdapter.itemClick = object : AlertMultiViewRVAdapter.ItemClick {
            override fun onStateUpdateClick(data: AlertDetail) {
                updateState(data)   
            }
        }
    }

    private fun updateState(data: AlertDetail) {
        CommunityRetrofitClient.instance.postNotificationState(data.notificationId)
            .enqueue(object : Callback<NotificationStateResponse> {
                override fun onResponse(
                    call: Call<NotificationStateResponse>,
                    response: Response<NotificationStateResponse>
                ) {
//                    Log.d("AlertStateUpdate", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
//                        Log.d("AlertStateUpdate", "responseBody: ${studyAlertResponse?.isSuccess}")
                        if (studyAlertResponse?.isSuccess == "true") {
//                            Log.d("AlertStateUpdate", "responseBody: ${studyAlertResponse?.result}")
                            fetchAlert()
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<NotificationStateResponse>, t: Throwable) {
                    Log.e("AlertStateUpdate", "Failure: ${t.message}", t)
                }
            })
    }
}
