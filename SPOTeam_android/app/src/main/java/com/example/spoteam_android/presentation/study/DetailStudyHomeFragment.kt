package com.example.spoteam_android.presentation.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.DetailStudyHomeAdapter
import com.example.spoteam_android.ProfileItem
import com.example.spoteam_android.SceduleItem
import com.example.spoteam_android.databinding.FragmentDetailStudyHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class DetailStudyHomeFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyHomeBinding
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private lateinit var scheduleAdapter: DetailStudyHomeAdapter
    private val page = 0
    private val size = 4

    private var isMemberLoaded = false
    private var isScheduleLoaded = false
    private var isAnnounceLoaded = false

    private val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailStudyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        studyViewModel.studyId.observe(viewLifecycleOwner) { studyId ->
            if (studyId != null) {
                fetchStudySchedules(studyId)
                fetchStudyMembers(studyId)
                fetchRecentAnnounce(studyId)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.fragmentDetailStudyHomeProfileRv.viewTreeObserver.addOnGlobalLayoutListener {
            binding.fragmentDetailStudyHomeProfileRv.requestLayout()
            binding.fragmentDetailStudyHomeProfileRv.invalidate()
        }
        studyViewModel.studyId.value?.let { fetchStudyMembers(it) }
    }

    private fun setupViews() {
        binding.fragmentDetailStudyHomeProfileRv.visibility = View.INVISIBLE
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList(), null, false)
        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        scheduleAdapter = DetailStudyHomeAdapter(ArrayList())
        binding.fragmentDetailStudyHomeScheduleRv.layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentDetailStudyHomeScheduleRv.adapter = scheduleAdapter

        studyViewModel.studyIntroduction.observe(viewLifecycleOwner) {
            binding.fragmentDetailStudyHomeIntroduceTv.text = it
        }
    }

    private fun fetchStudyMembers(studyId: Int) {
        studyViewModel.fetchStudyMembers(studyId) { response ->
            val members = response?.members ?: emptyList()
            profileAdapter.updateList(members.map {
                ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
            })
            isMemberLoaded = true
            checkAndShowContent()
        }
    }

    private fun fetchStudySchedules(studyId: Int) {
        studyViewModel.fetchStudySchedules(studyId, page, size) { response ->
            val schedules = response?.schedules ?: emptyList()
            val now = Calendar.getInstance().time
            val validSchedules = schedules.filter {
                val startDate = parseDate(it.startedAt)
                val endDate = parseDate(it.finishedAt)
                (startDate != null && startDate.after(now)) || (endDate != null && endDate.after(now))
            }
            val nearestSchedules = validSchedules.sortedBy { it.startedAt }.take(2)
            if (nearestSchedules.isEmpty()) {
                binding.fragmentDetailStudyHomeScheduleRv.visibility = View.GONE
            } else {
                binding.fragmentDetailStudyHomeScheduleRv.visibility = View.VISIBLE
                val scheduleItems = nearestSchedules.map {
                    SceduleItem(
                        dday = calculateDday(it.startedAt, it.finishedAt),
                        day = formatDate(it.startedAt),
                        scheduleContent = it.title,
                        concreteTime = formatTime(it.startedAt),
                        place = it.location
                    )
                }
                scheduleAdapter.updateList(ArrayList(scheduleItems))
            }
            isScheduleLoaded = true
            checkAndShowContent()
        }
    }

    private fun fetchRecentAnnounce(studyId: Int) {
        studyViewModel.fetchRecentAnnounce(studyId) { response ->
            val title = response?.title
            binding.fragmentDetailStudyHomeTitleTv.text = if (title.isNullOrEmpty()) "최근 공지가 없습니다" else title
            isAnnounceLoaded = true
            checkAndShowContent()
        }
    }

    private fun parseDate(dateStr: String?): java.util.Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateStr ?: return null)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDate(dateStr: String?): String {
        val date = parseDate(dateStr) ?: return "N/A"
        return SimpleDateFormat("M/d (EEE)", Locale.getDefault()).format(date)
    }

    private fun formatTime(dateStr: String?): String {
        val date = parseDate(dateStr) ?: return "N/A"
        return SimpleDateFormat("M/d h:mma", Locale.US).format(date)
    }

    private fun calculateDday(startedAt: String?, finishedAt: String?): String {
        val startDate = parseDate(startedAt) ?: return "N/A"
        val endDate = parseDate(finishedAt) ?: return "N/A"
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return when {
            today.before(startDate) -> "D-${((startDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()}"
            today in startDate..endDate -> "D-day"
            else -> "D+${((today.time - endDate.time) / (1000 * 60 * 60 * 24)).toInt()}"
        }
    }

    private fun checkAndShowContent() {
        if (isMemberLoaded && isScheduleLoaded && isAnnounceLoaded) {
            binding.root.visibility = View.VISIBLE
            binding.fragmentDetailStudyHomeProfileRv.visibility = View.VISIBLE
            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.VISIBLE
        }
    }
}