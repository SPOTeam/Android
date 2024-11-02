package com.example.spoteam_android.ui.calendar

import RetrofitClient.getAuthToken
import StudyApiService
import StudyViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.BoardAdapter
import com.example.spoteam_android.BoardItem
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCalendarBinding
import com.example.spoteam_android.ui.study.OnlineStudyFragment
import com.example.spoteam_android.ui.study.StudyFragment
import com.example.spoteam_android.ui.study.quiz.HostMakeQuizFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var eventAdapter: EventAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var todayDecorator: TodayDecorator
    private lateinit var selectedDateDecorator: TodayDecorator
    private lateinit var addButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendarView = binding.calendarView

        // TodayDecorator를 CalendarView에 추가
        todayDecorator = TodayDecorator(requireContext())
        selectedDateDecorator = TodayDecorator(requireContext())

        // Decorator 추가
        calendarView.addDecorator(todayDecorator)
        calendarView.addDecorator(selectedDateDecorator)
        addButton = binding.addButton



        eventsRecyclerView = binding.eventrecyclerview

//        fab  = binding.fab
//        fab.setOnClickListener {
//            val studyId = studyViewModel.studyId.value ?: 0
//            val fragment = CalendarAddEventFragment.newInstance(studyId)
//            (activity as MainActivity).switchFragment(fragment)
//        }

        addButton.setOnClickListener {
            val studyId = studyViewModel.studyId.value ?: 0
            val fragment = CalendarAddEventFragment.newInstance(studyId)
            (activity as MainActivity).switchFragment(fragment)
        }

        binding.fab.visibility = View.GONE // 기본값을 GONE으로 설정하여 버튼 숨기기

        studyViewModel.studyOwner.observe(viewLifecycleOwner) { studyOwner ->
            val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val currentEmail = sharedPreferences.getString("currentEmail", null)
            val kakaoNickname = sharedPreferences.getString("${currentEmail}_nickname", "Unknown")

            if (kakaoNickname == studyOwner) {
                binding.fab.visibility = View.VISIBLE // 닉네임이 일치할 경우 버튼 보이기
            } else {
                binding.fab.visibility = View.GONE // 닉네임이 일치하지 않을 경우 버튼 숨기기
            }
        }




        eventAdapter = EventAdapter(emptyList(), { event ->
            val hostMakeQuizFragment = HostMakeQuizFragment()
            hostMakeQuizFragment.show(parentFragmentManager, "HostMakeQuizFragment")
        }, false)

        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        calendarView.setOnMonthChangedListener { _, date ->
            val year = date.year
            val month = date.month // Calendar.MONTH는 0부터 시작하므로 +1 필요
            val day = date.day

            val studyId = studyViewModel.studyId.value ?: 0
            fetchGetSchedule(studyId, year, month) {
                eventViewModel.loadEvents(year, month, day)
            }
        }

        calendarView.setOnDateChangedListener  { _, date, selected ->
            if (selected) {
                val year = date.year
                val month = date.month
                val day = date.day

                val studyId = studyViewModel.studyId.value ?: 0

                todayDecorator.setSelectedDate(date)
                selectedDateDecorator.setSelectedDate(date)
                calendarView.invalidateDecorators() // Decorator 강제 갱신


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

        // 초기 진입 시 오늘 날짜 이벤트 로드
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val studyId = studyViewModel.studyId.value ?: 0
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, day)
        }



        return binding.root
    }




    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int, onComplete: () -> Unit) {

        val scheduleBoard = binding.eventrecyclerview
        val EventItems = arrayListOf<Event>()

        RetrofitClient.CAService.GetScheuled(
            authToken = getAuthToken(),
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
//                                    schedule.period
//                                    schedule.isAllDay
//                                    schedule.location
                            )

                            EventItems.add(eventItem)
                        }
                        eventViewModel.updateEvents(EventItems)
                        onComplete()
                    } else {
                        Toast.makeText(requireContext(), "조건에 맞는 항목이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
            }
        })
    }
}
