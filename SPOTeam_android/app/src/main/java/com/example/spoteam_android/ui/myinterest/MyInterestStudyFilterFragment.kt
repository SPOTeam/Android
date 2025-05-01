package com.example.spoteam_android.ui.myinterest

import com.example.spoteam_android.ui.interestarea.BottomNavVisibilityController
import com.example.spoteam_android.ui.interestarea.InterestFilterViewModel
import com.example.spoteam_android.ui.interestarea.InterestFragment
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentInterestFilterBinding
import com.example.spoteam_android.databinding.FragmentMyInterestStudyFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup


class MyInterestStudyFilterFragment : Fragment() {

    lateinit var binding: FragmentMyInterestStudyFilterBinding
    private val viewModel: MyInterestChipViewModel by activityViewModels()

    private var navVisibilityController: BottomNavVisibilityController? = null
    private var isLocationPlusVisible: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavVisibilityController) {
            navVisibilityController = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInterestStudyFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navVisibilityController?.hideBottomNav()

        setupToolbar()
        setupRecruitingChips()
        setupGenderChips()
        setupAgeRangeSlider()
        setupActivityFeeSlider()
        setupActivityFeeChips()
        setupSearchButton()
        setupOnlineOfflineChips()
        setupResetButton()

        restoreViewFromViewModel()

        binding.lvAddArea.setOnClickListener{
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, MyInterestStudyFilterLocationFragment())
                .addToBackStack(null)
                .commit()
        }


        arguments?.let {
            // arguments가 있는 경우에만, ViewModel에 값이 없을 때만 설정
            if (viewModel.selectedAddress!!.isEmpty() && viewModel.selectedCode!!.isEmpty()) {
                val addressList = it.getStringArrayList("ADDRESS_LIST")?.toMutableList() ?: mutableListOf()
                val codeList = it.getStringArrayList("CODE_LIST")?.toMutableList() ?: mutableListOf()

                viewModel.selectedAddress = addressList
                viewModel.selectedCode = codeList

                // 주소 리스트를 Chip으로 업데이트
                if (addressList.isNotEmpty()) {
                    updateChip(addressList)
                }

                val isOffline = it.getBoolean("IS_OFFLINE", false)
                setChipState(isOffline)
                isLocationPlusVisible = isOffline
            }
        }


    }

    override fun onDestroyView() {
        navVisibilityController?.showBottomNav()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        binding.toolbar.icBack.setOnClickListener {
            val bundle = Bundle().apply {
                putString("source", "HouseFragment")
            }
            val myInterestStudyFragment = MyInterestStudyFragment().apply {
                arguments = bundle
            }
            (activity as MainActivity).switchFragment(myInterestStudyFragment) // 2. 초기화된 인스턴스로 이동
        }
    }

    private fun setupGenderChips() {
        binding.chipGroupGender.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender = when (checkedId) {
                R.id.chip1_gender -> "UNKNOWN"
                R.id.chip2_gender -> "MALE"
                R.id.chip3_gender -> "FEMALE"
                else -> "MALE"
            }
            updateNextButtonState()
        }
    }

    private fun setupAgeRangeSlider() {
        val ageRangeSlider = binding.ageRangeSlider
        val minValueText = binding.minValueText
        val maxValueText = binding.maxValueText

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 60f)

        val customThumb = ContextCompat.getDrawable(requireContext(),R.drawable.custom_thumb)
        if (customThumb != null) {
            ageRangeSlider.setCustomThumbDrawable(customThumb)
        }

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val minAge = values[0].toInt()
            val maxAge = values[1].toInt()
            viewModel.minAge = minAge
            viewModel.maxAge = maxAge
            minValueText.text = minAge.toString()
            maxValueText.text = maxAge.toString()
        }
    }

    private fun setupActivityFeeSlider() {
        val activityfeeSlider = binding.activityfeeSlider
        val minValueText = binding.activityfeeMinValueText
        val maxValueText = binding.activityfeeMaxValueText

        activityfeeSlider.valueFrom = 1000f
        activityfeeSlider.valueTo = 500000f
        activityfeeSlider.stepSize = 100f
        activityfeeSlider.values = listOf(1000f, 500000f)
        val customThumb = ContextCompat.getDrawable(requireContext(),R.drawable.custom_thumb)
        if (customThumb != null) {
            activityfeeSlider.setCustomThumbDrawable(customThumb)
        }

        activityfeeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val minfee = values[0].toInt()
            val maxfee = values[1].toInt()
            val formattedMinFee = NumberFormat.getNumberInstance().format(minfee)
            val formattedMaxFee = NumberFormat.getNumberInstance().format(maxfee)
            minValueText.text = "₩$formattedMinFee"
            maxValueText.text = "₩$formattedMaxFee"
        }
    }

    private fun setupActivityFeeChips() {
        val chipGroup1 = binding.chipGroup1
        chipGroup1.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != ChipGroup.NO_ID) {
                val checkedChip = chipGroup1.findViewById<Chip>(checkedId)
                if (checkedChip.id == R.id.chip1) {
                    viewModel.hasFee = true
                    binding.activityfeeSlider.isVisible = true
                    binding.displayfeeFrameLayout.isVisible = true
                } else {
                    viewModel.hasFee = false
                    binding.activityfeeSlider.isVisible = false
                    binding.displayfeeFrameLayout.isVisible = false
                }
            } else {
                viewModel.hasFee = false
                binding.activityfeeSlider.isVisible = false
                binding.displayfeeFrameLayout.isVisible = false
            }
            updateNextButtonState()
        }
    }

    private fun setupResetButton(){
        //필터 초기화 클릭
        binding.txResetFilter.setOnClickListener {
            viewModel.reset()

            binding.chipGroupRecruiting.clearCheck()
            binding.chipGroupGender.clearCheck()
            binding.chipGroup1.clearCheck()
            binding.chipGroupNew.clearCheck()


            // ✅ RangeSlider 초기화
            binding.ageRangeSlider.values = listOf(18f, 60f)
            binding.activityfeeSlider.values = listOf(1000f, 500000f)

            // ✅ 텍스트뷰 값도 초기화 (선택사항)
            binding.minValueText.text = "18"
            binding.maxValueText.text = "60"
            binding.activityfeeMinValueText.text = "₩ 1,000"
            binding.activityfeeMaxValueText.text = "₩ 500,000"

            binding.lvAddArea.visibility = View.GONE
            binding.locationChipGroup.visibility = View.GONE
        }

    }


    private fun setupSearchButton() {
        binding.fragmentIntroduceStudyBt.setOnClickListener {

            val sliderValues = binding.activityfeeSlider.values
            viewModel.minfee = sliderValues[0].toInt()
            viewModel.maxfee = sliderValues[1].toInt()

            // ViewModel 값 → Bundle
            val bundle = Bundle().apply {
                putString("source", "MyInterestStudyFilterFragment")
            }

            val myInterestStudyFragment = MyInterestStudyFragment().apply {
                arguments = bundle
            }

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, myInterestStudyFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateNextButtonState() {
        binding.fragmentIntroduceStudyBt.isEnabled = true
    }


    private fun setupRecruitingChips() {
        binding.chipGroupRecruiting.setOnCheckedChangeListener { group, checkedId ->
            viewModel.isRecruiting = when (checkedId) {
                R.id.chip1_recruiting -> true
                R.id.chip2_recruiting -> false
                else -> true
            }
        }
        updateNextButtonState()
    }


    private fun setupOnlineOfflineChips() {
        binding.chipGroupNew.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip01 -> {
                    viewModel.isOnline = true
                    binding.lvAddArea.visibility = View.GONE
                    val chipGroup = binding.locationChipGroup
                    chipGroup.removeAllViews()
                    viewModel.selectedCode?.clear()
                    viewModel.selectedAddress?.clear()
                }
                R.id.chip02 -> {
                    viewModel.isOnline = false
                    binding.lvAddArea.visibility = View.VISIBLE
                }
            }
            updateNextButtonState()
        }
    }


    private fun setChipState(isOffline: Boolean) {
        if (isOffline) {
            binding.chipGroupNew.check(R.id.chip02)
        }
    }
    fun updateChip(addressList: MutableList<String>) {
        val chipGroup = binding.locationChipGroup
        chipGroup.removeAllViews()

        for (address in addressList) {
            val truncatedAddress = extractAddressUntilDong(address)

            val chip = Chip(requireContext()).apply {
                val chipDrawable = ChipDrawable.createFromAttributes(
                    requireContext(),
                    null,
                    0,
                    R.style.CustomChipCloseStyle2
                )
                setChipDrawable(chipDrawable)

                text = truncatedAddress
                textSize = 14f  // ✅ 텍스트 사이즈 통일
                setTextColor(ContextCompat.getColor(requireContext(), R.color.search_chip_text))
                isCloseIconVisible = true

                val widthInPx = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    260f,
                    resources.displayMetrics
                ).toInt()

                layoutParams = ViewGroup.MarginLayoutParams(
                    widthInPx,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 15
                    marginEnd = 15
                    topMargin = 25
                }

                setOnCloseIconClickListener {
                    chipGroup.removeView(this)
                    viewModel.removeAddress(address)
                    viewModel.selectedAddress?.let { it1 -> updateChip(it1) }

                    if (chipGroup.childCount == 0) {
                        binding.lvAddArea.visibility = View.VISIBLE
                    }

                    updateNextButtonState()
                }
            }

            chipGroup.addView(chip)
        }


        chipGroup.visibility = View.VISIBLE
        updateNextButtonState()

    }




    private fun extractAddressUntilDong(address: String): String {
        val regex = Regex("(\\S+(동|읍|면))")
        val match = regex.findAll(address).lastOrNull() // 가장 마지막 동/읍/면 추출

        return if (match != null) {
            val endIndex = match.range.last + 1
            address.substring(0, endIndex)
        } else {
            address
        }
    }

    private fun restoreViewFromViewModel() {
        // 모집 여부
        when (viewModel.isRecruiting) {
            true -> binding.chipGroupRecruiting.check(R.id.chip1_recruiting)
            false -> binding.chipGroupRecruiting.check(R.id.chip2_recruiting)
            null -> binding.chipGroupRecruiting.clearCheck()
        }

        // 성별
        when (viewModel.gender) {
            "UNKNOWN" -> binding.chipGroupGender.check(R.id.chip1_gender)
            "MALE" -> binding.chipGroupGender.check(R.id.chip2_gender)
            "FEMALE" -> binding.chipGroupGender.check(R.id.chip3_gender)
            null -> binding.chipGroupGender.clearCheck()
        }

        // 참가비 여부
        when (viewModel.hasFee) {
            true -> {
                binding.chipGroup1.check(R.id.chip1)
                binding.activityfeeSlider.isVisible = true
                binding.displayfeeFrameLayout.isVisible = true
            }
            false -> {
                binding.chipGroup1.check(R.id.chip2)
                binding.activityfeeSlider.isVisible = false
                binding.displayfeeFrameLayout.isVisible = false
            }
            null -> {
                binding.chipGroup1.clearCheck()
                binding.activityfeeSlider.isVisible = false
                binding.displayfeeFrameLayout.isVisible = false
            }
        }

        // 참가비 슬라이더
        val minFee = viewModel.finalMinFee?.toFloat() ?: 1000f
        val maxFee = viewModel.finalMaxFee?.toFloat() ?: 500000f
        binding.activityfeeSlider.values = listOf(minFee, maxFee)

        val formattedMin = NumberFormat.getNumberInstance().format(minFee.toInt())
        val formattedMax = NumberFormat.getNumberInstance().format(maxFee.toInt())
        binding.activityfeeMinValueText.text = "₩$formattedMin"
        binding.activityfeeMaxValueText.text = "₩$formattedMax"

        // 연령 슬라이더
        binding.ageRangeSlider.values = listOf(
            viewModel.minAge.toFloat(),
            viewModel.maxAge.toFloat()
        )
        binding.minValueText.text = viewModel.minAge.toString()
        binding.maxValueText.text = viewModel.maxAge.toString()

        // 온라인/오프라인 상태
        if (viewModel.isOnline == true) {
            binding.chipGroupNew.check(R.id.chip01)
            binding.lvAddArea.visibility = View.GONE
        } else if (viewModel.isOnline == false){
            binding.chipGroupNew.check(R.id.chip02)
            binding.lvAddArea.visibility = View.VISIBLE
        }

        // 지역 Chip 복원
        viewModel.selectedAddress?.let { addressList ->
            if (addressList.isNotEmpty()) {
                updateChip(addressList)
            }
        }
    }

}