package com.umcspot.android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.umcspot.android.HostWithDrawl
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.ui.interestarea.GetHostInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MemberLeaveStudyDialog(
    private val context: Context,
    private val studyID: Int,
    private val onComplete: (() -> Unit)? = null // 🔹 콜백 추가
) {
    private val dlg = Dialog(context)

    fun start() {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 배경을 투명하게 설정하여 radius 적용
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // 다이얼로그 레이아웃 설정
        dlg.setContentView(R.layout.dialog_member_leave_study)

        // 다이얼로그 표시
        dlg.show()

        // X 버튼 클릭 시 다이얼로그 닫기
        val ivClose: ImageView = dlg.findViewById(R.id.iv_close)
        ivClose.setOnClickListener {
            dlg.dismiss() // 다이얼로그 닫기
        }

        val btnTakeCharge: Button = dlg.findViewById(R.id.btn_take_charge)
        btnTakeCharge.setOnClickListener {
            withdrawFromStudy(studyID)
        }


        // 취소 버튼 클릭 시 다이얼로그 닫기
        val btnCancel: Button = dlg.findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss() // 다이얼로그 닫기
        }

    }

    private fun showMemberLeaveSuccessDialog() {
        val successDialog = MemberLeaveSuccessDialog(context, onComplete)  // ✅ 다이얼로그 객체 생성
        successDialog.start()  // ✅ 다이얼로그 표시
    }

    private fun withdrawFromStudy(studyId: Int) {
        val service = RetrofitInstance.retrofit.create(GetHostInterface::class.java)


        service.withDrawlMember(studyId).enqueue(object : Callback<HostWithDrawl> {
            override fun onResponse(call: Call<HostWithDrawl>, response: Response<HostWithDrawl>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("WithdrawStudy", """
                    ✅ [스터디 탈퇴 성공]
                    - 응답 코드: ${response.code()}
                    - 메시지: ${response.body()?.message}
                """.trimIndent())
                    // 예: 결과 전달 또는 화면 이동
                    (context as? FragmentActivity)?.supportFragmentManager?.setFragmentResult(
                        "study_withdraw_success", Bundle()
                    )
                    showMemberLeaveSuccessDialog() // ✅ 새로운 다이얼로그 호출
                    dlg.dismiss()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("WithdrawStudy", """
                    ❌ [스터디 탈퇴 실패]
                    - 응답 코드: ${response.code()}
                    - 메시지: ${response.body()?.message}
                    - 에러 바디: $errorBody
                """.trimIndent())
                }
            }

            override fun onFailure(call: Call<HostWithDrawl>, t: Throwable) {
                Log.e("WithdrawStudy", """
                ❌ [스터디 탈퇴 네트워크 실패]
                - 메시지: ${t.message}
                - 예외: $t
            """.trimIndent())
            }
        })
    }
}
