package com.spot.android.ui.alert

import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.spot.android.HouseFragment
import com.spot.android.MainActivity
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentAlertBinding
import com.spot.android.ui.community.AlertDetail
import com.spot.android.ui.community.AlertResponse
import com.spot.android.ui.community.AlertStudyResponse
import com.spot.android.ui.community.CommunityAPIService
import com.spot.android.ui.community.NotificationStateResponse
import com.spot.android.ui.study.DetailStudyFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlertFragment : Fragment() {

    private val studyViewModel: StudyViewModel by activityViewModels()


    private lateinit var binding: FragmentAlertBinding
    private var page: Int = 0
    private var size: Int = 100

    private var isChanged : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        studyViewModel.studyId.observe(viewLifecycleOwner) { id ->
            if (id != null && id > 0 && isChanged) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, DetailStudyFragment())
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
            }

            isChanged = false
        }

        fetchAlert()

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
                            binding.emptyAlert.visibility = View.GONE
                            initMultiViewRecyclerView(alertInfo)
                        } else {
                            showError(alertResponse?.message)
                            binding.emptyAlert.visibility = View.VISIBLE
                        }
                    } else {
                        showError(response.code().toString())
                        binding.emptyAlert.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<AlertResponse>, t: Throwable) {
                    Log.e("MyAlert", "Failure: ${t.message}", t)
                }
            })
    }

    private fun fetchStudyAlert(adapter: AlertMultiViewRVAdapter) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyAlert(page, size)
            .enqueue(object : Callback<AlertStudyResponse> {
                override fun onResponse(
                    call: Call<AlertStudyResponse>,
                    response: Response<AlertStudyResponse>
                ) {
                    val enabled = response.body()?.isSuccess == "true"
                    adapter.isExistAlert = enabled
                    adapter.notifyItemChanged(0) // 헤더만 갱신
                }

                override fun onFailure(call: Call<AlertStudyResponse>, t: Throwable) {
                    adapter.isExistAlert = false
                    adapter.notifyItemChanged(0)
                }
            })
    }

    private fun showError(message: String?) {
//        Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun initMultiViewRecyclerView(alertInfo: List<AlertDetail>) {
        val reversedAlertInfo = alertInfo.reversed()
        val adapter = AlertMultiViewRVAdapter(reversedAlertInfo)

        binding.alertContentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.alertContentRv.adapter = adapter


        adapter.itemClick = object : AlertMultiViewRVAdapter.ItemClick {
            override fun onStateUpdateClick(data: AlertDetail) {
                if(!data.isChecked) {
                    updateState(data)
                }
                Log.d("StudyIdInAlert", data.studyId.toString())
                isChanged = true
                studyViewModel.fetchStudyDetail(data.studyId)
            }
        }

        (context as MainActivity).isOnAlertFragment(this)

        adapter.headerClickListener = {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CheckAppliedStudyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        adapter.onNavigateToDetail = {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, DetailStudyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        fetchStudyAlert(adapter)
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
//                            Log.d("CurrentScrollPosition", "pos: $firstVisibleItemPosition, offset: $offset")
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

    override fun onPause() {
        super.onPause()
        (context as MainActivity).isOnAlertFragment(HouseFragment())
        (context as MainActivity).isOnCommunityHome(HouseFragment())
    }

    override fun onResume() {
        super.onResume()
        (context as MainActivity).isOnAlertFragment(this)
    }
}
