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
import com.example.spoteam_android.ui.study.OnlineStudyFragment
import com.google.gson.Gson

class ActivityFeeStudyFragment : Fragment() {
    private lateinit var binding: FragmentActivityFeeStudyBinding
    private val viewModel: StudyViewModel by activityViewModels()
    private val gson = Gson()

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
                        profileImage = viewModel.studyRequest.value?.profileImage ?: "null", // null 가능성 유지
                        regions = viewModel.studyRequest.value?.regions ?: emptyList(),
                        maxPeople = viewModel.studyRequest.value?.maxPeople ?: 0,
                        gender = viewModel.studyRequest.value?.gender ?: Gender.UNKNOWN,
                        minAge = viewModel.studyRequest.value?.minAge ?: 0,
                        maxAge = viewModel.studyRequest.value?.maxAge ?: 0,
                        fee = 0 // 활동비 없음
                    )
                    binding.fragmentActivityFeeStudyPreviewBt.isEnabled = true // 버튼 활성화
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
            // ViewModel에 저장된 데이터 JSON으로 변환하여 로그 출력
            val studyData = viewModel.studyRequest.value
            val studyDataJson = gson.toJson(studyData)

            Log.d("ActivityFeeStudyFragment", "$studyDataJson")
            val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val email = sharedPreferences.getString("currentEmail", null)

            if (email != null) {
                // 이메일 기반으로 memberId 가져오기
                val memberId = sharedPreferences.getInt("${email}_memberId", -1)

                if (memberId != -1) {
                    viewModel.submitStudyData(memberId)
                } else {
                    Toast.makeText(requireContext(), "Member ID not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Email not provided", Toast.LENGTH_SHORT).show()
            }


            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frm, MyStudyRegisterPreviewFragment()) // 변경할 Fragment로 교체
            transaction.commit()
        }
    }

    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, MemberStudyFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }
}