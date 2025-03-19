package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberIntroInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishReportCrewDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_crew_report_finish)

        val btnMove1 = dlg.findViewById<ImageView>(R.id.report_crew_finish_prev_iv)
        btnMove1.setOnClickListener {
            dlg.dismiss()
        }

        val btnMove2 = dlg.findViewById<Button>(R.id.dialog_report_crew_complete_bt)
        btnMove2.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }
}
