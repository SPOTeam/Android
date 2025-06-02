package com.example.spoteam_android

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.example.spoteam_android.databinding.DialogReportMemberContentBinding
import com.example.spoteam_android.presentation.community.CommunityAPIService
import com.example.spoteam_android.presentation.community.ReportCrewRequest
import com.example.spoteam_android.presentation.community.ReportCrewResponse
import com.example.spoteam_android.presentation.mypage.FinishReportCrewDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportStudyMemberContentFragment(
    private val context: Context,
    private val studyId: Int,
    private val memberId : Int
) {
    private val dlg = android.app.Dialog(context)
    private var binding: DialogReportMemberContentBinding? = null

    fun start() {
//        Log.d("ReportStudyCrewContentDialog", "StudyId : $studyId, memberId : $memberId")

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogReportMemberContentBinding.inflate(LayoutInflater.from(context))
        dlg.setContentView(binding!!.root)

        dlg.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 420f, context.resources.displayMetrics
            ).toInt()
        )

        binding?.reportCrewPrevIv?.setOnClickListener {
            dlg.dismiss()
        }

        binding?.reportCrewContentEt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkEditText()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding?.reportCrewTv?.setOnClickListener {
//            reportStudyMember()
            val finishDialog = FinishReportCrewDialog(context)
            finishDialog.start()
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun checkEditText() {
        val text = binding?.reportCrewContentEt?.text?.toString() ?: ""
        binding?.reportCrewTv?.isEnabled = text.isNotEmpty()
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    private fun reportStudyMember() {
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
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val finishDialog = FinishReportCrewDialog(context)
                        finishDialog.start()
                        dlg.dismiss()
                    } else {
                        showError("신고 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ReportCrewResponse>, t: Throwable) {
                    showError("네트워크 오류: ${t.message}")
                }
            })
    }
}
