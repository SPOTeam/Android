import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.spot.android.R
import com.spot.android.databinding.FragmentActivityFeeStudyBinding
import com.spot.android.ui.study.CorrectStudyCompleteDialog
import com.spot.android.ui.study.MyStudyRegisterPreviewFragment
class ActivityFeeStudyFragment : Fragment() {
    private lateinit var binding: FragmentActivityFeeStudyBinding
    private val viewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityFeeStudyBinding.inflate(inflater, container, false)

        setupChipGroupListener()
        setupFeeEditTextListener()
        setupPreviewButtonListener()
        binding.fragmentActivityFeeStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }
        binding.fragmentActivityFeeStudyEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFeeInput()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
            if (request == null) return@observe

            val hasFee = request.hasFee

            when (viewModel.mode.value) {
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
                }

                else -> Unit
            }
        }

        viewModel.patchSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                showCompletionDialog()
            } else {
                Toast.makeText(requireContext(), "수정에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }


    private fun setupChipGroupListener() {
        binding.fragmentActivityFeeStudyChipTrue.setOnClickListener {
            selectChip(it.id) // 선택된 Chip ID 전달
        }

        binding.fragmentActivityFeeStudyChipFalse.setOnClickListener {
            selectChip(it.id) // 선택된 Chip ID 전달
        }
    }

    private fun selectChip(selectedChipId: Int) {
        // 모든 칩의 상태 초기화 (선택되지 않은 배경)
        binding.fragmentActivityFeeStudyChipTrue.isChecked = false
        binding.fragmentActivityFeeStudyChipFalse.isChecked = false

        // 선택된 칩만 활성화
        when (selectedChipId) {
            R.id.fragment_activity_fee_study_chip_true -> {
                binding.fragmentActivityFeeStudyChipTrue.isChecked = true
                binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                checkFeeInput() // 입력값 검사
            }
            R.id.fragment_activity_fee_study_chip_false -> {
                binding.fragmentActivityFeeStudyChipFalse.isChecked = true
                binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
            }
        }
    }

    private fun setChipState(hasFee: Boolean?) {
        binding.fragmentActivityFeeStudyChipTrue.isChecked = hasFee == true
        binding.fragmentActivityFeeStudyChipFalse.isChecked = hasFee == false
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

        binding.fragmentActivityFeeStudyPreviewBt.isEnabled = feeText.isNotEmpty() && feeText.toIntOrNull() ?: 0 > 0
    }

    private fun updateFee() {
        val feeText = binding.fragmentActivityFeeStudyEt.text.toString()
        val fee = feeText.toIntOrNull() ?: 0

        if (fee > 100000) {
            binding.fragmentActivityFeeStudyEt.error = "최대 100,000원까지 입력 가능합니다."
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
            return
        }
        if (fee < 1000) {
            binding.fragmentActivityFeeStudyEt.error = "최소 1,000원부터 입력 가능합니다."
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
            return
        }

        binding.fragmentActivityFeeStudyEt.error = null
        binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true

        val currentFee = viewModel.studyRequest.value?.fee ?: -1
        if (fee != currentFee) {
            saveStudyData(fee)
        }
    }


    private fun setupPreviewButtonListener() {
        binding.fragmentActivityFeeStudyPreviewBt.setOnClickListener {
            if (viewModel.mode.value == StudyFormMode.EDIT) {
                viewModel.patchStudyData()
            } else {
                goToNextFragment()
            }
        }
    }


    private fun saveStudyData(fee: Int) {
        val hasFee = when {
            binding.fragmentActivityFeeStudyChipTrue.isChecked -> true
            binding.fragmentActivityFeeStudyChipFalse.isChecked -> false
            else -> null // 사용자가 아무 칩도 선택하지 않은 경우
        }

        viewModel.setStudyData(
            title = viewModel.studyRequest.value?.title.orEmpty(),
            goal = viewModel.studyRequest.value?.goal.orEmpty(),
            introduction = viewModel.studyRequest.value?.introduction.orEmpty(),
            isOnline = viewModel.studyRequest.value?.isOnline ?: true,
            profileImage = viewModel.studyRequest.value?.profileImage,
            regions = viewModel.studyRequest.value?.regions ?: emptyList(),
            maxPeople = viewModel.studyRequest.value?.maxPeople ?: 0,
            gender = viewModel.studyRequest.value?.gender ?: Gender.UNKNOWN,
            minAge = viewModel.studyRequest.value?.minAge ?: 0,
            maxAge = viewModel.studyRequest.value?.maxAge ?: 0,
            fee = fee,
            hasFee = hasFee
        )
    }


    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyStudyRegisterPreviewFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun goToPreviusFragment() {
        parentFragmentManager.popBackStack()
    }

    private fun showCompletionDialog() {
        val dialog = CorrectStudyCompleteDialog(requireContext())
        dialog.start(parentFragmentManager)
    }



}
