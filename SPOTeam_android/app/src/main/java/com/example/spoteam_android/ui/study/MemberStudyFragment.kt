import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentMemberStudyBinding
import com.example.spoteam_android.ui.study.MemberNumberRVAdapter
import com.example.spoteam_android.ui.study.OnlineStudyFragment

class MemberStudyFragment : Fragment() {
    private lateinit var binding: FragmentMemberStudyBinding
    private val viewModel: StudyViewModel by activityViewModels()
    private lateinit var memberAdapter: MemberNumberRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemberStudyBinding.inflate(inflater, container, false)

        setupSpinners()
        setupRangeSlider()
        binding.fragmentMemberStudyBt.setOnClickListener {
            saveData()
            goToNextFragment()
        }
        binding.fragmentMemberStudyBackBt.setOnClickListener {
            goToPreviusFragment()
        }

        val numbers = (0..9).toList()
        memberAdapter = MemberNumberRVAdapter(numbers)


        binding.rvMemberStudyNum.adapter = memberAdapter
        binding.rvMemberStudyNum.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        memberAdapter.onItemClick = { position ->
            binding.rvMemberStudyNum.smoothScrollToPosition(position)

            val selectedNumber = numbers[position]
            memberAdapter.setSelectedPosition(position)
            binding.selectedNumberText.text = "${String.format("%02d", selectedNumber)}명"
        }



        return binding.root
    }

    private fun setupSpinners() {
        // 참여 인원 Spinner 설정
        // 성별 Spinner 설정
        val genderSpinner: Spinner = binding.fragmentMemberStudyGenderSpinner
        val genderAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            R.layout.spinner_item
        )
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter
    }

//    private fun setupSpinners() {
//        val context = requireContext()
//
//        // 참여 인원 Spinner 설정
//        val numSpinner: Spinner = binding.fragmentMemberStudyNumSpinner
//        val numItems = resources.getStringArray(R.array.fragment_study_num_spinner_array).toList()
//        val numAdapter = CustomSpinnerAdapter(context, numItems)
//        numSpinner.adapter = numAdapter
//
//        // 성별 Spinner 설정
//        val genderSpinner: Spinner = binding.fragmentMemberStudyGenderSpinner
//        val genderItems = resources.getStringArray(R.array.gender_array).toList()
//        val genderAdapter = CustomSpinnerAdapter(context, genderItems)
//        genderSpinner.adapter = genderAdapter
//    }


    private fun setupRangeSlider() {
        val ageRangeSlider = binding.fragmentMemberStudyAgeAgeRangeSlider
        val minValueText = binding.fragmentMemberStudyAgeMinValueTv
        val maxValueText = binding.fragmentMemberStudyAgeMaxValueTv

        ageRangeSlider.valueFrom = 18f
        ageRangeSlider.valueTo = 60f
        ageRangeSlider.stepSize = 1f
        ageRangeSlider.values = listOf(18f, 60f)

        ageRangeSlider.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            minValueText.text = values[0].toInt().toString()
            maxValueText.text = values[1].toInt().toString()
        }
    }

    private fun saveData() {
        val maxPeople = memberAdapter.getSelectedNumber()  // 👈 새로 만든 함수로 가져오기

        val selectedGender = binding.fragmentMemberStudyGenderSpinner.selectedItem.toString()
        val gender = when (selectedGender) {
            "남자" -> Gender.MALE
            "여자" -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }

        val ageRangeSlider = binding.fragmentMemberStudyAgeAgeRangeSlider
        val minAge = ageRangeSlider.values[0].toInt()
        val maxAge = ageRangeSlider.values[1].toInt()

        // profileImage를 ViewModel에서 가져오는 예시
        val profileImage = viewModel.studyRequest.value?.profileImage

        viewModel.setStudyData(
            title = viewModel.studyRequest.value?.title.orEmpty(),
            goal = viewModel.studyRequest.value?.goal.orEmpty(),
            introduction = viewModel.studyRequest.value?.introduction.orEmpty(),
            isOnline = viewModel.studyRequest.value?.isOnline ?: true,
            regions = viewModel.studyRequest.value?.regions ?: emptyList(),
            maxPeople = maxPeople,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            fee = viewModel.studyRequest.value?.fee ?: 0,
            profileImage = profileImage // profileImage 파라미터 추가
        )

    }

    private fun goToNextFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, ActivityFeeStudyFragment())
        transaction.addToBackStack(null) // 뒤로 가기 스택에 추가
        transaction.commit()
    }
    private fun goToPreviusFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frm, OnlineStudyFragment()) // 변경할 Fragment로 교체
        transaction.addToBackStack(null) // 백스택에 추가
        transaction.commit()
    }
}