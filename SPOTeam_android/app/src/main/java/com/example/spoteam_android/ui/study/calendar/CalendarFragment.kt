package com.example.spoteam_android.ui.study.calendar

import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCalendarBinding
import com.example.spoteam_android.ui.study.quiz.CheckAttendanceFragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
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

        calendarView.setLeftArrow(R.drawable.left_arrow);

        customRectangleDecorator = CustomRectangleDecorator(requireContext())
        calendarView.addDecorator(customRectangleDecorator)


        start = arguments?.getString("my_start")


        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val todayDate = CalendarDay.from(year, month, day)
        calendarView.setCurrentDate(todayDate)  // 캘린더를 오늘 날짜로 설정
        calendarView.setSelectedDate(todayDate) // 오늘 날짜를 선택
        calendarView.invalidateDecorators()     // Decorator 강제 갱신

        val studyId = studyViewModel.studyId.value ?: 0

        // 오늘 날짜의 일정을 로드
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, day)

            // 어댑터와 데코레이터 갱신 추가
            val selectedDate = String.format("%04d-%02d-%02d", year, month, day)
            eventAdapter.updateSelectedDate(selectedDate)

            val date = CalendarDay.from(year, month, day)
            customRectangleDecorator.setSelectedDate(date)
            calendarView.invalidateDecorators() // 데코레이터 갱신

            calendarView.setDateTextAppearance(R.style.CustomDateTextAppearance)

            // 어댑터 데이터 갱신
            eventAdapter.updateEvents(eventViewModel.events.value ?: emptyList())
        }





        start?.let { dateStr ->
            try {
                // 날짜 문자열을 Date로 변환
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = formatter.parse(dateStr)

                // Calendar 객체를 생성하여 날짜 설정
                val calendar = Calendar.getInstance()
                calendar.time = date

                // Calendar를 CalendarDay로 변환하여 MaterialCalendarView에서 사용
                val calendarDay = CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, // 월은 0부터 시작하므로 1을 더해줌
                    calendar.get(Calendar.DAY_OF_MONTH)
                )


                // 캘린더를 해당 날짜로 이동하고, 선택 상태로 설정합니다.
                calendarView.setCurrentDate(calendarDay)
                calendarView.setSelectedDate(calendarDay)
                calendarView.invalidateDecorators() // Decorator 강제 갱신

                fetchGetSchedule(studyId, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1) {
                    eventViewModel.loadEvents(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }

                Log.d("CalendarFragment", "Selected startDateTime: $calendarDay")
            } catch (e: Exception) {
                Log.e("CalendarFragment", "Error parsing date: ${e.message}")
            }
        }

        // TodayDecorator를 CalendarView에 추가

        addButton = binding.addButton

        eventsRecyclerView = binding.eventrecyclerview

        addButton.setOnClickListener {
            val studyId = studyViewModel.studyId.value ?: 0
            val fragment = CalendarAddEventFragment.newInstance(studyId)
            (activity as MainActivity).switchFragment(fragment)
        }

        binding.addButton.visibility = View.GONE // 기본값을 GONE으로 설정하여 버튼 숨기기

        studyViewModel.studyOwner.observe(viewLifecycleOwner) { studyOwner ->
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val currentEmail = sharedPreferences.getString("currentEmail", null)
            val kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")

            if (kakaoNickname == studyOwner) {
                binding.addButton.visibility = View.VISIBLE // 닉네임이 일치할 경우 버튼 보이기
            } else {
                binding.addButton.visibility = View.GONE // 닉네임이 일치하지 않을 경우 버튼 숨기기
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
            eventAdapter.updateEvents(emptyList())
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
}
