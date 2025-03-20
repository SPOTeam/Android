package com.example.spoteam_android.ui.mypage

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.ContentResponse
import com.example.spoteam_android.ui.community.EndStudyResponse
import com.example.spoteam_android.ui.community.MemberAcceptResponse
import com.example.spoteam_android.ui.community.MemberIntroInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EndStudyFinishDialog(val context: Context) {

    private val dlg = android.app.Dialog(context)
    private var resultContext : String = ""

    fun setContext(text : String) {
        resultContext = text
    }


    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dlg.setContentView(R.layout.dialog_finish_end_study)

        val okButton = dlg.findViewById<Button>(R.id.dialog_complete_study_finish_bt)
        okButton.setOnClickListener {
//            Toast.makeText(context, resultContext,Toast.LENGTH_SHORT).show()
            dlg.dismiss()
        }

        val closeButton = dlg.findViewById<ImageView>(R.id.cancel_x_finish_study_iv)
        closeButton.setOnClickListener {
//            Toast.makeText(context, resultContext,Toast.LENGTH_SHORT).show()
            dlg.dismiss()
        }


        dlg.show()
    }
}
