import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.spot.android.R
import com.spot.android.databinding.FragmentMemberStudyBinding
import com.spot.android.ui.study.FixedRoundedSpinnerAdapter
import com.spot.android.ui.study.MemberNumberRVAdapter

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

        binding.rvMemberStudyNum.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateChildAlpha()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updateChildAlpha()
                }
            }
        })






                binding.rvMemberStudyNum.adapter = memberAdapter
        binding.rvMemberStudyNum.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        memberAdapter.onItemClick = { position ->
            smoothScrollToCenter(position)
            memberAdapter.setSelectedPosition(position)
            binding.selectedNumberText.text = "${String.format("%02d", numbers[position])}명"
            binding.fragmentMemberStudyBt.isEnabled = true
        }

        viewModel.studyRequest.observe(viewLifecycleOwner) { request ->
            if (viewModel.mode.value == StudyFormMode.EDIT && request != null) {
                binding.fragmentMemberStudyTv.text = "스터디 정보 수정"
                // 참여 인원 반영
                val index = (1..9).indexOf(request.maxPeople)
                if (index != -1) {
                    memberAdapter.setSelectedPosition(index)
                    smoothScrollToCenter(index)
                    binding.selectedNumberText.text = "${String.format("%02d", request.maxPeople)}명"
                    binding.fragmentMemberStudyBt.isEnabled = true
                }

                // 성별 반영
                val genderIndex = when (request.gender) {
                    Gender.MALE -> 1
                    Gender.FEMALE -> 2
                    else -> 0
                }
                binding.fragmentMemberStudyGenderSpinner.setSelection(genderIndex)

                // 연령대 반영
                val slider = binding.fragmentMemberStudyAgeAgeRangeSlider
                slider.setValues(request.minAge.toFloat(), request.maxAge.toFloat())
                binding.fragmentMemberStudyAgeMinValueTv.text = request.minAge.toString()
                binding.fragmentMemberStudyAgeMaxValueTv.text = request.maxAge.toString()
            }
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
        val maxPeople = memberAdapter.getSelectedNumber()

        val selectedGender = binding.fragmentMemberStudyGenderSpinner.selectedItem.toString()
        val gender = when (selectedGender) {
            "남성" -> Gender.MALE
            "여성" -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }

        val ageRangeSlider = binding.fragmentMemberStudyAgeAgeRangeSlider
        val minAge = ageRangeSlider.values[0].toInt()
        val maxAge = ageRangeSlider.values[1].toInt()

        val profileImage = viewModel.studyRequest.value?.profileImage
        val hasFee = viewModel.studyRequest.value?.hasFee

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
            profileImage = profileImage,
            hasFee = hasFee
        )

    }

    private fun goToNextFragment() {
        val nextFragment = ActivityFeeStudyFragment().apply {
            arguments = Bundle().apply {
                putSerializable("mode", viewModel.mode.value)
                viewModel.studyId.value?.let { putInt("studyId", it) }
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frm, nextFragment)
            .addToBackStack(null)
            .commit()
    }
    private fun goToPreviusFragment() {
        parentFragmentManager.popBackStack()
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
        }

        binding.rvMemberStudyNum.postDelayed({
            updateChildAlpha()
        }, 200)
    }

    private fun updateChildAlpha() {
        val layoutManager = binding.rvMemberStudyNum.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisible..lastVisible) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            val alpha = if (i == firstVisible || i == lastVisible) 0.4f else 1f
            view.alpha = alpha
        }
    }



}