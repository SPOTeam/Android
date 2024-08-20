package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.example.spoteam_android.R

class AttendanceAcceptDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_consider_complete)

        val btnMove1 = dlg.findViewById<TextView>(R.id.ok_tv)
        btnMove1.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

}
