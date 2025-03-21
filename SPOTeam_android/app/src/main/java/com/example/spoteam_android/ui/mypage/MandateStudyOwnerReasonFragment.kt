package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import com.example.spoteam_android.HostWithDrawl
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.WithdrawHostRequest
import com.example.spoteam_android.databinding.FragmentMandateStudyOwnerReasonBinding
import com.example.spoteam_android.ui.interestarea.GetHostInterface
import com.example.spoteam_android.weather.Weather
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MandateStudyOwnerReasonFragment(private val studyId: Int, private val memberId: Int) : BottomSheetDialogFragment() {
    private var _binding: FragmentMandateStudyOwnerReasonBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMandateStudyOwnerReasonBinding.inflate(inflater, container, false)

        // 닫기 버튼 클릭 시 프래그먼트 종료
        binding.ivClose.setOnClickListener {
            dismiss()
        }

        binding.popupEditText.addTextChangedListener { text ->
            val isTextNotEmpty = !text.isNullOrBlank()
            binding.btnTakeCharge.isEnabled = isTextNotEmpty
        }


        binding.btnTakeCharge.setOnClickListener{
            val reasonText = binding.popupEditText.text.toString()

            requestWithdrawHost(studyId,memberId,reasonText)
            showMemberLeaveSuccessDialog()

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

    private fun showMemberLeaveSuccessDialog() {
        val successDialog = HostLeaveStudySuccessDialog(requireContext())  // ✅ 다이얼로그 객체 생성
        successDialog.start()  // ✅ 다이얼로그 표시
    }

    private fun requestWithdrawHost(studyId: Int, newHostId: Int, reason: String) {
        val service = RetrofitInstance.retrofit.create(GetHostInterface::class.java)

        val request = WithdrawHostRequest(newHostId, reason)
        val call = service.withDrawlHost(studyId, request)


        call.enqueue(object : Callback<HostWithDrawl> {
            override fun onResponse(call: Call<HostWithDrawl>, response: Response<HostWithDrawl>) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    Log.d("MandateStudyOwner", """
                    ✅ [호스트 위임 성공]
                    - 응답 코드: ${response.code()} 
                    - 메시지: ${response.body()?.message}
                    - 전체 응답: ${response.body()}
                """.trimIndent())
                    setFragmentResult("host_withdraw_success", Bundle())  // 여기!
                    dismiss()
                } else {
                    val errorBody = response.errorBody()?.string()

                }
            }

            override fun onFailure(call: Call<HostWithDrawl>, t: Throwable) {

            }
        })
    }


}
