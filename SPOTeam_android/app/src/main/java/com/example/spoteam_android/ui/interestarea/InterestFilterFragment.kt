package com.example.spoteam_android.ui.interestarea

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.NumberFormat
import android.os.Bundle
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
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class InterestFilterFragment : Fragment() {

    lateinit var binding: FragmentInterestFilterBinding
    private val viewModel: InterestFilterViewModel by activityViewModels()

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
        binding = FragmentInterestFilterBinding.inflate(inflater, container, false)

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
        setupStudyThemeChips()
        setupSearchButton()

        restoreViewFromViewModel()

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
            val interestFragment = InterestFragment().apply {
                arguments = bundle
            }
            (activity as MainActivity).switchFragment(interestFragment) // 2. 초기화된 인스턴스로 이동
        }
    }

    private fun setupGenderChips() {
        binding.chipGroupGender.setOnCheckedChangeListener { _, checkedId ->
            viewModel.gender = when (checkedId) {
                R.id.chip1_gender -> "UNKNOWN"
                R.id.chip2_gender -> "MALE"
                R.id.chip3_gender -> "FEMALE"
                ChipGroup.NO_ID -> null // ✅ 선택 안 됐을 때
                else -> null
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
        ageRangeSlider.values = listOf(viewModel.minAge.toFloat(), viewModel.maxAge.toFloat())

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

        activityfeeSlider.valueFrom = 1000f
        activityfeeSlider.valueTo = 500000f
        activityfeeSlider.stepSize = 100f
        val minFee = (viewModel.finalMinFee ?: 1000).toFloat()
        val maxFee = (viewModel.finalMaxFee ?: 500000).toFloat()

        activityfeeSlider.values = listOf(minFee, maxFee)

        val customThumb = ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb)
        if (customThumb != null) {
            activityfeeSlider.setCustomThumbDrawable(customThumb)
        }

        activityfeeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            val minfee = values[0].toInt()
            val maxfee = values[1].toInt()

            // ViewModel에도 저장
            viewModel.minfee = minfee
            viewModel.maxfee = maxfee

            val formattedMinFee = NumberFormat.getNumberInstance().format(minfee)
            val formattedMaxFee = NumberFormat.getNumberInstance().format(maxfee)
            binding.activityfeeMinValueText.text = "₩$formattedMinFee"
            binding.activityfeeMaxValueText.text = "₩$formattedMaxFee"
        }
    }

    private fun setupActivityFeeChips() {
        val chipGroup1 = binding.chipGroup1

        chipGroup1.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip1 -> {
                    viewModel.hasFee = true
                    binding.activityfeeSlider.isVisible = true
                    binding.displayfeeFrameLayout.isVisible = true
                }
                R.id.chip2 -> {
                    viewModel.hasFee = false
                    binding.activityfeeSlider.isVisible = false
                    binding.displayfeeFrameLayout.isVisible = false
                }
                ChipGroup.NO_ID -> {
                    viewModel.hasFee = null
                    binding.activityfeeSlider.isVisible = false
                    binding.displayfeeFrameLayout.isVisible = false
                }
            }
            updateNextButtonState()
        }
    }


    private fun setupStudyThemeChips() {
        for (i in 0 until binding.chipGroup2.childCount) {
            val chip = binding.chipGroup2.getChildAt(i) as? Chip ?: continue

            chip.setOnClickListener {
                val originalText = chip.text.toString()
                val theme = when (originalText) {
                    "시사/뉴스" -> "시사뉴스"
                    "전공/진로학습" -> "전공및진로학습"
                    else -> originalText
                }

                // themeTypes가 null이면 빈 리스트로 초기화
                viewModel.themeTypes = (viewModel.themeTypes ?: mutableListOf()).apply {
                    if (chip.isChecked) {
                        if (!contains(theme)) add(theme)
                    } else {
                        remove(theme)
                    }
                }
                updateNextButtonState()
            }
        }
    }


    private fun setupSearchButton() {
        //필터 초기화 클릭
        binding.txResetFilter.setOnClickListener {
            viewModel.reset()
            binding.chipGroupRecruiting.clearCheck()
            binding.chipGroupGender.clearCheck()
            binding.chipGroup1.clearCheck()
            binding.chipGroup2.clearCheck()

            binding.ageRangeSlider.values = listOf(18f, 60f)
            binding.activityfeeSlider.values = listOf(1000f, 5000000f)

            // ✅ 텍스트뷰 값도 초기화 (선택사항)
            binding.minValueText.text = "18"
            binding.maxValueText.text = "60"
            binding.activityfeeMinValueText.text = "₩ 1,000"
            binding.activityfeeMaxValueText.text = "₩ 500,000"
        }

        binding.fragmentIntroduceStudyBt.setOnClickListener {
            // ViewModel 값 → Bundle
            val bundle = Bundle().apply {
                putString("source", "InterestFilterFragment")
            }

            val interestFragment = InterestFragment().apply {
                arguments = bundle
            }

            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, interestFragment)
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
                else -> null
            }
        }
        updateNextButtonState()
    }

    private fun restoreViewFromViewModel() {
        // 모집 상태
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

        // 연령 슬라이더
        binding.ageRangeSlider.values = listOf(
            viewModel.minAge.toFloat() ?: 18f,
            viewModel.maxAge.toFloat() ?: 60f
        )
        binding.minValueText.text = viewModel.minAge.toString()
        binding.maxValueText.text = viewModel.maxAge.toString()

        val minFee = viewModel.finalMinFee?.toFloat() ?: 1000f
        val maxFee = viewModel.finalMaxFee?.toFloat() ?: 500000f


        val formattedMin = NumberFormat.getNumberInstance().format(minFee.toInt())
        val formattedMax = NumberFormat.getNumberInstance().format(maxFee.toInt())

        binding.activityfeeMinValueText.text = "₩$formattedMin"
        binding.activityfeeMaxValueText.text = "₩$formattedMax"

// hasFee 여부에 따라 슬라이더 영역 표시 여부 결정
        binding.activityfeeSlider.isVisible = viewModel.hasFee == true
        binding.displayfeeFrameLayout.isVisible = viewModel.hasFee == true

        // 테마 (ChipGroup2)
        val selectedThemes = viewModel.themeTypes ?: emptyList()
        for (i in 0 until binding.chipGroup2.childCount) {
            val chip = binding.chipGroup2.getChildAt(i) as? Chip ?: continue
            val chipText = chip.text.toString()
            val theme = when (chipText) {
                "시사/뉴스" -> "시사뉴스"
                "전공/진로학습" -> "전공및진로학습"
                else -> chipText
            }
            chip.isChecked = selectedThemes.contains(theme)
        }
    }

}

