package com.example.spoteam_android.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.MainActivity
import com.example.spoteam_android.databinding.FragmentCalendarBinding
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var imgbtnAddEvent: ImageButton
    private val eventViewModel: EventViewModel by activityViewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        calendarView = binding.calendarView
        eventsRecyclerView = binding.eventrecyclerview
        imgbtnAddEvent = binding.imgbtnAddEvent

        eventAdapter = EventAdapter(emptyList(), { event ->
            // 여기에 출석체크 넣으세요!!
            (activity as MainActivity).switchFragment(CalendarAddEventFragment())
        }, false)

        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsRecyclerView.adapter = eventAdapter

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            Log.d("CalendarFragment", "Date selected: $year-${month + 1}-$dayOfMonth")
            eventViewModel.loadEvents(year, month + 1, dayOfMonth)
//            todoViewModel.loadTodosForDate(year, month + 1, dayOfMonth)
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

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgbtnAddEvent.setOnClickListener {
            (activity as MainActivity).switchFragment(CalendarAddEventFragment())
        }
    }
}
