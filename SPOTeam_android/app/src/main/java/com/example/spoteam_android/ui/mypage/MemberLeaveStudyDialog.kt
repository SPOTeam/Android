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


class MemberLeaveStudyDialog(private val context: Context,
                           private val studyID: Int, ){

    private val dlg = Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 배경을 투명하게 설정하여 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_member_leave_study)

        // 다이얼로그 표시
        dlg.show()

        // X 버튼 클릭 시 다이얼로그 닫기
        val ivClose: ImageView = dlg.findViewById(R.id.iv_close)
        ivClose.setOnClickListener {
            dlg.dismiss() // 다이얼로그 닫기
        }

        val btnTakeCharge: Button = dlg.findViewById(R.id.btn_take_charge)
        btnTakeCharge.setOnClickListener {
            dlg.dismiss() // 현재 다이얼로그 닫기
            showMemberLeaveSuccessDialog() // ✅ 새로운 다이얼로그 호출
        }


        // 취소 버튼 클릭 시 다이얼로그 닫기
        val btnCancel: Button = dlg.findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss() // 다이얼로그 닫기
        }

    }

    private fun showMemberLeaveSuccessDialog() {
        val successDialog = MemberLeaveSuccessDialog(context,studyID)  // ✅ 다이얼로그 객체 생성
        successDialog.start()  // ✅ 다이얼로그 표시
    }

}
