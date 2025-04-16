package com.example.spoteam_android.ui.myinterest

import com.example.spoteam_android.ui.interestarea.BottomNavVisibilityController
import com.example.spoteam_android.ui.interestarea.InterestFilterViewModel
import com.example.spoteam_android.ui.interestarea.InterestFragment
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
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
        viewModel.reset()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("BottomNav", "InterestFilterFragment에서 hideBottomNav() 호출")
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

        binding.lvAddArea.setOnClickListener{
            (activity as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, MyInterestStudyFilterLocationFragment())
                .addToBackStack(null)
                .commit()
        }

        arguments?.let {
            // 전달된 주소와 코드가 있을 경우 ViewModel에 저장
            viewModel.selectedAddress = it.getString("ADDRESS")
            viewModel.selectedCode = it.getString("CODE")

            val address = viewModel.selectedAddress
            val isOffline = it.getBoolean("IS_OFFLINE", false)

            address?.let { addr ->
                updateChip(addr)
            }

            setChipState(isOffline)
            setupChipCloseListener()
            isLocationPlusVisible = isOffline
        }

    }

    override fun onDestroyView() {
        Log.d("BottomNav", "MyInterestFilterFragment에서 showBottomNav() 호출 - onDestroyView")
        navVisibilityController?.showBottomNav()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        binding.toolbar.icBack.setOnClickListener {
            viewModel.reset() // 1. ViewModel 값 초기화
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
            binding.chipGroupNew.clearCheck()
            binding.locationChip.visibility = View.GONE
            binding.lvAddArea.visibility = View.GONE
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
                    binding.locationChip.visibility = View.GONE
                    binding.lvAddArea.visibility = View.GONE
                }
                R.id.chip02 -> {
                    viewModel.isOnline = false
                    binding.lvAddArea.visibility = View.VISIBLE

                    // 선택된 주소가 있다면 chip도 보여줌
                    if (!viewModel.selectedAddress.isNullOrEmpty()) {
                        binding.locationChip.visibility = View.VISIBLE
                    }
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
    private fun setupChipCloseListener() {
        binding.locationChip.setOnCloseIconClickListener {
            binding.locationChip.visibility = View.GONE
            binding.lvAddArea.visibility = View.VISIBLE
        }
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