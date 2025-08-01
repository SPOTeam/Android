package com.umcspot.android

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.umcspot.android.databinding.DialogReportStudymemberBinding
import com.umcspot.android.ui.community.CommunityAPIService
import com.umcspot.android.ui.community.MembersDetail
import com.umcspot.android.ui.community.StudyMemberResponse
import com.umcspot.android.ui.mypage.ReportStudyCrewMemberRVAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect

class ReportStudyMemberFragment(
    private val context: Context,
    private val studyId: Int
) {

    private val dlg = Dialog(context) // ✅ 일반 Dialog로 변경
    private var binding: DialogReportStudymemberBinding? = null
    private var selectedCrewMember: Int = -1

    fun start() {
//        Log.d("ReportStudyCrewDialog", "StudyId : $studyId")

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = DialogReportStudymemberBinding.inflate(dlg.layoutInflater)
        dlg.setContentView(binding!!.root)

        dlg.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 420f, context.resources.displayMetrics
            ).toInt()
        )


        dlg.setCanceledOnTouchOutside(true) // 바깥 터치 시 닫기 허용

        binding?.reportCrewPrevIv?.setOnClickListener {
            dlg.dismiss()
        }

        fetchStudyMember()

        binding?.reportCrewTv?.setOnClickListener {
            startReportContentDialog(studyId)
        }

        dlg.show()
    }

    private fun startReportContentDialog(studyId: Int) {
        val reportContentDialog = ReportStudyMemberContentFragment(context, studyId, selectedCrewMember)
        reportContentDialog.start()
        dlg.dismiss()
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
                            initMembersRecycler(studyMembers)
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
        val spanCount = 5
        binding?.reportMemberRv?.layoutManager = GridLayoutManager(context, spanCount)

        val spacingInDp = 20
        binding?.reportMemberRv?.addItemDecoration(
            GridSpacingItemDecoration(spanCount, dpToPx(spacingInDp))
        )

        val dataRVAdapter = ReportStudyCrewMemberRVAdapter(studyMembers, object :
            ReportStudyCrewMemberRVAdapter.OnMemberClickListener {
            override fun onProfileClick(member: MembersDetail) {
                selectedCrewMember = member.memberId
//                Log.d("ReportCrew", "ReportCrewMember : $selectedCrewMember")
                checkMemberId()
            }
        })

        binding?.reportMemberRv?.adapter = dataRVAdapter
    }


    private fun checkMemberId() {
        if (selectedCrewMember != -1) {
            binding?.reportCrewTv?.isEnabled = true
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {

            outRect.bottom = spacing
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}
