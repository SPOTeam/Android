package com.example.spoteam_android.ui.alert

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.spoteam_android.R

class RefuseDialog(context : Context) {

    private val dlg = android.app.Dialog(context)

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
            dlg.dismiss()
        }

        // 다이얼로그 표시
        dlg.show()
    }
}