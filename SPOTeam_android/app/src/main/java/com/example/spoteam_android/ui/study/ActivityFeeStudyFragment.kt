import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentActivityFeeStudyBinding
import com.example.spoteam_android.ui.mypage.PurposeUploadComplteDialog
import com.example.spoteam_android.ui.study.CorrectStudyCompleteDialog
import com.example.spoteam_android.ui.study.MyStudyRegisterPreviewFragment
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
            if (viewModel.mode.value == StudyFormMode.EDIT && request != null) {
                binding.fragmentActivityFeeStudyTv.text = "스터디 정보 수정"
                binding.fragmentActivityFeeStudyPreviewBt.text = "수정완료"
                setChipState(request.hasFee)

                if (request.hasFee) {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                    binding.fragmentActivityFeeStudyEt.setText(request.fee.toString())
                    binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
                } else {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                    binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
                }
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

    private fun setChipState(hasFee: Boolean) {
        binding.fragmentActivityFeeStudyChipTrue.isChecked = hasFee
        binding.fragmentActivityFeeStudyChipFalse.isChecked = !hasFee
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

        // 입력 값이 없거나 0보다 작으면 버튼을 비활성화
        binding.fragmentActivityFeeStudyPreviewBt.isEnabled = feeText.isNotEmpty() && feeText.toIntOrNull() ?: 0 > 0
    }

    private fun updateFee() {
        val feeText = binding.fragmentActivityFeeStudyEt.text.toString()
        val fee = feeText.toIntOrNull() ?: 0

        if (fee > 10000) {
            binding.fragmentActivityFeeStudyEt.error = "최대 10,000원까지 입력 가능합니다."
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
            return
        }

        binding.fragmentActivityFeeStudyEt.error = null
        binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true

        // ✅ 값이 바뀐 경우에만 ViewModel에 저장
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
        // ViewModel에 필요한 데이터를 저장합니다
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
            fee = fee
        )
    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MyStudyRegisterPreviewFragment()) // MyStudyRegisterPreviewFragment로 전환
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
