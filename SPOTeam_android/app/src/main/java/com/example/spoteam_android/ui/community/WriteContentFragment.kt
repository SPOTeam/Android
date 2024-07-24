package com.example.spoteam_android.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spoteam_android.databinding.FragmentWriteContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteContentFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentWriteContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteContentBinding.inflate(inflater, container, false)
        binding.writeContentFinishBtn.setOnClickListener{
            dismiss()
        }

        return binding.root
    }
}
