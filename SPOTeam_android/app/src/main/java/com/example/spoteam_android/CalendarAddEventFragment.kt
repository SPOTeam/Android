package com.example.spoteam_android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.util.*

class CalendarAddEventFragment : Fragment() {

    private lateinit var eventTitleEditText: EditText
    private lateinit var startDateTimeTextView: TextView
    private lateinit var endDateTimeTextView: TextView
    private lateinit var saveButton: ImageButton

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_add_event, container, false)
        eventTitleEditText = view.findViewById(R.id.eventTitleEditText)
        startDateTimeTextView = view.findViewById(R.id.startDateTimeTextView)
        endDateTimeTextView = view.findViewById(R.id.endDateTimeTextView)
        saveButton = view.findViewById(R.id.saveButton)

            // 기본값으로 오늘 날짜와 시간 설정
        val calendar = Calendar.getInstance()
        val today = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
        val now = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

        startDateTimeTextView.text = "$today $now"
        endDateTimeTextView.text = "$today $now"

        startDateTimeTextView.setOnClickListener { showDateTimePickerDialog(startDateTimeTextView) }
        endDateTimeTextView.setOnClickListener { showDateTimePickerDialog(endDateTimeTextView) }

        saveButton.setOnClickListener {
            val title = eventTitleEditText.text.toString()
            val startDateTime = startDateTimeTextView.text.toString()
            val endDateTime = endDateTimeTextView.text.toString()

            // 날짜와 시간을 구분자로 분리하여 year, month, day, hour, minute 추출
            val startParts = startDateTime.split(" ", "-", ":")
            val startYear = startParts[0].toInt()
            val startMonth = startParts[1].toInt()
            val startDay = startParts[2].toInt()
            val startHour = startParts[3].toInt()
            val startMinute = startParts[4].toInt()

            val endParts = endDateTime.split(" ", "-", ":")
            val endYear = endParts[0].toInt()
            val endMonth = endParts[1].toInt()
            val endDay = endParts[2].toInt()
            val endHour = endParts[3].toInt()
            val endMinute = endParts[4].toInt()

            val event = Event(
                id = EventRepository.getNextId(),
                title = title,
                startYear = startYear,
                startMonth = startMonth,
                startDay = startDay,
                startHour = startHour,
                startMinute = startMinute,
                endYear = endYear,
                endMonth = endMonth,
                endDay = endDay,
                endHour = endHour,
                endMinute = endMinute,
            )

            eventViewModel.addEvent(event)
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun showDateTimePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->
                        textView.text = String.format("%04d-%02d-%02d %02d:%02d", selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute)
                    },
                    hour, minute, true
                )
                timePickerDialog.show()
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
