package com.example.spoteam_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var eventsTextView: TextView
    private lateinit var icBar: ImageView
    private lateinit var icCheck: ImageView
    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        eventsTextView = view.findViewById(R.id.eventsTextView)
        icBar = view.findViewById(R.id.ic_bar)
        icCheck = view.findViewById(R.id.ic_check)



        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            Log.d("CalendarFragment", "Date selected: $year-${month + 1}-$dayOfMonth")
            eventViewModel.loadEvents(year, month + 1, dayOfMonth)

        }


        eventViewModel.events.observe(viewLifecycleOwner, Observer { events ->
            Log.d("CalendarFragment", "Events updated: ${events.size} events")
            displayEvents(events)
            updateEventIconsVisibility(events)
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

    private fun displayEvents(events: List<Event>) {
        Log.d("CalendarFragment", "Displaying ${events.size} events")
        eventsTextView.text = events.joinToString("\n") { "${it.title}" }
    }
    private fun updateEventIconsVisibility(events: List<Event>) {
        if (events.isNotEmpty()) {
            icBar.visibility = View.VISIBLE
            icCheck.visibility = View.VISIBLE
        } else {
            icBar.visibility = View.GONE
            icCheck.visibility = View.GONE
        }
    }
}
