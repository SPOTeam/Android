package com.example.spoteam_android.ui.study

import LocationStudyFragment
import MemberStudyFragment
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

        // 새로 추가된 코드: regions 리스트 초기화
        selectedLocationCode = null
        viewModel.clearRegions()

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

        binding.fragmentOnlineStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }

        return binding.root
    }


    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            val currentStudyRequest = viewModel.studyRequest.value ?: StudyRequest(
                themes = listOf("어학"),
                title = "",
                goal = "",
                introduction = "",
                isOnline = true,
                profileImage = null,
                regions = null, // 기본값을 null로 설정
                maxPeople = 0,
                gender = Gender.UNKNOWN,
                minAge = 0,
                maxAge = 0,
                fee = 0,
                hasFee = false
            )

            when (checkedId) {
                R.id.fragment_online_study_chip_online -> {
                    isLocationPlusVisible = false
                    viewModel.setStudyData(
                        title = currentStudyRequest.title,
                        goal = currentStudyRequest.goal,
                        introduction = currentStudyRequest.introduction,
                        isOnline = true,
                        profileImage = currentStudyRequest.profileImage,
                        regions = null, // online 선택 시 regions를 null로 설정
                        maxPeople = currentStudyRequest.maxPeople,
                        gender = currentStudyRequest.gender,
                        minAge = currentStudyRequest.minAge,
                        maxAge = currentStudyRequest.maxAge,
                        fee = currentStudyRequest.fee
                    )
                }
                R.id.fragment_online_study_chip_offline -> {
                    isLocationPlusVisible = true
                    viewModel.setStudyData(
                        title = currentStudyRequest.title,
                        goal = currentStudyRequest.goal,
                        introduction = currentStudyRequest.introduction,
                        isOnline = false,
                        profileImage = currentStudyRequest.profileImage,
                        regions = currentStudyRequest.regions ?: mutableListOf(), // offline 선택 시 regions에 값을 설정
                        maxPeople = currentStudyRequest.maxPeople,
                        gender = currentStudyRequest.gender,
                        minAge = currentStudyRequest.minAge,
                        maxAge = currentStudyRequest.maxAge,
                        fee = currentStudyRequest.fee
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
            profileImage = null,
            regions = null,
            maxPeople = 0,
            gender = Gender.UNKNOWN,
            minAge = 0,
            maxAge = 0,
            fee = 0,
            hasFee = false
        )

        val currentRegions = currentStudyRequest.regions?.toMutableList() ?: mutableListOf()

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
            profileImage = currentStudyRequest.profileImage,
            regions = if (currentStudyRequest.isOnline) null else currentRegions,
            maxPeople = currentStudyRequest.maxPeople,
            gender = currentStudyRequest.gender,
            minAge = currentStudyRequest.minAge,
            maxAge = currentStudyRequest.maxAge,
            fee = currentStudyRequest.fee
        )
    }



    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MemberStudyFragment())
        transaction.commit()
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, IntroduceStudyFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
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