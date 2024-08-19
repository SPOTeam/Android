package com.example.spoteam_android.ui.calendar

import RetrofitClient.getAuthToken
import StudyApiService
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
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.databinding.FragmentCalendarBinding
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
    private lateinit var eventAdapter: EventAdapter
    private var studyId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studyId = arguments?.getInt("studyId") ?: 0
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
            fetchGetSchedule(
                9,year,month
            )
            Log.d("CalendarFragment", "Date selected: $year-${month + 1}-$dayOfMonth")
            eventViewModel.loadEvents(year, month + 1, dayOfMonth)
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
        eventViewModel.loadEvents(year, month, day)


        return binding.root
    }

    private fun fetchGetSchedule(studyId: Int, year: Int, month: Int) {
        Log.d("CalendarFragment", "fetchGetSchedule() 실행")

        RetrofitClient.CAService.GetScheuled(
            authToken = getAuthToken(),
            studyId = 9,
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
                        val schedules = apiResponse.result.scheduleList
                        // schedules 리스트를 UI에 표시하는 로직을 여기에 추가합니다.
                        Log.d("CalendarFragment", "받은 일정 개수: ${schedules.size}")
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

