package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.spoteam_android.R


class HostLeaveStudySuccessDialog(
    private val context: Context,
    private val onComplete: (() -> Unit)? = null // 🔹 콜백 추가

){

    private val dlg = Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 배경을 투명하게 설정하여 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_host_leave_success)

        // 다이얼로그 표시
        dlg.show()

        // X 버튼 클릭 시 다이얼로그 닫기
        val ivClose: ImageView = dlg.findViewById(R.id.iv_close)
        ivClose.setOnClickListener {
            onComplete?.invoke()
            dlg.dismiss() // 다이얼로그 닫기
        }

        val btnTakeCharge: Button = dlg.findViewById(R.id.btn_take_charge)
        btnTakeCharge.setOnClickListener {
            onComplete?.invoke()
            dlg.dismiss() // 다이얼로그 닫기
        }

    }


}
