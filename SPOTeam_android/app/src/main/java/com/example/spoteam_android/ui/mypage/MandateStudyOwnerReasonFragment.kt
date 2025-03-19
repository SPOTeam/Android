package com.example.spoteam_android.ui.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMandateStudyOwnerReasonBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

        binding.btnTakeCharge.setOnClickListener{
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}
