package com.example.spoteam_android

import android.app.DatePickerDialog
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
    private lateinit var eventDateTextView: TextView
    private lateinit var saveButton: ImageButton

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_add_event, container, false)
        eventTitleEditText = view.findViewById(R.id.eventTitleEditText)
        eventDateTextView = view.findViewById(R.id.eventDateTextView)
        saveButton = view.findViewById(R.id.saveButton)

        // 기본값으로 오늘 날짜 설정
        val calendar = Calendar.getInstance()
        val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        eventDateTextView.text = today

        eventDateTextView.setOnClickListener { showDatePickerDialog(eventDateTextView) }

        saveButton.setOnClickListener {
            val title = eventTitleEditText.text.toString()
            val date = eventDateTextView.text.toString()

            // 날짜를 구분자로 분리하여 year, month, day 추출
            val parts = date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            val event = Event(
                id = EventRepository.getNextId(),
                title = title,
                year = year,
                month = month,
                day = day,
                startDate = date,
                endDate = date,
                startTime = "",
                endTime = ""
            )

            eventViewModel.addEvent(event)
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                textView.text = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
