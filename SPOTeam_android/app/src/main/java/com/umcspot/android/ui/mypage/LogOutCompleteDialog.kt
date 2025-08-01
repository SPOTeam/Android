package com.umcspot.android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.umcspot.android.R

class LogOutCompleteDialog(
    private val context: Context,
    private val onConfirm: () -> Unit
) {
    private val dlg = Dialog(context)

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_logout_complete)

        val confirmButton: Button = dlg.findViewById(R.id.dialog_complete_bt)
        val closeButton: ImageView = dlg.findViewById(R.id.close_iv)

        confirmButton.setOnClickListener {
            onConfirm()
            dlg.dismiss()
        }

        closeButton.setOnClickListener {
            onConfirm()
            dlg.dismiss()
        }

        dlg.show()
    }
}
