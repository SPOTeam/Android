package com.spot.android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.spot.android.R

class LogOutDialog(
    private val context: Context,
    private val onConfirm: () -> Unit
) {
    private val dlg = Dialog(context)

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_logout)

        val logoutBtn: Button = dlg.findViewById(R.id.logout_bt)
        val cancelBtn: Button = dlg.findViewById(R.id.cancel_bt)
        val closeIv: ImageView = dlg.findViewById(R.id.close_iv)

        logoutBtn.setOnClickListener {
            onConfirm()
            dlg.dismiss()
        }

        cancelBtn.setOnClickListener { dlg.dismiss() }
        closeIv.setOnClickListener { dlg.dismiss() }

        dlg.show()
    }
}
