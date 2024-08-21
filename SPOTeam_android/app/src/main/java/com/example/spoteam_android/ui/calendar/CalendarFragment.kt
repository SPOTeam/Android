package com.example.spoteam_android.ui.calendar

import RetrofitClient.getAuthToken
import StudyApiService
import StudyViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.example.spoteam_android.ui.study.quiz.HostMakeQuizFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var imgbtnAddEvent: ImageButton
    private val eventViewModel: EventViewModel by activityViewModels()
    private val studyViewModel: StudyViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)

        calendarView = binding.calendarView
        eventsRecyclerView = binding.eventrecyclerview
        imgbtnAddEvent = binding.imgbtnAddEvent

        eventAdapter = EventAdapter(emptyList(), { event ->
            val hostMakeQuizFragment = HostMakeQuizFragment()
            hostMakeQuizFragment.show(parentFragmentManager, "HostMakeQuizFragment")
        }, false)

        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val studyId = studyViewModel.studyId.value ?: 0 // 기본값 0
            fetchGetSchedule(studyId, year, month + 1) {
                eventViewModel.loadEvents(year, month + 1, dayOfMonth)
            }

        }

        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            Log.d("CalendarFragment", "Events updated: ${events.size} events")
            eventAdapter.updateEvents(events)
        })

        // 초기 진입 시 오늘 날짜 이벤트 로드
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        Log.d("CalendarFragment", "Initial load: $year-$month-$day")

        val studyId = studyViewModel.studyId.value ?: 0
        Log.d("My","이벤트 초기 로드")
        fetchGetSchedule(studyId, year, month) {
            eventViewModel.loadEvents(year, month, day)
        }



        return binding.root
    }




    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int, onComplete: () -> Unit) {
        Log.d("CalendarFragment", "fetchGetSchedule() 실행")

        val scheduleBoard = binding.eventrecyclerview
        val EventItems = arrayListOf<Event>()

        Log.d("CalendarFragment","$year")
        Log.d("CalendarFragment","$month")
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
                Log.d("My",response.toString())
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("CalendarFragment", "$apiResponse")

                    if (apiResponse?.isSuccess == true) {
                        apiResponse.result.scheduleList.forEach { schedule ->
                            Log.d("CalendarFragment", "Schedule ID: ${schedule.scheduleId}")
                            Log.d("CalendarFragment", "Title: ${schedule.title}")
                            Log.d("CalendarFragment", "Started At: ${schedule.startedAt}")
                            Log.d("CalendarFragment", "Finished At: ${schedule.finishedAt}")
                            Log.d("CalendarFragment", "Period: ${schedule.period}")
                            Log.d("CalendarFragment", "Is All Day: ${schedule.isAllDay}")
                            Log.d("CalendarFragment", "Location: ${schedule.location}")
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
                        Log.d("CalendarFragment", "isSuccess == False")
                    }
                } else {
                    Log.d("CalendarFragment", "연결 실패: ${response.code()} - ${response.message()}")
                    Log.e("CalendarFragment", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Log.d("CalendarFragment", "API 호출 실패: ${t.message}")
            }
        })
    }
}
