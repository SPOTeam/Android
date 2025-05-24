package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMandateStudyOwnerBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.StudyMemberResponse

class MandateStudyOwnerFragment(
    private val context: Context,
    private val studyId: Int,
    private val onComplete: (() -> Unit)? = null
) {
    private val dlg = Dialog(context)
    private var binding: FragmentMandateStudyOwnerBinding? = null
    private var selectedMember: MembersDetail? = null

    fun start() {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding = FragmentMandateStudyOwnerBinding.inflate(dlg.layoutInflater)
        dlg.setContentView(binding!!.root)

        dlg.window?.setLayout(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 350f, context.resources.displayMetrics
            ).toInt()
        )
        dlg.setCanceledOnTouchOutside(true)

        binding?.btnTakeCharge?.isEnabled = false

        binding?.ivClose?.setOnClickListener {
            dlg.dismiss()
        }

        binding?.btnTakeCharge?.setOnClickListener {
            selectedMember?.let { member ->
                openMandateStudyOwnerReasonDialog(studyId, member.memberId)
                dlg.dismiss()
            }
        }

        fetchStudyMember()
        dlg.show()
    }

    private fun fetchStudyMember() {
        val service = RetrofitInstance.retrofit.create(CommunityAPIService::class.java)
        service.getStudyMembers(studyId)
            .enqueue(object : retrofit2.Callback<StudyMemberResponse> {
                override fun onResponse(
                    call: retrofit2.Call<StudyMemberResponse>,
                    response: retrofit2.Response<StudyMemberResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.isSuccess == "true") {
                            initMembersRecycler(body.result.members)
                        } else {
                            showError("불러오기 실패")
                        }
                    } else {
                        showError("오류 코드: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<StudyMemberResponse>, t: Throwable) {
                    Log.e("MandateDialog", "Failure: ${t.message}", t)
                    showError("네트워크 오류")
                }
            })
    }

    private fun initMembersRecycler(members: List<MembersDetail>) {
        val spanCount = 5
        val spacingDp = 8

        val layoutManager = GridLayoutManager(context, spanCount)
        binding?.rvMandateHost?.layoutManager = layoutManager

        // 이미 데코레이션이 여러 번 중첩되지 않도록 기존 데코레이션 제거
        binding?.rvMandateHost?.itemDecorationCount?.let { count ->
            for (i in count - 1 downTo 0) {
                binding?.rvMandateHost?.removeItemDecorationAt(i)
            }
        }

        binding?.rvMandateHost?.addItemDecoration(
            GridSpacingItemDecoration(spanCount, dpToPx(spacingDp))
        )

        val adapter = ReportStudyCrewMemberRVAdapter(members, object :
            ReportStudyCrewMemberRVAdapter.OnMemberClickListener {
            override fun onProfileClick(member: MembersDetail) {
                selectedMember = member
                binding?.btnTakeCharge?.isEnabled = true
            }
        })
        binding?.rvMandateHost?.adapter = adapter
    }


    private fun openMandateStudyOwnerReasonDialog(studyId: Int, memberId: Int) {
        val reasonDialog = MandateStudyOwnerReasonFragment(context, studyId, memberId, onComplete)
        reasonDialog.start()
    }

    private fun showError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
