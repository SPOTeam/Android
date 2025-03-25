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
import com.example.spoteam_android.ui.study.FixedRoundedSpinnerAdapter
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

        val numbers = (1..9).toList()
        memberAdapter = MemberNumberRVAdapter(numbers)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvMemberStudyNum)




        binding.rvMemberStudyNum.adapter = memberAdapter
        binding.rvMemberStudyNum.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        memberAdapter.onItemClick = { position ->
            smoothScrollToCenter(position)
            memberAdapter.setSelectedPosition(position)
            binding.selectedNumberText.text = "${String.format("%02d", numbers[position])}명"
            binding.fragmentMemberStudyBt.isEnabled = true
        }









        return binding.root
    }


    private fun setupSpinners() {
        val genderList = listOf("누구나", "남성", "여성")
        val genderAdapter = FixedRoundedSpinnerAdapter(requireContext(), genderList)
        binding.fragmentMemberStudyGenderSpinner.adapter = genderAdapter
    }



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

//    private fun updateNextButtonState() {
//        val isChecked = binding.
//
//        if (isOnline) {
//            binding.fragmentOnlineStudyBt.isEnabled = true
//            binding.fragmentOnlineStudyBt.visibility = View.VISIBLE
//            binding.fragmentOnlineStudyLocationPlusBt.visibility = View.GONE
//        } else {
//            if (hasChip) {
//                binding.fragmentOnlineStudyBt.isEnabled = true
//                binding.fragmentOnlineStudyBt.visibility = View.VISIBLE
//                binding.fragmentOnlineStudyLocationPlusBt.visibility = View.GONE
//            } else {
//                binding.fragmentOnlineStudyBt.isEnabled = false
//                binding.fragmentOnlineStudyBt.visibility = View.GONE
//                binding.fragmentOnlineStudyLocationPlusBt.visibility = View.VISIBLE
//            }
//        }
//    }

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
    private fun smoothScrollToCenter(position: Int) {
        val layoutManager = binding.rvMemberStudyNum.layoutManager as LinearLayoutManager
        val view = layoutManager.findViewByPosition(position)
        if (view != null) {
            val rvCenterX = binding.rvMemberStudyNum.width / 2
            val viewCenterX = view.left + view.width / 2
            val scrollBy = viewCenterX - rvCenterX
            binding.rvMemberStudyNum.smoothScrollBy(scrollBy, 0)
        } else {
            binding.rvMemberStudyNum.scrollToPosition(position)
            binding.rvMemberStudyNum.post {
                val v = layoutManager.findViewByPosition(position)
                v?.let {
                    val rvCenterX = binding.rvMemberStudyNum.width / 2
                    val viewCenterX = it.left + it.width / 2
                    val scrollBy = viewCenterX - rvCenterX
                    binding.rvMemberStudyNum.smoothScrollBy(scrollBy, 0)
                }
            }
        }
    }

}