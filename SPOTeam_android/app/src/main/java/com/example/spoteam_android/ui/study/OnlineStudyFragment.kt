package com.example.spoteam_android.ui.study

import LocationStudyFragment
import StudyRequest
import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentOnlineStudyBinding
import com.example.spoteam_android.ui.study.MemberStudyFragment

class OnlineStudyFragment : Fragment() {
    private lateinit var binding: FragmentOnlineStudyBinding
    private val viewModel: StudyViewModel by activityViewModels()

    private var isLocationPlusVisible: Boolean = false
    private var selectedLocationCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnlineStudyBinding.inflate(inflater, container, false)

        arguments?.let {
            val address = it.getString("ADDRESS")
            val isOffline = it.getBoolean("IS_OFFLINE", false)
            selectedLocationCode = it.getString("CODE")

            address?.let { addr ->
                updateChip(addr)
            }

            setChipState(isOffline)
            isLocationPlusVisible = isOffline
            updateLocationPlusLayoutVisibility(isLocationPlusVisible)
        }

        setupChipGroupListener()
        setupChipCloseListener()

        binding.fragmentOnlineStudyBt.setOnClickListener {
            saveData()
            goToNextFragment()
        }

        binding.fragmentOnlineStudyLocationPlusBt.setOnClickListener {
            goToLocationFragment()
        }

        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_online_study_chip_online -> {
                    isLocationPlusVisible = false
                    viewModel.setStudyData(
                        title = "",
                        goal = "",
                        introduction = "",
                        isOnline = true,
                        profileImage = viewModel.studyRequest.value?.profileImage,
                        regions = viewModel.studyRequest.value?.regions ?: emptyList(),
                        maxPeople = 0,
                        gender = Gender.UNKNOWN,
                        minAge = 0,
                        maxAge = 0,
                        fee = 0
                    )
                }
                R.id.fragment_online_study_chip_offline -> {
                    isLocationPlusVisible = true
                    viewModel.setStudyData(
                        title = "",
                        goal = "",
                        introduction = "",
                        isOnline = false,
                        profileImage = viewModel.studyRequest.value?.profileImage,
                        regions = viewModel.studyRequest.value?.regions ?: emptyList(),
                        maxPeople = 0,
                        gender = Gender.UNKNOWN,
                        minAge = 0,
                        maxAge = 0,
                        fee = 0
                    )
                }
            }
            updateLocationPlusLayoutVisibility(isLocationPlusVisible)
            updateNextButtonState()
        }
    }

    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            updateNextButtonState()
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

    private fun saveData() {
        val currentStudyRequest = viewModel.studyRequest.value ?: StudyRequest(
            themes = listOf("어학"),
            title = "",
            goal = "",
            introduction = "",
            isOnline = true,
            profileImage = null, // 기본값
            regions = mutableListOf(),
            maxPeople = 0,
            gender = Gender.UNKNOWN,
            minAge = 0,
            maxAge = 0,
            fee = 0
        )

        val currentRegions = currentStudyRequest.regions.toMutableList()
        selectedLocationCode?.let { code ->
            if (code !in currentRegions) {
                currentRegions.add(code)
            }
        }

        viewModel.setStudyData(
            title = currentStudyRequest.title,
            goal = currentStudyRequest.goal,
            introduction = currentStudyRequest.introduction,
            isOnline = currentStudyRequest.isOnline,
            profileImage = currentStudyRequest.profileImage, // 설정
            regions = currentRegions,
            maxPeople = currentStudyRequest.maxPeople,
            gender = currentStudyRequest.gender,
            minAge = currentStudyRequest.minAge,
            maxAge = currentStudyRequest.maxAge,
            fee = currentStudyRequest.fee
        )
    }

    private fun goToNextFragment() {
        Log.d("OnlineStudyFragment", "Selected Location Code: $selectedLocationCode")
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MemberStudyFragment())
        transaction.commit()
    }

    private fun goToLocationFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, LocationStudyFragment())
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
        updateNextButtonState()
    }
}
