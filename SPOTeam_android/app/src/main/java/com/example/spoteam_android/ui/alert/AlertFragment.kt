package com.example.spoteam_android.ui.alert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.HouseFragment
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentAlertBinding
import com.example.spoteam_android.ui.community.AlertDetail
import com.example.spoteam_android.ui.community.AlertResponse
import com.example.spoteam_android.ui.community.AlertStudyResponse
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.NotificationStateResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertFragment : Fragment() {

    private lateinit var binding: FragmentAlertBinding
    private var page: Int = 0
    private var size: Int = 100

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
            (context as MainActivity).isOnAlertFragment(HouseFragment())
        }

        binding.studyAlertCl.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CheckAppliedStudyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        fetchAlert()
        fetchStudyAlert()

        return binding.root
    }

    fun scrollToTop() {
        binding.alertContentRv.smoothScrollToPosition(0)
    }

    private fun fetchAlert() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getAlert(page, size)
            .enqueue(object : Callback<AlertResponse> {
                override fun onResponse(
                    call: Call<AlertResponse>,
                    response: Response<AlertResponse>
                ) {
                    if (response.isSuccessful) {
                        val alertResponse = response.body()
                        if (alertResponse?.isSuccess == "true") {
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
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyAlert(page, size)
            .enqueue(object : Callback<AlertStudyResponse> {
                override fun onResponse(
                    call: Call<AlertStudyResponse>,
                    response: Response<AlertStudyResponse>
                ) {
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
                        if (studyAlertResponse?.isSuccess == "true") {
                            binding.studyAlertCl.isEnabled = true
                        } else {
                            binding.studyAlertCl.isEnabled = false
                            showError(studyAlertResponse?.message)
                        }
                    } else {
                        binding.studyAlertCl.isEnabled = false
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<AlertStudyResponse>, t: Throwable) {
                    binding.studyAlertCl.isEnabled = false
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
        val layoutManager = binding.alertContentRv.layoutManager as LinearLayoutManager

        // ✅ 스크롤 위치와 offset 저장
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleItemView = layoutManager.findViewByPosition(firstVisibleItemPosition)
        val offset = firstVisibleItemView?.top ?: 0

        val scrollInfo = Pair(firstVisibleItemPosition, offset)

        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postNotificationState(data.notificationId)
            .enqueue(object : Callback<NotificationStateResponse> {
                override fun onResponse(
                    call: Call<NotificationStateResponse>,
                    response: Response<NotificationStateResponse>
                ) {
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
                        if (studyAlertResponse?.isSuccess == "true") {
                            Log.d("CurrentScrollPosition", "pos: $firstVisibleItemPosition, offset: $offset")
                            fetchAlertWithoutScroll(scrollInfo) // ✅ 스크롤 복원 포함
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

    private fun fetchAlertWithoutScroll(scrollInfo: Pair<Int, Int>) {
        val (scrollPosition, offset) = scrollInfo

        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getAlert(page, size)
            .enqueue(object : Callback<AlertResponse> {
                override fun onResponse(
                    call: Call<AlertResponse>,
                    response: Response<AlertResponse>
                ) {
                    if (response.isSuccessful) {
                        val alertResponse = response.body()
                        if (alertResponse?.isSuccess == "true") {
                            val alertInfo = alertResponse.result.notifications
                            initMultiViewRecyclerView(alertInfo)

                            // ✅ RecyclerView가 데이터 바인딩 완료 후 실행
                            binding.alertContentRv.post {
                                (binding.alertContentRv.layoutManager as LinearLayoutManager)
                                    .scrollToPositionWithOffset(scrollPosition, offset)
                            }
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
}
