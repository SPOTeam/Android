package com.example.spoteam_android.ui.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentOnlineStudyBinding

class OnlineStudyFragment : Fragment() {
    private lateinit var binding: FragmentOnlineStudyBinding
    private var isOffline: Boolean = false
    private var isLocationPlusVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineStudyBinding.inflate(inflater, container, false)

        arguments?.let {
            val address = it.getString("ADDRESS")
            isOffline = it.getBoolean("IS_OFFLINE", false)

            address?.let { addr ->
                updateChip(addr)
            }

            // 상태에 따라 초기 설정
            setChipState(isOffline)
            isLocationPlusVisible = isOffline
            updateLocationPlusLayoutVisibility(isLocationPlusVisible)
        }

        setupChipGroupListener()
        setupChipCloseListener()

        binding.fragmentOnlineStudyLocationPlusBt.setOnClickListener {
            goToNextFragment()
        }

        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_online_study_chip_online -> {
                    binding.fragmentOnlineStudyLocationPlusCl.visibility = View.GONE
                    isLocationPlusVisible = false
                }
                R.id.fragment_online_study_chip_offline -> {
                    binding.fragmentOnlineStudyLocationPlusCl.visibility = View.VISIBLE
                    isLocationPlusVisible = true
                }
            }
            updateNextButtonState()
        }
    }

    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            updateNextButtonState() // Chip 삭제 시 버튼 비활성화
        }
    }


    private fun setChipState(isOffline: Boolean) {
        if (isOffline) {
            binding.fragmentOnlineStudyChipgroup.check(R.id.fragment_online_study_chip_offline)
        } else {
            binding.fragmentOnlineStudyChipgroup.check(R.id.fragment_online_study_chip_online)
        }
    }

    private fun updateLocationPlusLayoutVisibility(visible: Boolean) {
        binding.fragmentOnlineStudyLocationPlusCl.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateNextButtonState() {
        binding.fragmentOnlineStudyBt.isEnabled =
            (binding.fragmentOnlineStudyChipgroup.checkedChipId == R.id.fragment_online_study_chip_online
                    || binding.locationChip.visibility == View.VISIBLE)
    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, LocationStudyFragment()) // 변경할 Fragment로 교체
        transaction.commit()
    }

    private fun extractAddressUntilDong(address: String): String {
        val index = address.indexOf("동")
        return if (index != -1) {
            address.substring(0, index + 1)
        } else {
            address
        }
    }

    fun updateChip(address: String) {
        val truncatedAddress = extractAddressUntilDong(address)
        binding.locationChip.apply {
            visibility = View.VISIBLE
            text = truncatedAddress
        }
        updateNextButtonState() // 주소 업데이트 후 버튼 상태 재조정
    }
}
