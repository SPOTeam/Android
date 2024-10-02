package com.example.spoteam_android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.Toast
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.spoteam_android.ui.mypage.ExitStudyPopupFragment

class StudyRegisterPopupFragment(private val context: Context) {

    private val dlg = Dialog(context)

    fun start() {

        Log.d("StudyRegisterPopupFragment","start가 실행되었습니다.")
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.fragment_study_register_popup)

        val editText = dlg.findViewById<EditText>(R.id.popup_edit_text)
        val applyButton = dlg.findViewById<ImageView>(R.id.popup_apply_button)

        val complete = CompleteSignupStudyFragment(context)

        applyButton.setOnClickListener {
            val inputText = editText.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(context, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                complete.start()
                dlg.dismiss()
            }
        }
        // 다이얼로그 표시
        dlg.show()
    }

}

