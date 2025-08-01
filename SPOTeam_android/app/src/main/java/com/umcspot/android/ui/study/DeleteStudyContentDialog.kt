package com.umcspot.android.ui.study

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import androidx.fragment.app.FragmentManager
import com.umcspot.android.R


class DeleteStudyContentDialog(private val context: Context)  {
    private val dlg = android.app.Dialog(context)

    fun start(fragmentManager: FragmentManager) {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_content_delete)

        val btnMove = dlg.findViewById<Button>(R.id.dialog_delete_complete_bt)
        btnMove.setOnClickListener {
            (context as? MyStudyPostContentActivity)?.finish()
        }

        // 다이얼로그 표시
        dlg.show()
    }
}