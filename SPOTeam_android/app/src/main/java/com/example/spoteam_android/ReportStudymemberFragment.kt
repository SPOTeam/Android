package com.example.spoteam_android

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageButton
import android.widget.Toast
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReportStudymemberFragment(private val context: Context)  {
    private val dlg = BottomSheetDialog(context,R.style.CustomBottomSheetDialogTheme)
    private var selectedImageView: ImageView? = null

    fun start() {

        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.fragment_report_studymeber)

        val heightInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            700f,
            context.resources.displayMetrics
        ).toInt()

        dlg.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            heightInPx
        )



        val btnExit = dlg.findViewById<ImageView>(R.id.write_content_prev_iv)
        btnExit?.setOnClickListener {
            dlg.dismiss()
        }

        val check = dlg.findViewById<ImageView>(R.id.ic_small_check)
        val txreport =  dlg.findViewById<TextView>(R.id.tx_report_complete)
        val btnSubmit = dlg.findViewById<ImageView>(R.id.imgbtn_report)
        val txreason = dlg.findViewById<TextView>(R.id.tx_reason_report)
        val et = dlg.findViewById<TextView>(R.id.popup_edit_text)

        btnSubmit?.setOnClickListener {
            check?.visibility = View.VISIBLE
            txreport?.visibility = View.VISIBLE
            txreason?.visibility = View.GONE
            et?.visibility = View.GONE
            btnSubmit.visibility = View.GONE

            Handler(Looper.getMainLooper()).postDelayed({
                dlg.dismiss()
            }, 2000)
        }

        val userImageViews = listOf(
            dlg.findViewById<ImageView>(R.id.host_profile),
            dlg.findViewById<ImageView>(R.id.crew1_profile),
            dlg.findViewById<ImageView>(R.id.crew2_profile),
            dlg.findViewById<ImageView>(R.id.crew3_profile),
            dlg.findViewById<ImageView>(R.id.crew4_profile),
            dlg.findViewById<ImageView>(R.id.crew5_profile),
            dlg.findViewById<ImageView>(R.id.crew6_profile),
            dlg.findViewById<ImageView>(R.id.crew7_profile),
            dlg.findViewById<ImageView>(R.id.crew8_profile),
            dlg.findViewById<ImageView>(R.id.crew9_profile)
        ).filterNotNull()



        userImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                highlightImageView(imageView)
            }
        }


        // 다이얼로그 표시
        dlg.show()
    }

    private fun highlightImageView(imageView: ImageView) {
        selectedImageView?.setBackgroundResource(0)
        imageView.setBackgroundResource(R.drawable.selected_border)  // 선택된 ImageView에 빨간 테두리 설정
        selectedImageView = imageView  // 현재 선택된 ImageView 업데이트
    }
}