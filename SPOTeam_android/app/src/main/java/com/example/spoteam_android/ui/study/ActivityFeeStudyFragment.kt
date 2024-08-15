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
import com.example.spoteam_android.databinding.FragmentActivityFeeStudyBinding
import com.example.spoteam_android.ui.study.MyStudyRegisterPreviewFragment
import com.example.spoteam_android.ui.study.RegisterStudyFragment

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

        return binding.root
    }

    private fun setupChipGroupListener() {
        binding.fragmentOnlineStudyChipgroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.fragment_activity_fee_study_chip_false -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.GONE
                    saveStudyData(fee = 0) // 활동비 없음
                    binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
                }
                R.id.fragment_activity_fee_study_chip_true -> {
                    binding.fragmentActivityFeeStudyNumFl.visibility = View.VISIBLE
                    updateFee()
                    checkFeeInput()  // 추가된 메서드
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
        } else {
            binding.fragmentActivityFeeStudyEt.error = null
            binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true
            saveStudyData(fee) // 입력된 활동비를 ViewModel에 저장
        }
    }

    private fun setupPreviewButtonListener() {
        binding.fragmentActivityFeeStudyPreviewBt.setOnClickListener {
            goToNextFragment() // 버튼 클릭 시 다음 프래그먼트로 이동
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
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, RegisterStudyFragment()) // 이전 프래그먼트로 전환
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
