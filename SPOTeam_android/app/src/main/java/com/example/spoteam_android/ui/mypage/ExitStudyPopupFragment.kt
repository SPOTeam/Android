
package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.R

class ExitStudyPopupFragment(private val context: Context,private val adapter: BoardAdapter, private val position: Int) {

    private val dlg = Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.fragment_exit_study_popup)

        val btnExit = dlg.findViewById<ImageButton>(R.id.imgbtn_exit)
        btnExit.setOnClickListener {
            Toast.makeText(context, "스터디에서 탈퇴했습니다.", Toast.LENGTH_SHORT).show()
            dlg.dismiss()
            adapter.removeItem(position)
        }



        val btnCancel = dlg.findViewById<ImageButton>(R.id.imgbtn_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        // 다이얼로그 표시
        dlg.show()
    }

}

