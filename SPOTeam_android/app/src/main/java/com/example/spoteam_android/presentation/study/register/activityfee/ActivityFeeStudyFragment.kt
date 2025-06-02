package com.example.spoteam_android.presentation.study.register.activityfee

import StudyFormMode
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentActivityFeeStudyBinding
import com.example.spoteam_android.presentation.study.register.util.CorrectStudyCompleteDialog
import com.example.spoteam_android.presentation.study.register.preview.MyStudyRegisterPreviewFragment
import com.example.spoteam_android.presentation.study.register.StudyRegisterViewModel
import kotlinx.coroutines.launch

class ActivityFeeStudyFragment : Fragment() {

    private lateinit var binding: FragmentActivityFeeStudyBinding
    private val registerViewModel: StudyRegisterViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt("studyId", -1)?.let {
            if (it != -1) {
                registerViewModel.setStudyId(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivityFeeStudyBinding.inflate(inflater, container, false)

        binding.fragmentActivityFeeStudyBackBt.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        observeRegisterViewModel()
        setupChipGroupListener()
        setupFeeEditTextListener()
        setupPreviewButtonListener()

        return binding.root
    }

    private fun observeRegisterViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                registerViewModel.studyRequest.collect { request ->
                    if (request == null) return@collect

                    val hasFee = request.hasFee
                    when (registerViewModel.mode.value) {
                        StudyFormMode.EDIT -> {
                            binding.fragmentActivityFeeStudyTv.text = "스터디 정보 수정"
                            binding.fragmentActivityFeeStudyPreviewBt.text = "수정완료"
                            setChipState(hasFee)

                            if (hasFee == true) {
                                binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                                binding.fragmentActivityFeeStudyEt.setText(request.fee.toString())
                            } else {
                                binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                            }

                            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
                        }

                        StudyFormMode.CREATE -> {
                            setChipState(hasFee)
                            if (hasFee == true) {
                                binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                            } else {
                                binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                            }
                            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = (hasFee == false)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupChipGroupListener() {
        binding.fragmentActivityFeeStudyChipTrue.setOnClickListener {
            selectChip(it.id)
        }
        binding.fragmentActivityFeeStudyChipFalse.setOnClickListener {
            selectChip(it.id)
        }
    }

    private fun selectChip(selectedChipId: Int) {
        binding.fragmentActivityFeeStudyChipTrue.isChecked = false
        binding.fragmentActivityFeeStudyChipFalse.isChecked = false

        when (selectedChipId) {
            R.id.fragment_activity_fee_study_chip_true -> {
                binding.fragmentActivityFeeStudyChipTrue.isChecked = true
                binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                checkFeeInput()
            }

            R.id.fragment_activity_fee_study_chip_false -> {
                binding.fragmentActivityFeeStudyChipFalse.isChecked = true
                binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true

                val current = registerViewModel.studyRequest.value ?: return
                registerViewModel.setStudyRequest(
                    current.copy(hasFee = false, fee = 0)
                )
            }
        }
    }

    private fun setChipState(hasFee: Boolean?) {
        binding.fragmentActivityFeeStudyChipTrue.isChecked = (hasFee == true)
        binding.fragmentActivityFeeStudyChipFalse.isChecked = (hasFee == false)
    }

    private fun setupFeeEditTextListener() {
        binding.fragmentActivityFeeStudyEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateFee()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkFeeInput() {
        val feeText = binding.fragmentActivityFeeStudyEt.text.toString()
        // 숫자로 변환 후 > 0인지 확인
        binding.fragmentActivityFeeStudyPreviewBt.isEnabled =
            feeText.isNotEmpty() && (feeText.toIntOrNull() ?: 0) > 0
    }

    private fun updateFee() {
        val feeText = binding.fragmentActivityFeeStudyEt.text.toString()
        val fee = feeText.toIntOrNull() ?: 0

        when {
            feeText.isEmpty() -> {
                binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
                return
            }

            fee > 100_000 -> {
                binding.fragmentActivityFeeStudyEt.error = "최대 100,000원까지 입력 가능합니다."
                binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
                return
            }

            fee < 1_000 -> {
                binding.fragmentActivityFeeStudyEt.error = "최소 1,000원부터 입력 가능합니다."
                binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
                return
            }

            else -> binding.fragmentActivityFeeStudyEt.error = null
        }

        binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true

        val current = registerViewModel.studyRequest.value ?: return
        if (current.fee != fee || current.hasFee != true) {
            registerViewModel.setStudyRequest(
                current.copy(hasFee = true, fee = fee)
            )
        }
    }

    private fun setupPreviewButtonListener() {
        binding.fragmentActivityFeeStudyPreviewBt.setOnClickListener {
            when (registerViewModel.mode.value) {
                StudyFormMode.EDIT -> {
                    registerViewModel.submitStudyData(
                        onSuccess = {
                            val dialog = CorrectStudyCompleteDialog(requireContext())
                            dialog.start(parentFragmentManager)
                        },
                        onFailure = {
                            Log.e("ActivityFeeStudyFragment", "스터디 수정 실패")
                        }
                    )
                }

                StudyFormMode.CREATE -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, MyStudyRegisterPreviewFragment())
                        .addToBackStack(null)
                        .commit()
                }

                else -> Unit
            }
        }

    }

}
