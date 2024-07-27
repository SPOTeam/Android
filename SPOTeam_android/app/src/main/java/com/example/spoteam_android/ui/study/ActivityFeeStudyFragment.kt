import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentActivityFeeStudyBinding

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

        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_activity_fee_study_chip_false -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
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
                        fee = 0 // 활동비 없음
                    )
                }
                R.id.fragment_activity_fee_study_chip_true -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                    updateFee()
                }
            }
        }
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

    private fun updateFee() {
        val feeText = binding.fragmentActivityFeeStudyEt.text.toString()
        val fee = feeText.toIntOrNull() ?: 0

        if (fee > 10000) {
            binding.fragmentActivityFeeStudyEt.error = "최대 10,000원까지 입력 가능합니다."
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = false
        } else {
            binding.fragmentActivityFeeStudyEt.error = null
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
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
    }

    private fun setupPreviewButtonListener() {
        binding.fragmentActivityFeeStudyPreviewBt.setOnClickListener {
            viewModel.submitStudyData()
            // 서버 전송 후 사용자에게 알림을 추가하거나 다른 액션을 수행할 수 있습니다.
        }
    }
}

