package com.example.spoteam_android.presentation.study.register.online

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.spoteam_android.R
import com.example.spoteam_android.core.util.parseLocationTsv
import com.example.spoteam_android.databinding.FragmentOnlineStudyBinding
import com.example.spoteam_android.domain.study.entity.StudyRegisterRequest
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel
import com.example.spoteam_android.presentation.study.register.member.MemberStudyFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class OnlineStudyFragment : Fragment() {
    private lateinit var binding: FragmentOnlineStudyBinding

    private val registerViewModel: StudyRegisterViewModel by activityViewModels()

    private var isLocationPlusVisible: Boolean = false
    private var selectedLocationCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt("studyId", -1)?.let {
            if (it != -1) {
                registerViewModel.setStudyId(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineStudyBinding.inflate(inflater, container, false)

        parseLocationTsv(requireContext())

        if (registerViewModel.mode.value == StudyFormMode.CREATE) {
            registerViewModel.updateRegions(emptyList())
            arguments?.let {
                val address = it.getString("ADDRESS")
                val isOffline = it.getBoolean("IS_OFFLINE", false)
                selectedLocationCode = it.getString("CODE")

                if (isOffline) {
                    address?.let { addr -> updateChip(addr) }
                    setChipState(false)
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

        observeRegisterViewModel()
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
            goToPreviousFragment()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            val address = it.getString("ADDRESS")
            val code = it.getString("CODE")
            if (!address.isNullOrBlank() && !code.isNullOrBlank()) {
                updateChip(address)
                selectedLocationCode = code

                val current = registerViewModel.studyRequest.value
                val isOnlineSelected = binding.fragmentOnlineStudyChipOffline.isChecked.not()
                if (registerViewModel.mode.value == StudyFormMode.EDIT && current != null) {
                    val updated = current.copy(
                        isOnline = isOnlineSelected.not(),
                        regions = listOf(code)
                    )
                    registerViewModel.setStudyRequest(updated)
                }
            }
            it.clear()
        }
    }

    private fun observeRegisterViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.studyRequest.collect { request ->
                    if (request == null) return@collect

                    if (registerViewModel.mode.value == StudyFormMode.EDIT) {
                        binding.fragmentOnlineStudyTv.text = "스터디 정보 수정"
                    }

                    setChipState(request.isOnline)
                    isLocationPlusVisible = !request.isOnline
                    updateLocationPlusLayoutVisibility(isLocationPlusVisible)

                    val newCode = request.regions?.firstOrNull()
                    if (!request.isOnline && newCode != null && newCode != selectedLocationCode) {
                        updateChip(registerViewModel.uploadedImageUrl.value ?: "")
                        selectedLocationCode = newCode
                    } else if (!request.isOnline && newCode == null) {
                        binding.locationChip.visibility = View.GONE
                    }

                    updateNextButtonState()
                }
            }
        }
    }

    private fun updateChip(address: String) {
        binding.fragmentOnlineStudyLocationPlusCl.visibility = View.VISIBLE
        binding.locationChip.apply {
            visibility = View.VISIBLE
            text = address
        }
        binding.locationChip.post {
            updateNextButtonState()
        }
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
        binding.fragmentOnlineStudyChipOnline.isChecked = false
        binding.fragmentOnlineStudyChipOffline.isChecked = false
        selectedChip.isChecked = true

        val isOnline = selectedChip.id == R.id.fragment_online_study_chip_online
        isLocationPlusVisible = !isOnline

        val current = registerViewModel.studyRequest.value
            ?: StudyRegisterRequest(
                themes = emptyList(),
                title = "",
                goal = "",
                introduction = "",
                isOnline = true,
                profileImage = null,
                regions = null,
                maxPeople = 0,
                gender = "ALL",
                minAge = 0,
                maxAge = 0,
                fee = 0,
                hasFee = false
            )

        val updated = current.copy(
            isOnline = isOnline,
            regions = if (isOnline) null else current.regions ?: emptyList()
        )
        registerViewModel.setStudyRequest(updated)

        updateLocationPlusLayoutVisibility(isLocationPlusVisible)
        updateNextButtonState()
    }

    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            selectedLocationCode = null

            if (registerViewModel.mode.value == StudyFormMode.EDIT) {
                val current = registerViewModel.studyRequest.value
                current?.let {
                    val cleared = it.copy(regions = null)
                    registerViewModel.setStudyRequest(cleared)
                }
            }

            updateNextButtonState()
        }
    }

    private fun setChipState(isOnline: Boolean) {
        binding.fragmentOnlineStudyChipOnline.isChecked = isOnline
        binding.fragmentOnlineStudyChipOffline.isChecked = !isOnline
        binding.fragmentOnlineStudyBt.isEnabled = isOnline
    }

    private fun updateLocationPlusLayoutVisibility(visible: Boolean) {
        val locationBtn = binding.fragmentOnlineStudyLocationPlusBt
        val nextBtn = binding.fragmentOnlineStudyBt
        val params = nextBtn.layoutParams as ViewGroup.MarginLayoutParams

        if (visible) {
            locationBtn.visibility = View.VISIBLE
            params.marginStart = 6.dpToPx()
            nextBtn.layoutParams = params

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.fragmentOnlineStudyButtonContainer)

            constraintSet.connect(locationBtn.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(locationBtn.id, ConstraintSet.END, nextBtn.id, ConstraintSet.START)

            constraintSet.connect(nextBtn.id, ConstraintSet.START, locationBtn.id, ConstraintSet.END)
            constraintSet.connect(nextBtn.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            constraintSet.applyTo(binding.fragmentOnlineStudyButtonContainer)
        } else {
            locationBtn.visibility = View.GONE
            params.marginStart = 0
            nextBtn.layoutParams = params

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.fragmentOnlineStudyButtonContainer)

            constraintSet.connect(nextBtn.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(nextBtn.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

            constraintSet.clear(locationBtn.id)

            constraintSet.applyTo(binding.fragmentOnlineStudyButtonContainer)
        }
    }

    private fun updateNextButtonState() {
        val isOnline = binding.fragmentOnlineStudyChipOnline.isChecked
        val hasChip = binding.locationChip.visibility == View.VISIBLE

        binding.fragmentOnlineStudyBt.visibility = View.VISIBLE
        binding.fragmentOnlineStudyBt.isEnabled = isOnline || hasChip
        binding.fragmentOnlineStudyLocationPlusBt.visibility = if (isOnline) View.GONE else View.VISIBLE
    }

    private fun saveData() {
        val current = registerViewModel.studyRequest.value ?: return
        val regions = selectedLocationCode?.let { listOf(it) } ?: emptyList()
        val preservedProfileImage = current.profileImage ?: registerViewModel.localImageUri.value

        val updated = current.copy(
            isOnline = binding.fragmentOnlineStudyChipOnline.isChecked,
            regions = if (binding.fragmentOnlineStudyChipOnline.isChecked) null else regions,
            profileImage = preservedProfileImage
        )
        registerViewModel.setStudyRequest(updated)
    }

    private fun clearChipSelection() {
        binding.fragmentOnlineStudyChipOnline.isChecked = false
        binding.fragmentOnlineStudyChipOffline.isChecked = false
    }

    private fun goToNextFragment() {
        val nextFragment = MemberStudyFragment().apply {
            arguments = Bundle().apply {
                putSerializable("mode", registerViewModel.mode.value)
                registerViewModel.studyId.value?.let { putInt("studyId", it) }
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun goToPreviousFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun goToLocationFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, LocationStudyFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun Int.dpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}
