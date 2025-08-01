package com.spot.android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.spot.android.HostWithDrawl
import com.spot.android.RetrofitInstance
import com.spot.android.WithdrawHostRequest
import com.spot.android.databinding.FragmentMandateStudyOwnerReasonBinding
import com.spot.android.ui.interestarea.GetHostInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MandateStudyOwnerReasonFragment(
    private val context: Context,
    private val studyId: Int,
    private val memberId: Int,
    private val onComplete: (() -> Unit)? = null
) {
    private val dlg = Dialog(context)
    private var binding: FragmentMandateStudyOwnerReasonBinding? = null

    fun start() {
        dlg.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = FragmentMandateStudyOwnerReasonBinding.inflate(dlg.layoutInflater)
        dlg.setContentView(binding!!.root)

        dlg.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt()
        )
        dlg.setCanceledOnTouchOutside(true)

        binding?.ivClose?.setOnClickListener {
            dlg.dismiss()
        }

        binding?.mandateStudyContentEt?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkEditText()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding?.mandateStudyTv?.setOnClickListener {
            val reasonText = binding?.mandateStudyContentEt?.text.toString()
            requestWithdrawHost(studyId, memberId, reasonText)
        }

        dlg.show()
    }

    private fun checkEditText() {
        val text = binding?.mandateStudyContentEt?.text?.toString() ?: ""
        binding?.mandateStudyTv?.isEnabled = text.isNotEmpty()
    }

    private fun requestWithdrawHost(studyId: Int, newHostId: Int, reason: String) {
        val service = RetrofitInstance.retrofit.create(GetHostInterface::class.java)
        val request = WithdrawHostRequest(newHostId, reason)

        service.withDrawlHost(studyId, request).enqueue(object : Callback<HostWithDrawl> {
            override fun onResponse(call: Call<HostWithDrawl>, response: Response<HostWithDrawl>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("MandateStudyOwner", """
                        ✅ [호스트 위임 성공]
                        - 응답 코드: ${response.code()}
                        - 메시지: ${response.body()?.message}
                        - 전체 응답: ${response.body()}
                    """.trimIndent())

                    showMemberLeaveSuccessDialog()
                    dlg.dismiss()
                } else {
                    Toast.makeText(context, "위임 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HostWithDrawl>, t: Throwable) {
                Log.e("MandateStudyOwner", "위임 실패: ${t.message}", t)
                Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showMemberLeaveSuccessDialog() {
        val successDialog = HostLeaveStudySuccessDialog(context, onComplete)
        successDialog.start()
    }
}
