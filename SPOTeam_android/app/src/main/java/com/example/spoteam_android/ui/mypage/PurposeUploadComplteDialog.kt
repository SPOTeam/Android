package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.example.spoteam_android.R

class PurposeUploadComplteDialog(private val context: Context) {
    private val dlg = android.app.Dialog(context)

    fun start(fragmentManager: FragmentManager) {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_purpose_upload_complete)

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)

        val tvNickname = dlg.findViewById<TextView>(R.id.dialog_complete_content_username_tv)

        if (email != null) {
            val nickname = getNicknameFromPreferences(email)
            tvNickname.text = nickname ?: "닉네임 없음"  // 닉네임이 없는 경우의 기본 값
        } else {
            tvNickname.text = "이메일 없음"  // 이메일이 없는 경우의 기본 값
        }

        val btnMove = dlg.findViewById<Button>(R.id.dialog_complete_bt)
        btnMove.setOnClickListener {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm, PurposePreferenceFragment())
            transaction.commit()
            dlg.dismiss()
        }

        // 다이얼로그 표시
        dlg.show()
    }

    private fun getNicknameFromPreferences(email: String): String? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("${email}_nickname", null)
    }
}
