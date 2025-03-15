package com.example.spoteam_android.ui.mypage.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.databinding.FragmentCancelBinding
import com.example.spoteam_android.databinding.FragmentCommunityPrivacyBinding

class CancelSPOTFragment : Fragment() {
    private lateinit var binding: FragmentCancelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCancelBinding.inflate(inflater, container, false)


        binding.prevIv.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        binding.realCancel.setOnClickListener{
            val dlg = CancelDialog(requireContext())
//            dlg.setMemberId()
            dlg.start()
        }

        return binding.root
    }

}
