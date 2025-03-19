package com.example.spoteam_android

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.spoteam_android.databinding.FragmentReportMemberContentBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.ReportCrewRequest
import com.example.spoteam_android.ui.community.ReportCrewResponse
import com.example.spoteam_android.ui.community.StudyMemberResponse
import com.example.spoteam_android.ui.mypage.FinishReportCrewDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface ReportCompleteListener {
    fun onReportCompleted()
}

class ReportStudyMemberContentFragment(
    private val context: Context,
    private val studyId: Int,
    private val memberId : Int,
    private val listener: ReportCompleteListener // ✅ 콜백 추가
) {
    private val dlg = BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme)
    private var binding: FragmentReportMemberContentBinding? = null // ✅ nullable 변경

    fun start() {
        Log.d("ReportStudyCrewContentDialog", "StudyId : $studyId, memberId : $memberId")

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // ✅ 바인딩 초기화
        binding = FragmentReportMemberContentBinding.inflate(dlg.layoutInflater)
        dlg.setContentView(binding!!.root) // ✅ null 체크 후 사용

        val heightInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            700f,
            context.resources.displayMetrics
        ).toInt()

        dlg.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            heightInPx
        )

        val btnExit = binding?.reportCrewPrevIv
        btnExit?.setOnClickListener {
            dlg.dismiss()
        }

        binding?.reportCrewContentEt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkEditText()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding?.reportCrewTv?.setOnClickListener{
            reportStudyMember()
        }

        dlg.show()
    }

    private fun checkEditText() {
        val check = binding?.reportCrewContentEt?.text?.toString() ?: ""
        binding?.reportCrewTv?.isEnabled = check.isNotEmpty()
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun reportStudyMember() {
        Log.d("ReportCrewContent", binding?.reportCrewContentEt?.text.toString())

        val requestBody = ReportCrewRequest(
            content = binding?.reportCrewContentEt?.text.toString()
        )

        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.reportStudyMember(studyId, memberId, requestBody)
            .enqueue(object : Callback<ReportCrewResponse> {
                override fun onResponse(
                    call: Call<ReportCrewResponse>,
                    response: Response<ReportCrewResponse>
                ) {
                    if (response.isSuccessful) {
                        val reportResponse = response.body()
                        if (reportResponse?.isSuccess == "true") {
                            Log.d("ReportCrew", "SUCCESS REPORT CREW")
                            val finishReportCrewDialog = FinishReportCrewDialog(context)
                            finishReportCrewDialog.start()
                            dlg.dismiss()
                            listener.onReportCompleted()
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ReportCrewResponse>, t: Throwable) {
                    Log.e("ReportStudyCrewDialog", "Failure: ${t.message}", t)
                }
            })
    }
}
