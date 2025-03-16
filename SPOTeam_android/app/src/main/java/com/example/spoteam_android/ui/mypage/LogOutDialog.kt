package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.example.spoteam_android.R

class LogOutDialog(
    private val context: Context,
    private val onConfirm: () -> Unit //로그아웃 실행을 콜백함.
) {
    private val dlg = Dialog(context)

    fun start() {
        //다이얼로그 배경처리 관련 로직
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_logout)

        //다이얼로그 내 버튼별 UI 설정
        val logoutBtn: Button = dlg.findViewById(R.id.logout_bt)
        val cancelBtn: Button = dlg.findViewById(R.id.cancel_bt)
        val closeIv: ImageView = dlg.findViewById(R.id.close_iv)

        // 로그아웃 버튼 클릭 시 로그아웃 실행
        logoutBtn.setOnClickListener {
            onConfirm() // 마이페이지프래그먼트에있는 플랫폼별 로그아웃실행
            dlg.dismiss() //다이얼로그 닫기
        }

        // 취소 버튼 및 닫기 버튼 클릭 시 다이얼로그 닫기
        cancelBtn.setOnClickListener { dlg.dismiss() }
        closeIv.setOnClickListener { dlg.dismiss() }

        dlg.show() //다이얼로그 표시
    }
}
