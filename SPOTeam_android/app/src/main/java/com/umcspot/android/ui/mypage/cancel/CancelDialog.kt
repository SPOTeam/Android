package com.umcspot.android.ui.mypage.cancel

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.umcspot.android.R

class CancelDialog(
    private val context: Context,
    private val onConfirm: () -> Unit
) {
    private val dlg = Dialog(context)

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_cancel_spot)

        val withdrawBtn: Button = dlg.findViewById(R.id.logout_bt)
        val cancelBtn: Button = dlg.findViewById(R.id.cancel_bt)
        val closeIv: ImageView = dlg.findViewById(R.id.close_iv)

        withdrawBtn.setOnClickListener {
            onConfirm()
            dlg.dismiss()
        }

        cancelBtn.setOnClickListener { dlg.dismiss() }
        closeIv.setOnClickListener { dlg.dismiss() }

        dlg.show()
    }
}