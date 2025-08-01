package com.umcspot.android.ui.study

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.umcspot.android.DetailStudyHomeAdapter
import com.umcspot.android.MemberResponse
import com.umcspot.android.ProfileItem
import com.umcspot.android.RecentAnnounceResponse
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.SceduleItem
import com.umcspot.android.ScheduleListResponse
import com.umcspot.android.databinding.FragmentDetailStudyHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailStudyHomeFragment : Fragment() {

    private lateinit var binding: FragmentDetailStudyHomeBinding
    private lateinit var profileAdapter: DetailStudyHomeProfileAdapter
    private lateinit var scheduleAdapter: DetailStudyHomeAdapter
    private val page = 0
    private val size = 4


    private var isMemberLoaded = false
    private var isScheduleLoaded = false
    private var isAnnounceLoaded = false

    val studyViewModel: StudyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDetailStudyHomeBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews() // 뷰 바인딩 초기화 및 Adapter 연결

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

        studyViewModel.studyId.value?.let { studyId ->
            fetchStudyMembers(studyId)
        }
    }



    private fun setupViews() {
        //프로필 업데이트
        binding.fragmentDetailStudyHomeProfileRv.visibility = View.INVISIBLE
        profileAdapter = DetailStudyHomeProfileAdapter(ArrayList(),null,false)
        binding.fragmentDetailStudyHomeProfileRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.fragmentDetailStudyHomeProfileRv.adapter = profileAdapter

        //스케쥴 업데이트
        scheduleAdapter = DetailStudyHomeAdapter(ArrayList())
        binding.fragmentDetailStudyHomeScheduleRv.layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentDetailStudyHomeScheduleRv.adapter = scheduleAdapter

        studyViewModel.studyIntroduction.observe(viewLifecycleOwner) { introduction ->
            binding.fragmentDetailStudyHomeIntroduceTv.text = introduction
        }

    }

    private fun fetchStudyMembers(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentMemberId = sharedPreferences.getInt("${sharedPreferences.getString("currentEmail", "")}_memberId", -1)

        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { memberResponse ->
                        val members = memberResponse.result?.members ?: emptyList()
                        members.forEach { member ->
                            Log.d("StudyMembers", "Member ID: ${member.memberId}, Nickname: ${member.nickname}, Profile Image: ${member.profileImage ?: "NULL"}")
                        }


                        // 프로필 어댑터 업데이트
                        profileAdapter.updateList(members.map {
                            ProfileItem(profileImage = it.profileImage, nickname = it.nickname)
                        })
                        isMemberLoaded = true
                        checkAndShowContent()

                    }
                } else {
                    isMemberLoaded = true
                    checkAndShowContent()
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                isMemberLoaded = true
                checkAndShowContent()

            }
        })
    }






    private fun fetchStudySchedules(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getStudySchedules(studyId, page, size).enqueue(object : Callback<ScheduleListResponse> {
            override fun onResponse(call: Call<ScheduleListResponse>, response: Response<ScheduleListResponse>) {
                if (response.isSuccessful) {
                    val scheduleListResponse = response.body()
                    val schedules = scheduleListResponse?.result?.schedules ?: emptyList()

                    if (schedules.isEmpty()) {
                        binding.fragmentDetailStudyHomeScheduleRv.visibility = View.GONE
                    } else {
                        // 현재 시간
                        val now = Calendar.getInstance().time

                        // 현재 시간 이후이거나, 일정이 진행 중인 것만 필터링
                        val validSchedules = schedules.filter { schedule ->
                            val startDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(schedule.startedAt)
                            val endDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(schedule.finishedAt)

                            // 시작 시간이 현재보다 미래이거나, 종료 시간이 현재보다 미래이면 표시
                            (startDate != null && startDate.after(now)) || (endDate != null && endDate.after(now))
                        }

                        // 가장 가까운 일정 2개만 가져오기 (시작일 기준 정렬)
                        val nearestSchedules = validSchedules.sortedBy { it.startedAt }.take(2)

                        if (nearestSchedules.isEmpty()) {
                            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.GONE
                        } else {
                            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.VISIBLE

                            val scheduleItems = nearestSchedules.map { schedule ->
                                SceduleItem(
                                    dday = calculateDday(schedule.startedAt, schedule.finishedAt),
                                    day = formatDate(schedule.startedAt),
                                    scheduleContent = schedule.title,
                                    concreteTime = formatTime(schedule.startedAt),
                                    place = schedule.location
                                )
                            }
                            scheduleAdapter.updateList(ArrayList(scheduleItems))
                        }
                    }
                    isScheduleLoaded = true
                    checkAndShowContent()
                } else {
                    isScheduleLoaded = true
                    checkAndShowContent()
                }
            }

            override fun onFailure(call: Call<ScheduleListResponse>, t: Throwable) {
                isScheduleLoaded = true
                checkAndShowContent()
            }
        })
    }


    private fun fetchRecentAnnounce(studyId: Int) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        api.getRecentAnnounce(studyId).enqueue(object : Callback<RecentAnnounceResponse> {
            override fun onResponse(call: Call<RecentAnnounceResponse>, response: Response<RecentAnnounceResponse>) {
                if (response.isSuccessful) {
                    val announceResponse = response.body()
                    val recentAnnounce = announceResponse?.result

                    if (recentAnnounce?.title.isNullOrEmpty()) {
                        binding.fragmentDetailStudyHomeTitleTv.text = "최근 공지가 없습니다"
                    } else {
                        binding.fragmentDetailStudyHomeTitleTv.text = recentAnnounce?.title ?: "최근 공지가 없습니다"
                    }
                    isAnnounceLoaded = true
                    checkAndShowContent()
                } else {
                    isAnnounceLoaded = true
                    checkAndShowContent()
                }
            }
            override fun onFailure(call: Call<RecentAnnounceResponse>, t: Throwable) {
                isAnnounceLoaded = true
                checkAndShowContent()
            }
        })
    }

    private fun calculateDday(startedAt: String?, finishedAt: String?): String {
        if (startedAt.isNullOrEmpty() || finishedAt.isNullOrEmpty()) {
            return "N/A"
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = formatter.parse(startedAt)
        val endDate = formatter.parse(finishedAt)

        if (startDate == null || endDate == null) {
            return "N/A"
        }

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val todayDate = today.time

        return when {
            todayDate.before(startDate) -> { // 일정 시작 전
                val daysLeft = ((startDate.time - todayDate.time) / (1000 * 60 * 60 * 24)).toInt()
                "D-${daysLeft}"
            }
            todayDate in startDate..endDate -> { // 일정 진행 중
                "D-day"
            }
            else -> { // 일정 종료 후
                val daysPassed = ((todayDate.time - endDate.time) / (1000 * 60 * 60 * 24)).toInt()
                "D+${daysPassed}"
            }
        }
    }


    // Null 체크를 추가한 날짜 변경 로직
    private fun formatDate(startedAt: String?): String {
        if (startedAt.isNullOrEmpty()) {
            return "N/A"
        }

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("M/d (EEE)", Locale.getDefault())

        val date = inputFormatter.parse(startedAt)
        return outputFormatter.format(date)
    }

    // Null 체크를 추가한 시간 변경 로직
    private fun formatTime(staredAt: String?): String {
        if (staredAt.isNullOrEmpty()) {
            return "N/A"
        }

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("M/d h:mma", Locale.US) // Locale.US를 사용하여 AM/PM을 영어로 유지

        val date = inputFormatter.parse(staredAt)
        return outputFormatter.format(date)
    }

    private fun checkAndShowContent() {
        if (isMemberLoaded && isScheduleLoaded && isAnnounceLoaded) {
            binding.root.visibility = View.VISIBLE
            binding.fragmentDetailStudyHomeProfileRv.visibility = View.VISIBLE
            binding.fragmentDetailStudyHomeScheduleRv.visibility = View.VISIBLE
        }
    }

}
