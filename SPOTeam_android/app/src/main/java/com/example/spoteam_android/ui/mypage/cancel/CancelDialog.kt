package com.example.spoteam_android.ui.mypage.cancel

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberIntroInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelDialog(private val context: Context) {

    private val dlg = android.app.Dialog(context)
    private var memberId : Int? = null

    fun setMemberId(memberId: Int) {
        this.memberId = memberId
    }

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 커스텀 다이얼로그 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_cancel_spot)

        val btnMove1 = dlg.findViewById<TextView>(R.id.cancel_ok_bt)
        btnMove1.setOnClickListener {
            val acceptDelete = CancelFinishDialog(context)
            acceptDelete.start()
            dlg.dismiss()
        }

        val btnMove2 = dlg.findViewById<TextView>(R.id.cancel_cancel_bt)
        btnMove2.setOnClickListener {
            dlg.dismiss()
        }

        val btnMove3 = dlg.findViewById<ImageView>(R.id.cancel_x_iv)
        btnMove3.setOnClickListener {
            dlg.dismiss()
        }

        dlg.show()
    }

    private fun deleteMember() {
        //회원 탈퇴 로직 삽입
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

}
