package com.example.spoteam_android.ui.alert

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.ui.study.DetailStudyFragment

class OkDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)
    private var studyId : Int = -1

    fun setStudyId(studyId : Int) {
        this.studyId = studyId
    }
    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_attendance_complete)

        val btnMove = dlg.findViewById<TextView>(R.id.move_to_study_tv)
        btnMove.setOnClickListener {
            val fragment = DetailStudyFragment()

            val bundle = Bundle().apply {
                putInt("FromOKToDetailStudy", studyId)
            }

            fragment.arguments = bundle

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
            dlg.dismiss()
        }

        // 다이얼로그 표시
        dlg.show()
    }
}