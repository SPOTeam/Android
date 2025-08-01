package com.umcspot.android.ui.category

import com.umcspot.android.ui.interestarea.BottomNavVisibilityController
import android.content.Context
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.umcspot.android.R
import com.umcspot.android.databinding.FragmentCategoryFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class CategoryInterestFilterFragment : Fragment() {

    private var initalsource : Boolean = false
    private var initialGender: String? = null
    private var initialMinAge: Int = 18
    private var initialMaxAge: Int = 60
    private var initialHasFee: Boolean? = null
    private var initialMinFee: Int? = null
    private var initialMaxFee: Int? = null
    private var initialIsRecruiting: String? = null

    lateinit var binding: FragmentCategoryFilterBinding
    private val viewModel: CategoryInterestViewModel by activityViewModels()

    private var navVisibilityController: BottomNavVisibilityController? = null


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
        binding = FragmentCategoryFilterBinding.inflate(inflater, container, false)
//        viewModel.reset()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navVisibilityController?.hideBottomNav()

        applyReceivedFilterArguments()  // ✨추가

        viewModel.source = true

        setupToolbar()
        setupRecruitingChips()
        setupGenderChips()
        setupAgeRangeSlider()
        setupActivityFeeSlider()
        setupActivityFeeChips()
        setupSearchButton()
        setupResetButton()

    }

    private fun applyReceivedFilterArguments() {
        // ViewModel -> UI 초기화
        viewModel.gender?.let { gender ->
            when (gender) {
                "UNKNOWN" -> binding.chip1Gender.isChecked = true
                "MALE" -> binding.chip2Gender.isChecked = true
                "FEMALE" -> binding.chip3Gender.isChecked = true
            }
        }

        viewModel.isRecruiting?.let { status ->
            when (status) {
                "RECRUITING" -> binding.chip1Recruiting.isChecked = true
                "COMPLETED" -> binding.chip2Recruiting.isChecked = true
            }
        }

        binding.ageRangeSlider.values = listOf(viewModel.minAge.toFloat(), viewModel.maxAge.toFloat())
        binding.minValueText.text = viewModel.minAge.toString()
        binding.maxValueText.text = viewModel.maxAge.toString()

        viewModel.hasFee?.let { hasFee ->
            if (hasFee) {
                binding.chip1.isChecked = true
                binding.activityfeeSlider.isVisible = true
                binding.displayfeeFrameLayout.isVisible = true
                binding.activityfeeSlider.values = listOf(
                    viewModel.minfee?.toFloat() ?: 1000f,
                    viewModel.maxfee?.toFloat() ?: 10000f
                )
                binding.activityfeeMinValueText.text = "₩${NumberFormat.getNumberInstance().format(viewModel.minfee ?: 1000)}"
                binding.activityfeeMaxValueText.text = "₩${NumberFormat.getNumberInstance().format(viewModel.maxfee ?: 10000)}"
            } else {
                binding.chip2.isChecked = true
                binding.activityfeeSlider.isVisible = false
                binding.displayfeeFrameLayout.isVisible = false
            }
        }

        // ✨✨ 여기서 초기값 복사해놓기 ✨✨
        initalsource = viewModel.source
        initialGender = viewModel.gender
        initialMinAge = viewModel.minAge
        initialMaxAge = viewModel.maxAge
        initialHasFee = viewModel.hasFee
        initialMinFee = viewModel.minfee
        initialMaxFee = viewModel.maxfee
        initialIsRecruiting = viewModel.isRecruiting
    }

    override fun onDestroyView() {
        navVisibilityController?.showBottomNav()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        binding.toolbar.icBack.setOnClickListener {
            // ViewModel 초기값으로 복구 ✨
            viewModel.source = initalsource
            viewModel.gender = initialGender
            viewModel.minAge = initialMinAge
            viewModel.maxAge = initialMaxAge
            viewModel.hasFee = initialHasFee
            viewModel.minfee = initialMinFee
            viewModel.maxfee = initialMaxFee
            viewModel.isRecruiting = initialIsRecruiting

            parentFragmentManager.popBackStack()
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

        // 🔽 valueFrom/to 설정 후 값 적용해야 함!
        val minAge = viewModel.minAge.coerceIn(18, 60)
        val maxAge = viewModel.maxAge.coerceIn(18, 60)

        ageRangeSlider.values = listOf(minAge.toFloat(), maxAge.toFloat())

        val customThumb = ContextCompat.getDrawable(requireContext(),R.drawable.custom_thumb)
        if (customThumb != null) {
            ageRangeSlider.setCustomThumbDrawable(customThumb)
        }

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val updatedMinAge = values[0].toInt()
            val updatedMaxAge = values[1].toInt()
            viewModel.minAge = updatedMinAge
            viewModel.maxAge = updatedMaxAge
            minValueText.text = updatedMinAge.toString()
            maxValueText.text = updatedMaxAge.toString()
        }
    }

    private fun setupActivityFeeSlider() {
        val activityfeeSlider = binding.activityfeeSlider
        val minValueText = binding.activityfeeMinValueText
        val maxValueText = binding.activityfeeMaxValueText

        activityfeeSlider.valueFrom = 1000f
        activityfeeSlider.valueTo = 10000f
        activityfeeSlider.stepSize = 100f
        activityfeeSlider.values = listOf(1000f, 10000f)
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

            // ✅ RangeSlider 초기화
            binding.ageRangeSlider.values = listOf(18f, 60f)
            binding.activityfeeSlider.values = listOf(1000f, 10000f)

            // ✅ 텍스트뷰 값도 초기화 (선택사항)
            binding.minValueText.text = "18"
            binding.maxValueText.text = "60"
            binding.activityfeeMinValueText.text = "₩ 1,000"
            binding.activityfeeMaxValueText.text = "₩ 10,000"
        }

    }


    private fun setupSearchButton() {
        binding.fragmentIntroduceStudyBt.setOnClickListener {

            val sliderValues = binding.activityfeeSlider.values
            viewModel.minfee = sliderValues[0].toInt()
            viewModel.maxfee = sliderValues[1].toInt()

            // ✨ 현재 화면에서 선택한 chip, slider 값들을 ViewModel에 저장
            viewModel.gender = when (binding.chipGroupGender.checkedChipId) {
                R.id.chip1_gender -> "UNKNOWN"
                R.id.chip2_gender -> "MALE"
                R.id.chip3_gender -> "FEMALE"
                else -> null
            }

            viewModel.minAge = binding.ageRangeSlider.values[0].toInt()
            viewModel.maxAge = binding.ageRangeSlider.values[1].toInt()

            viewModel.hasFee = when (binding.chipGroup1.checkedChipId) {
                R.id.chip1 -> true
                R.id.chip2 -> false
                else -> null
            }

            viewModel.isRecruiting = when (binding.chipGroupRecruiting.checkedChipId) {
                R.id.chip1_recruiting -> "RECRUITING"
                R.id.chip2_recruiting -> "COMPLETED"
                else -> null
            }

            parentFragmentManager.popBackStack()
        }
    }


    private fun updateNextButtonState() {
        binding.fragmentIntroduceStudyBt.isEnabled = true
    }

    private fun setupRecruitingChips() {
        binding.chipGroupRecruiting.setOnCheckedChangeListener { group, checkedId ->
            viewModel.isRecruiting = when (checkedId) {
                R.id.chip1_recruiting -> "RECRUITING"  // 모집중
                R.id.chip2_recruiting -> "COMPLETED"   // 모집완료
                else -> null                           // 선택 안함
            }
        }
        updateNextButtonState()
    }
}