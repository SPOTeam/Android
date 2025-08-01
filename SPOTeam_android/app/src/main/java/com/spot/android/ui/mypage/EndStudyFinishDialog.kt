package com.spot.android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.spot.android.R

class EndStudyFinishDialog(
    val context: Context,
    val onComplete: (() -> Unit)? = null // 🔹 콜백 추가
) {

    private val dlg = android.app.Dialog(context)


    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_finish_end_study)

        val okButton = dlg.findViewById<Button>(R.id.dialog_complete_study_finish_bt)
        okButton.setOnClickListener {
//            Toast.makeText(context, resultContext,Toast.LENGTH_SHORT).show()
            onComplete?.invoke() // ✅ 여기서 fetchProgress() 실행
            dlg.dismiss()
        }

        val closeButton = dlg.findViewById<ImageView>(R.id.cancel_x_finish_study_iv)
        closeButton.setOnClickListener {
//            Toast.makeText(context, resultContext,Toast.LENGTH_SHORT).show()
            onComplete?.invoke() // ✅ 여기서 fetchProgress() 실행
            dlg.dismiss()
        }


        dlg.show()
    }
}
