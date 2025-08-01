package com.umcspot.android.ui.alert

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.databinding.FragmentCheckAppliedStudyBinding
import com.umcspot.android.ui.category.CategoryFragment_1
import com.umcspot.android.ui.community.AcceptedAlertStudyResponse
import com.umcspot.android.ui.community.AlertStudyDetail
import com.umcspot.android.ui.community.AlertStudyResponse
import com.umcspot.android.ui.community.CommunityAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckAppliedStudyFragment : Fragment(), AttendStudyCompleteListener, AttendStudyRejectListener{

    lateinit var binding: FragmentCheckAppliedStudyBinding
    private var page : Int = 0
    private var size : Int = 10

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckAppliedStudyBinding.inflate(inflater, container, false)

        binding.communityPrevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.lookAroundStudyTv.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CategoryFragment_1())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }

        fetchStudyAlert()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchStudyAlert()
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
                            val studyAlertInfo = studyAlertResponse.result.notifications
                            binding.emptyAttendStudy.visibility = View.GONE
                            binding.communityCategoryContentRv.visibility = View.VISIBLE
                            initRecyclerview(studyAlertInfo)
                        } else {
                            binding.emptyAttendStudy.visibility = View.VISIBLE
                            binding.communityCategoryContentRv.visibility = View.GONE
                        }
                    } else {
                        binding.emptyAttendStudy.visibility = View.VISIBLE
                        binding.communityCategoryContentRv.visibility = View.GONE
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
//                val dlgOK = OkDialog(requireContext(), this@CheckAppliedStudyFragment)
//                dlgOK.setStudyId(data.studyId)
//                dlgOK.start()

            }

            override fun onRefuseClick(data: AlertStudyDetail) {
                val dlgRefuse = RefuseDialog(requireContext(), this@CheckAppliedStudyFragment)
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
                    if (response.isSuccessful) {
                        val studyAlertResponse = response.body()
                        if (studyAlertResponse?.isSuccess == "true") {
                            val dlgOK = OkDialog(requireContext(), this@CheckAppliedStudyFragment)
                            dlgOK.setStudyId(studyId)
                            dlgOK.start()
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

    override fun onAttendComplete() {
        fetchStudyAlert()
    }

    override fun onAttendRejected() {
        fetchStudyAlert()
    }
}