package com.example.spoteam_android.ui.alert

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.ui.community.AcceptedAlertStudyResponse
import com.example.spoteam_android.ui.community.CommunityRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RefuseDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)
    private var studyId : Int = -1

    fun setStudyId(studyId : Int) {
        this.studyId = studyId
    }

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_attendance_reject)

        val btnCancel = dlg.findViewById<TextView>(R.id.attendance_cancel_tv)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }

        val btnReject = dlg.findViewById<TextView>(R.id.attendance_reject_tv)
        btnReject.setOnClickListener {
            postStudyAccept(studyId)
            dlg.dismiss()
        }

        // 다이얼로그 표시
        dlg.show()
    }

    private fun postStudyAccept(studyId : Int) {
        CommunityRetrofitClient.instance.postAcceptedStudyAlert(studyId, false)
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
    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }
}