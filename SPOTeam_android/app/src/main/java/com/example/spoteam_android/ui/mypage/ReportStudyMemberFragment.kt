package com.example.spoteam_android

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.databinding.FragmentReportStudymemberBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.StudyMemberResponse
import com.example.spoteam_android.ui.mypage.ReportStudyCrewMemberRVAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportStudyMemberFragment(private val context: Context, private val studyId: Int) : ReportCompleteListener {
    private val dlg = BottomSheetDialog(context, R.style.CustomBottomSheetDialogTheme)
    private var binding: FragmentReportStudymemberBinding? = null // ✅ nullable 변경
    private var selectedCrewMember: Int = -1

    fun start() {
        Log.d("ReportStudyCrewDialog", "StudyId : $studyId")

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // ✅ 바인딩 초기화
        binding = FragmentReportStudymemberBinding.inflate(dlg.layoutInflater)
        dlg.setContentView(binding!!.root) // ✅ null 체크 후 사용

        val heightInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            700f,
            context.resources.displayMetrics
        ).toInt()

        dlg.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            heightInPx
        )

        val btnExit = binding?.reportCrewPrevIv
        btnExit?.setOnClickListener {
            dlg.dismiss()
        }

        fetchStudyMember() // ✅ 멤버 리스트 가져오기

        binding?.reportCrewTv?.setOnClickListener{
            startReportContentDialog(studyId)
        }

        dlg.show()
    }

    private fun startReportContentDialog(studyId: Int) {
        val reportContentDialog = ReportStudyMemberContentFragment(context, studyId, selectedCrewMember, this)
        reportContentDialog.start()
    }

    private fun fetchStudyMember() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyMembers(studyId)
            .enqueue(object : Callback<StudyMemberResponse> {
                override fun onResponse(
                    call: Call<StudyMemberResponse>,
                    response: Response<StudyMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val studyMemberResponse = response.body()
                        if (studyMemberResponse?.isSuccess == "true") {
                            val studyMembers = studyMemberResponse.result.members
                            initMembersRecycler(studyMembers)  // ✅ 멤버 데이터 설정
                        }
                    } else {
                        showError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<StudyMemberResponse>, t: Throwable) {
                    Log.e("ReportStudyCrewDialog", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initMembersRecycler(studyMembers: List<MembersDetail>) {
        binding?.reportMemberRv?.layoutManager = GridLayoutManager(context, 5)

        val dataRVAdapter = ReportStudyCrewMemberRVAdapter(studyMembers, object :
            ReportStudyCrewMemberRVAdapter.OnMemberClickListener {
            override fun onProfileClick(member: MembersDetail) {
                selectedCrewMember = member.memberId
                Log.d("ReportCrew", "ReportCrewMember : $selectedCrewMember")
                checkMemberId()
            }
        })

        binding?.reportMemberRv?.adapter = dataRVAdapter
    }

    private fun checkMemberId() {
        if(selectedCrewMember != -1) {
            binding?.reportCrewTv?.isEnabled = true
        }
    }


    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onReportCompleted() {
        dlg.dismiss()
    }
}
