package com.example.spoteam_android.presentation.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.spoteam_android.R

class AttendanceContentDialog(
    private val context: Context
) {

    private val dlg = android.app.Dialog(context)
    private var callback: fetchData? = null // ✅ 콜백 필드 추가

    fun setCallback(cb: fetchData) {
        callback = cb
    }

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_accept_attendance)

        val btnMove1 = dlg.findViewById<ImageView>(R.id.accept_close)
        btnMove1.setOnClickListener {
            callback?.fetchAttendedData()
            dlg.dismiss()
        }

        val btnMove2 = dlg.findViewById<TextView>(R.id.accept_tv)
        btnMove2.setOnClickListener {
            callback?.fetchAttendedData()
            dlg.dismiss()
        }

        dlg.show()
    }
}
