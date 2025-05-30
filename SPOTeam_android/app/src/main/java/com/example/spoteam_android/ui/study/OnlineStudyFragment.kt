    package com.example.spoteam_android.ui.study

    import LocationStudyFragment
    import MemberStudyFragment
    import StudyRequest
    import StudyViewModel
    import android.content.res.Resources
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.constraintlayout.widget.ConstraintSet
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
        ): View {
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

            viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
                if (viewModel.mode.value == StudyFormMode.EDIT && request != null) {
                    binding.fragmentOnlineStudyTv.text = "스터디 정보 수정"
                    setChipState(request.isOnline)
                    isLocationPlusVisible = !request.isOnline
                    updateLocationPlusLayoutVisibility(isLocationPlusVisible)

                    val newCode = request.regions?.firstOrNull()

                    if (!request.isOnline && newCode != null && newCode != selectedLocationCode) {
                        val address = viewModel.findAddressFromCode(newCode)
                        address?.let {
                            updateChip(it)
                            selectedLocationCode = newCode
                        }
                    } else if (!request.isOnline && newCode == null) {
                        binding.locationChip.visibility = View.GONE
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
            observeViewModel()

            return binding.root
        }

        override fun onResume() {
            super.onResume()
            arguments?.let {
                val address = it.getString("ADDRESS")
                val code = it.getString("CODE")
                Log.d("OnlineStudyFragment", "onResume profileImage = ${viewModel.studyRequest.value?.profileImage}")

                if (!address.isNullOrBlank() && !code.isNullOrBlank()) {
                    updateChip(address)
                    selectedLocationCode = code

                    val current = viewModel.studyRequest.value
                    val isOnlineSelected = binding.fragmentOnlineStudyChipOnline.isChecked.not() // 오프라인 칩이 선택되어야 true
                    val hasFee = viewModel.studyRequest.value?.hasFee

                    if (viewModel.mode.value == StudyFormMode.EDIT && current != null) {
                        val updated = current.copy(
                            isOnline = isOnlineSelected.not(), // 온라인이면 true, 오프라인이면 false
                            regions = listOf(code)
                        )

                        viewModel.setStudyData(
                            title = updated.title,
                            goal = updated.goal,
                            introduction = updated.introduction,
                            isOnline = updated.isOnline,
                            profileImage = updated.profileImage,
                            regions = updated.regions,
                            maxPeople = updated.maxPeople,
                            gender = updated.gender,
                            minAge = updated.minAge,
                            maxAge = updated.maxAge,
                            fee = updated.fee,
                            hasFee = hasFee
                        )
                    }
                }

                it.clear()
            }
        }

        private fun observeViewModel() {
            viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
                if (viewModel.mode.value == StudyFormMode.EDIT && request != null) {
                    binding.fragmentOnlineStudyTv.text = "스터디 정보 수정"
                    setChipState(request.isOnline)
                    isLocationPlusVisible = !request.isOnline
                    updateLocationPlusLayoutVisibility(isLocationPlusVisible)

                    val newCode = request.regions?.firstOrNull()
                    if (!request.isOnline && newCode != null && newCode != selectedLocationCode) {
                        viewModel.findAddressFromCode(newCode)?.let {
                            updateChip(it)
                            selectedLocationCode = newCode
                        }
                    } else if (!request.isOnline && newCode == null) {
                        binding.locationChip.visibility = View.GONE
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

            val current = viewModel.studyRequest.value ?: StudyRequest(
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
                title = current.title,
                goal = current.goal,
                introduction = current.introduction,
                isOnline = isOnline,
                profileImage = current.profileImage,
                regions = if (isOnline) null else current.regions ?: mutableListOf(),
                maxPeople = current.maxPeople,
                gender = current.gender,
                minAge = current.minAge,
                maxAge = current.maxAge,
                fee = current.fee,
                hasFee = current.hasFee
            )

            updateLocationPlusLayoutVisibility(isLocationPlusVisible)
            updateNextButtonState()
        }


        private fun setupChipCloseListener() {
            binding.locationChip.setOnCloseIconClickListener {
                binding.locationChip.visibility = View.GONE
                selectedLocationCode = null

                if (viewModel.mode.value == StudyFormMode.EDIT) {
                    val current = viewModel.studyRequest.value
                    val cleared = current?.copy(regions = null)
                    cleared?.let {
                        viewModel.setStudyData(
                            title = it.title,
                            goal = it.goal,
                            introduction = it.introduction,
                            isOnline = it.isOnline,
                            profileImage = it.profileImage,
                            regions = it.regions,
                            maxPeople = it.maxPeople,
                            gender = it.gender,
                            minAge = it.minAge,
                            maxAge = it.maxAge,
                            fee = it.fee,
                            hasFee = it.hasFee
                        )
                    }
                }

                updateNextButtonState()
            }
        }

        private fun setChipState(isOnline: Boolean) {
            binding.fragmentOnlineStudyChipOnline.isChecked = isOnline
            binding.fragmentOnlineStudyBt.isEnabled = isOnline
            binding.fragmentOnlineStudyChipOffline.isChecked = !isOnline
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
            val current = viewModel.studyRequest.value ?: return
            val regions = selectedLocationCode?.let { listOf(it) } ?: listOf()
            val preservedProfileImage = current.profileImage ?: viewModel.profileImageUri.value

            viewModel.setStudyData(
                title = current.title,
                goal = current.goal,
                introduction = current.introduction,
                isOnline = current.isOnline,
                profileImage = preservedProfileImage,
                regions = if (current.isOnline) null else regions,
                maxPeople = current.maxPeople,
                gender = current.gender,
                minAge = current.minAge,
                maxAge = current.maxAge,
                fee = current.fee,
                hasFee = current.hasFee
            )
        }
        private fun clearChipSelection() {
            binding.fragmentOnlineStudyChipOnline.isChecked = false
            binding.fragmentOnlineStudyChipOffline.isChecked = false
        }

        private fun goToNextFragment() {
            val nextFragment = MemberStudyFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("mode", viewModel.mode.value)
                    viewModel.studyId.value?.let { putInt("studyId", it) }
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, nextFragment)
                .addToBackStack(null)
                .commit()
        }

        private fun goToPreviusFragment() {
            parentFragmentManager.popBackStack()
        }

        private fun goToLocationFragment() {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frm, LocationStudyFragment())
                .commit()
        }
        fun Int.dpToPx(): Int =
            (this * Resources.getSystem().displayMetrics.density).toInt()
    }