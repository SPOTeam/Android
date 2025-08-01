package com.spot.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import com.spot.android.R

class CompleteSignupStudyFragment (private val context: Context) {

    private val dlg = Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.fragment_complete_signup_study)

        val btnOk = dlg.findViewById<ImageButton>(R.id.imgbtn_ok)
        btnOk?.setOnClickListener {
            Toast.makeText(context, "스터디 신청을 완료했습니다.", Toast.LENGTH_SHORT).show()
            dlg.dismiss()
        }

        // 다이얼로그 표시
        dlg.show()
    }

}

