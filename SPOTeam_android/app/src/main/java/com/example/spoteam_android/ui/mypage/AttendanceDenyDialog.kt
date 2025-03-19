package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberIntroInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceDenyDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)
    private var memberId : Int? = null
    private var studyId : Int? = null

    fun setStudyId(studyId : Int) {
        this.studyId = studyId
    }

    fun setMemberId(memberId: Int) {
        this.memberId = memberId
    }

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_consider_reject)


        val btnMove1 = dlg.findViewById<TextView>(R.id.reject_tv)
        btnMove1.setOnClickListener {
            denyMemberAttendance()
            dlg.dismiss()
        }

        val btnMove2 = dlg.findViewById<TextView>(R.id.cancel_tv)
        btnMove2.setOnClickListener {
            dlg.dismiss()
        }

        val btnMove3 = dlg.findViewById<ImageView>(R.id.close_button)
        btnMove3.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun denyMemberAttendance() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.postAttendanceMember(studyId!!, memberId!!,false)
            .enqueue(object : Callback<MemberAcceptResponse> {
                override fun onResponse(
                    call: Call<MemberAcceptResponse>,
                    response: Response<MemberAcceptResponse>
                ) {
                    Log.d("MyStudyAttendance", "response: ${response.isSuccessful}")
                    if (response.isSuccessful) {
                        val memberAttendanceResponse = response.body()
                        Log.d("MyStudyAttendance", "responseBody: ${memberAttendanceResponse?.isSuccess}")
                        if (memberAttendanceResponse?.isSuccess == "true") {
                            Log.d("MyStudyAttendance", "deny")
                        } else {
                            showError(memberAttendanceResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<MemberAcceptResponse>, t: Throwable) {
                    Log.e("MyStudyAttendance", "Failure: ${t.message}", t)
                }
            })
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

}
