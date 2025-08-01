package com.spot.android.ui.study.calendar

import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spot.android.MemberResponse
import com.spot.android.R
import com.spot.android.RetrofitInstance
import com.spot.android.databinding.FragmentCalendarBinding
import com.spot.android.ui.study.quiz.CheckAttendanceFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var calendarView: MaterialCalendarView
    private val eventViewModel: EventViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var customRectangleDecorator: CustomRectangleDecorator
    private lateinit var eventAdapter: EventAdapter
    private lateinit var addButton: ImageButton
    private var start: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendarView = binding.calendarView

        calendarView.setLeftArrow(R.drawable.left_arrow)
        calendarView.setRightArrow(R.drawable.right_arrow)
        calendarView.setWeekDayFormatter(CustomWeekDayFormatter(requireContext()))

        customRectangleDecorator = CustomRectangleDecorator(requireContext())
        calendarView.addDecorator(customRectangleDecorator)


        start = arguments?.getString("my_start")


        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val date = CalendarDay.from(year, month, day)

        val studyId = studyViewModel.studyId.value ?: 0

        // 오늘 날짜의 일정을 로드
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, day)

            // 어댑터와 데코레이터 갱신 추가
            val selectedDate = String.format("%04d-%02d-%02d", year, month, day)
            eventAdapter.updateSelectedDate(selectedDate)
            customRectangleDecorator.setSelectedDate(date)
            calendarView.invalidateDecorators() // 데코레이터 갱신

            calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance)

            // 어댑터 데이터 갱신
            eventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())

        }

        addButton = binding.addButton

        eventsRecyclerView = binding.eventrecyclerview

        addButton.setOnClickListener {
            val studyId = studyViewModel.studyId.value ?: 0
            val bottomSheet = CalendarAddEventFragment.newInstance(studyId)
            bottomSheet.show(parentFragmentManager, "CalendarAddEventBottomSheet")
        }


        studyViewModel.studyOwner.observe(viewLifecycleOwner) { studyOwner ->
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val currentEmail = sharedPreferences.getString("currentEmail", null)
            val kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")

            if (kakaoNickname == studyOwner) {
                binding.addButton.visibility = View.VISIBLE
            } else {
                binding.addButton.visibility = View.GONE
            }
        }

        eventAdapter = EventAdapter(emptyList(), { event ->
            val hostMakeQuizFragment = CheckAttendanceFragment()



            // scheduleId를 전달하기 위해 Bundle 생성
            val bundle = Bundle().apply {
                putInt("scheduleId", event.id) // Event 객체의 id를 scheduleId로 전달
            }
            hostMakeQuizFragment.arguments = bundle

            // DialogFragment 설정 및 표시
            hostMakeQuizFragment.show(parentFragmentManager, "HostMakeQuizFragment")
            hostMakeQuizFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogBorder20WhiteTheme)
        }, false)



        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        // 월이 넘어가면 event 초기화
        calendarView.setOnMonthChangedListener { _, date ->
            val year = date.year
            val month = date.month
            val studyId = studyViewModel.studyId.value ?: 0

            fetchGetSchedule(studyId, year, month) {
                eventViewModel.loadEvents(year, month, -1) // day는 선택 안 되었으므로 -1 또는 null로
            }

            calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance)
        }

        calendarView.setOnDateChangedListener  { _, date, selected ->
            if (selected) {
                val year = date.year
                val month = date.month
                val day = date.day

                val studyId = studyViewModel.studyId.value ?: 0
                val selectedDate = String.format("%04d-%02d-%02d", year, month, day)
                eventAdapter.updateSelectedDate(selectedDate)

                calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance)
                customRectangleDecorator.setSelectedDate(date)
                calendarView.invalidateDecorators() // 데코레이터 갱신

                fetchGetSchedule(studyId, year, month) {
                    eventViewModel.loadEvents(year, month, day)
                }
            }
        }

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            val eventDates = eventViewModel.getEventDates()
            calendarView.addDecorator(EventDecorator(eventDates))
            eventAdapter.updateEvents(events)
        })


        isCurrentUserStudyMember(studyId) { isMember ->
            if (isMember) {
                addButton.visibility = View.VISIBLE
            } else {
                addButton.visibility = View.GONE
            }
        }

        return binding.root
    }


    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int, onComplete: () -> Unit) {

        val EventItems = arrayListOf<Event>()
        val service = RetrofitInstance.retrofit.create(CalendarApiService::class.java)

        service.GetScheuled(
            studyId = studyId,
            year = year,
            month = month,
        ).enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(
                call: Call<ScheduleResponse>,
                response: Response<ScheduleResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("CalendarFragment","$apiResponse" )

                    if (apiResponse?.isSuccess == true) {
                        apiResponse.result.scheduleList.forEach { schedule ->
                            val eventItem = Event(
                                id = schedule.scheduleId,
                                title = schedule.title,
                                startYear = schedule.startedAt.substring(0, 4).toInt(),
                                startMonth = schedule.startedAt.substring(5, 7).toInt(),
                                startDay = schedule.startedAt.substring(8, 10).toInt(),
                                startHour = schedule.startedAt.substring(11, 13).toInt(),
                                startMinute = schedule.startedAt.substring(14, 16).toInt(),
                                endYear = schedule.finishedAt.substring(0, 4).toInt(),
                                endMonth = schedule.finishedAt.substring(5, 7).toInt(),
                                endDay = schedule.finishedAt.substring(8, 10).toInt(),
                                endHour = schedule.finishedAt.substring(11, 13).toInt(),
                                endMinute = schedule.finishedAt.substring(14, 16).toInt(),
                                period = schedule.period,
                                isAllDay = schedule.isAllDay
                            )

                            EventItems.add(eventItem)
                        }
                        eventViewModel.updateEvents(EventItems)
                        eventAdapter.updateEvents(EventItems)
                        onComplete()
                    } else {
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
            }
        })
    }


    fun getMemberId(context: Context): Int {

        var memberId: Int = -1

        val sharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPreferences.getString("currentEmail", null)

        // 현재 로그인된 사용자 정보를 로그
        memberId = if (currentEmail != null) sharedPreferences.getInt(
            "${currentEmail}_memberId",
            -1
        ) else -1

        return memberId // 저장된 memberId 없을 시 기본값 -1 반환
    }


    private fun isCurrentUserStudyMember(
        studyId: Int,
        onResult: (Boolean) -> Unit
    ) {
        val api = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        // SharedPreferences에서 현재 로그인된 memberId 가져오기
        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("currentEmail", null)
        val currentMemberId = if (email != null) {
            sharedPreferences.getInt("${email}_memberId", -1)
        } else {
            -1
        }

        if (currentMemberId == -1) {
            onResult(false)
            return
        }

        // API 호출
        api.getStudyMembers(studyId).enqueue(object : Callback<MemberResponse> {
            override fun onResponse(call: Call<MemberResponse>, response: Response<MemberResponse>) {
                if (response.isSuccessful) {
                    val members = response.body()?.result?.members ?: emptyList()
                    val isMember = members.any { it.memberId == currentMemberId } // ✅ memberId 기준 비교
                    onResult(isMember)
                } else {
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<MemberResponse>, t: Throwable) {
                onResult(false)
            }
        })
    }





}
