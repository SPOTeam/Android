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
import com.bumptech.glide.Glide
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentOnlineStudyBinding
import com.example.spoteam_android.util.parseLocationTsv
import com.google.android.material.chip.Chip

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

        selectedLocationCode = null
        val locationList = parseLocationTsv(requireContext())
        viewModel.setLocationList(locationList)

        if (viewModel.mode.value == StudyFormMode.CREATE) {
            viewModel.clearRegions()
            arguments?.let {
                val address = it.getString("ADDRESS")
                val isOffline = it.getBoolean("IS_OFFLINE", false)
                selectedLocationCode = it.getString("CODE")

                if (isOffline) {
                    address?.let { addr -> updateChip(addr) }
                    setChipState(false) // 오프라인일 경우 offline 체크
                    isLocationPlusVisible = true
                    updateLocationPlusLayoutVisibility(true)
                } else {
                    clearChipSelection()
                    updateLocationPlusLayoutVisibility(false)
                    binding.fragmentOnlineStudyBt.isEnabled = false
                }
            } ?: run {
                clearChipSelection()
                updateLocationPlusLayoutVisibility(false)
                binding.fragmentOnlineStudyBt.isEnabled = false
            }
        }

        viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
            if (viewModel.mode.value == StudyFormMode.EDIT) {
                Log.d("OnlineStudyFragment", "observe - isOnline: ${request.isOnline}, regions: ${request.regions}")
                setChipState(request.isOnline)
                isLocationPlusVisible = !request.isOnline
                updateLocationPlusLayoutVisibility(isLocationPlusVisible)

                request.regions?.firstOrNull()?.let { code ->
                    selectedLocationCode = code
                    val address = viewModel.findAddressFromCode(code)
                    address?.let { updateChip(it) }
                }
            }
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
    private fun clearChipSelection() {
        binding.fragmentOnlineStudyChipOnline.isChecked = false
        binding.fragmentOnlineStudyChipOffline.isChecked = false
    }


    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipOnline.setOnClickListener {
            selectChip(binding.fragmentOnlineStudyChipOnline)
        }

        binding.fragmentOnlineStudyChipOffline.setOnClickListener {
            selectChip(binding.fragmentOnlineStudyChipOffline)
        }
    }

    private fun selectChip(selectedChip: Chip) {
        // Chip UI 업데이트
        binding.fragmentOnlineStudyChipOnline.isChecked = false
        binding.fragmentOnlineStudyChipOffline.isChecked = false
        selectedChip.isChecked = true

        val isOnline = selectedChip.id == R.id.fragment_online_study_chip_online
        isLocationPlusVisible = !isOnline

        // ✅ 등록 모드에서만 ViewModel 값 업데이트
        if (viewModel.mode.value == StudyFormMode.CREATE) {
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

            viewModel.setStudyData(
                title = currentStudyRequest.title,
                goal = currentStudyRequest.goal,
                introduction = currentStudyRequest.introduction,
                isOnline = isOnline,
                profileImage = currentStudyRequest.profileImage,
                regions = if (isOnline) null else currentStudyRequest.regions ?: mutableListOf(),
                maxPeople = currentStudyRequest.maxPeople,
                gender = currentStudyRequest.gender,
                minAge = currentStudyRequest.minAge,
                maxAge = currentStudyRequest.maxAge,
                fee = currentStudyRequest.fee
            )
        }

        updateLocationPlusLayoutVisibility(isLocationPlusVisible)
        updateNextButtonState()
    }





    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            updateNextButtonState()
        }
    }

    private fun setChipState(isOnline: Boolean) {
        binding.fragmentOnlineStudyChipOnline.isChecked = isOnline
        binding.fragmentOnlineStudyChipOffline.isChecked = !isOnline
    }


    private fun updateLocationPlusLayoutVisibility(visible: Boolean) {
        binding.fragmentOnlineStudyLocationPlusCl.visibility = if (visible) View.VISIBLE else View.GONE
        binding.fragmentOnlineStudyBt.visibility = if (visible) View.GONE else View.VISIBLE
        binding.fragmentOnlineStudyLocationPlusBt.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateNextButtonState() {
        val isOnline = binding.fragmentOnlineStudyChipOnline.isChecked
        val hasChip = binding.locationChip.visibility == View.VISIBLE

        if (isOnline) {
            binding.fragmentOnlineStudyBt.isEnabled = true
            binding.fragmentOnlineStudyBt.visibility = View.VISIBLE
            binding.fragmentOnlineStudyLocationPlusBt.visibility = View.GONE
        } else {
            if (hasChip) {
                binding.fragmentOnlineStudyBt.isEnabled = true
                binding.fragmentOnlineStudyBt.visibility = View.VISIBLE
                binding.fragmentOnlineStudyLocationPlusBt.visibility = View.GONE
            } else {
                binding.fragmentOnlineStudyBt.isEnabled = false
                binding.fragmentOnlineStudyBt.visibility = View.GONE
                binding.fragmentOnlineStudyLocationPlusBt.visibility = View.VISIBLE
            }
        }
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

//    private fun extractAddressUntilDong(address: String): String {
//        val index = address.indexOf("동")
//        return if (index != -1) {
//            address.substring(0, index + 1)
//        } else {
//            address
//        }
//    }

    fun updateChip(address: String) {
//        val truncatedAddress = extractAddressUntilDong(address)
        binding.locationChip.apply {
            visibility = View.VISIBLE
            text = address
        }
        binding.locationChip.post {
            updateNextButtonState()
        } // 하 이코드 떄문에 개고생..
    }
}