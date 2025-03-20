package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.provider.CalendarContract.Colors
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.ContentResponse
import com.example.spoteam_android.ui.community.EndStudyResponse
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberIntroInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EndStudyDialog(val context: Context, val studyId : Int) {

    private val dlg = android.app.Dialog(context)
    private lateinit var editText: EditText
    private lateinit var applyButton: Button
    private lateinit var charCountTextView: TextView
    private lateinit var description: TextView

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_end_study)

        val closeButton = dlg.findViewById<ImageView>(R.id.end_study_close)
        applyButton = dlg.findViewById(R.id.dialog_apply_end_study_bt)
        val cancelButton = dlg.findViewById<Button>(R.id.dialog_cancel_end_study_bt)
        editText = dlg.findViewById(R.id.dialog_study_result_et)
        charCountTextView = dlg.findViewById(R.id.et_count_tv)

        description = dlg.findViewById(R.id.dialog_end_study_additional_comment_tv)

        applyButton.isEnabled = false

        initTextWatcher()

        closeButton.setOnClickListener {
            dlg.dismiss()
        }

        cancelButton.setOnClickListener {
            dlg.dismiss()
        }

        applyButton.setOnClickListener {
            val result = editText.text.toString()
//            sendStudyEndResult(result)
            val lastDlg =  EndStudyFinishDialog(context)
            lastDlg.setContext(result)
            lastDlg.start()
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun sendStudyEndResult(result : String) {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.endStudy(studyId)
            .enqueue(object : Callback<EndStudyResponse> {
                override fun onResponse(
                    call: Call<EndStudyResponse>,
                    response: Response<EndStudyResponse>
                ) {
                    if (response.isSuccessful) {
                        val contentResponse = response.body()
                        if (contentResponse?.isSuccess == "true") {
//                            val dlg =  EndStudyFinishDialog(context)
//                            dlg.start()
                        } else {
                            showError(contentResponse?.message)
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<EndStudyResponse>, t: Throwable) {
                    Log.e("EndStudyResponse", "Failure: ${t.message}", t)
                }
            })
    }
    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }
    private fun initTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLength = s?.length ?: 0
                charCountTextView.text = textLength.toString()

                // 버튼 활성화/비활성화
                applyButton.isEnabled = textLength > 0

                if(textLength > 0 ) {
                    description.setTextColor(ContextCompat.getColorStateList(context, R.color.selector_blue))
                } else {
                    description.setTextColor(ContextCompat.getColorStateList(context, R.color.button_disabled_text))
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length ?: 0 > 30) {
                    editText.setText(s?.subSequence(0, 30)) // 30자 초과 방지
                    editText.setSelection(30) // 커서를 맨 끝으로 이동
                }
            }
        })
    }


}
