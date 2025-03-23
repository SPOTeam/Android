package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentMandateStudyOwnerBinding
import com.example.spoteam_android.ui.community.CommunityAPIService
import com.example.spoteam_android.ui.community.MembersDetail
import com.example.spoteam_android.ui.community.ReportCrewRequest
import com.example.spoteam_android.ui.community.ReportCrewResponse
import com.example.spoteam_android.ui.community.StudyMemberResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MandateStudyOwnerFragment(private val studyId: Int) : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)

    }

    private var _binding: FragmentMandateStudyOwnerBinding? = null
    private val binding get() = _binding!!
    private var selectedMember: MembersDetail? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMandateStudyOwnerBinding.inflate(inflater, container, false)

        binding.btnTakeCharge.isEnabled = false
        // 닫기 버튼 클릭 시 프래그먼트 종료
        binding.ivClose.setOnClickListener {
            dismiss()
        }

//        // 완료 버튼 활성화 로직 (예시: EditText 입력 감지)
//        binding.popupEditText.addTextChangedListener {
//            binding.tvComplete.isEnabled = !it.isNullOrBlank()
//        }
//
//        // 완료 버튼 클릭 시 처리
//        binding.tvComplete.setOnClickListener {
//            // 여기에 위임 로직 추가 (예: API 호출 또는 데이터 전달)
//            binding.ivSuccess.visibility = View.VISIBLE
//            binding.finishReportTv.visibility = View.VISIBLE
//            binding.tvComplete.isEnabled = false // 완료 후 비활성화
//        }
        fetchStudyMember() // ✅ 멤버 리스트 가져오기
        binding.btnTakeCharge.setOnClickListener{
            selectedMember?.let { member ->
                openMandateStudyOwnerReasonFragment(studyId, member.memberId)
                dismiss()
            }
        }

        return binding.root
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
                    }
                }

                override fun onFailure(call: Call<StudyMemberResponse>, t: Throwable) {
                    Log.e("ReportStudyCrewDialog", "Failure: ${t.message}", t)
                }
            })
    }

    private fun initMembersRecycler(studyMembers: List<MembersDetail>) {
        binding?.rvMandateHost?.layoutManager = GridLayoutManager(context, 5)

        val dataRVAdapter = ReportStudyCrewMemberRVAdapter(studyMembers, object :
            ReportStudyCrewMemberRVAdapter.OnMemberClickListener {
            override fun onProfileClick(member: MembersDetail) {
                Log.d("MandateStudyOwner", "Clicked memberId: ${member.memberId}")
                selectedMember = member  // 선택된 멤버 저장
                binding.btnTakeCharge.isEnabled = true  // 버튼 활성화

            }
        })

        binding?.rvMandateHost?.adapter = dataRVAdapter
    }

    private fun openMandateStudyOwnerReasonFragment(studyId: Int,memberId: Int) {
        val mandateReasonFragment = MandateStudyOwnerReasonFragment(studyId,memberId)
        mandateReasonFragment.show(parentFragmentManager, "MandateStudyOwnerReasonFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
