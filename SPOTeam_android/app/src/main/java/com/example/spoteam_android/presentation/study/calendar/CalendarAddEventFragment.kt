package com.example.spoteam_android.presentation.study.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.databinding.FragmentCalendarAddEventBinding
import com.example.spoteam_android.domain.study.entity.MakeScheduleRequest
import com.example.spoteam_android.presentation.study.CompleteScheduleDialog
import com.example.spoteam_android.presentation.study.StudyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class CalendarAddEventFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCalendarAddEventBinding
    private lateinit var eventTitleEditText: EditText
    private lateinit var eventPositionEditText: EditText
    private lateinit var startDateTimeTextView: TextView
    private lateinit var endDateTimeTextView: TextView
    private lateinit var charTitleCountText: TextView
    private lateinit var charPostionCountText: TextView
    private lateinit var saveButton: Button
    private lateinit var closeButton: ImageView
    private lateinit var spinner: Spinner
    private lateinit var checkBox: CheckBox
    private lateinit var startYearTx: TextView
    private lateinit var startTimeTx: TextView
    private lateinit var endYearTx: TextView
    private lateinit var endTimeTx: TextView
    private lateinit var txEndGuide: TextView
    private lateinit var txEveryDay: TextView

    private val studyViewModel: StudyViewModel by activityViewModels()

    private var studyId: Int = 0
    private var period: String = "NONE"
    private var startDateTime = ""
    private var endDateTime = ""
    private var isAllDay = false
    private var isStartDateSet = false
    private var isEndDateSet = false

    override fun getTheme(): Int = R.style.InterestBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarAddEventBinding.inflate(inflater, container, false)

        eventTitleEditText = binding.eventTitleEditText
        eventPositionEditText = binding.eventPositionEditText
        startDateTimeTextView = binding.startDateTimeTextView
        endDateTimeTextView = binding.endDateTimeTextView
        saveButton = binding.fragmentIntroduceStudyBt
        closeButton = binding.icClose
        spinner = binding.routineSpinner
        checkBox = binding.checkBoxEveryDay
        startYearTx = binding.txStartYear
        startTimeTx = binding.txStartTime
        endYearTx = binding.txEndYear
        endTimeTx = binding.txEndTime
        txEndGuide = binding.txEndGuide
        txEveryDay = binding.txEveryDay
        charTitleCountText = binding.charTitleCountText
        charPostionCountText = binding.charPositionCountText

        studyId = arguments?.getInt("studyId") ?: 0

        txEveryDay.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            isAllDay = isChecked
            if (isAllDay) {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                startDateTime = String.format("%04d-%02d-%02d 00:00", year, month, day)
                startYearTx.text = String.format("%04d.%02d.%02d", year, month, day)
                startTimeTx.text = "00:00 am"

                endDateTimeTextView.visibility = View.GONE
                txEndGuide.visibility = View.GONE
                endYearTx.visibility = View.GONE
                endTimeTx.visibility = View.GONE
            } else {
                resetDateTimeFields()
                endDateTimeTextView.visibility = View.VISIBLE
                txEndGuide.visibility = View.VISIBLE
            }
        }

        eventTitleEditText.addTextChangedListener(createTextWatcher(charTitleCountText))
        eventPositionEditText.addTextChangedListener(createTextWatcher(charPostionCountText))

        startDateTimeTextView.setOnClickListener {
            // 생략: showDateTimePickerDialog
        }
        endDateTimeTextView.setOnClickListener {
            // 생략: showDateTimePickerDialog
        }

        saveButton.setOnClickListener {
            uploadScheduleToServer(isAllDay)
        }

        closeButton.setOnClickListener { dismiss() }

        return binding.root
    }

    private fun createTextWatcher(counterView: TextView) = object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateSaveButtonState()
        }
        override fun afterTextChanged(s: Editable?) {
            counterView.text = "(${s?.length ?: 0}/20)"
        }
    }

    private fun updateSaveButtonState() {
        val isTitleValid = eventTitleEditText.text.isNotEmpty()
        val isPositionValid = eventPositionEditText.text.isNotEmpty()
        saveButton.isEnabled = isTitleValid && isPositionValid && (isStartDateSet && isEndDateSet || isAllDay)
    }

    private fun resetDateTimeFields() {
        startDateTime = ""
        endDateTime = ""
        startYearTx.text = ""
        startTimeTx.text = ""
        endYearTx.text = ""
        endTimeTx.text = ""
        isStartDateSet = false
        isEndDateSet = false
        updateSaveButtonState()
    }

    private fun uploadScheduleToServer(isChecked: Boolean) {
        val title = eventTitleEditText.text.toString()
        val location = eventPositionEditText.text.toString()
        val start = startDateTime
        val end = if (isChecked) "${start.split(" ")[0]} 23:59" else endDateTime

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val startDate: Date = inputFormat.parse(start)
        val endDate: Date = inputFormat.parse(end)

        val formattedStart = outputFormat.format(startDate)
        val formattedEnd = outputFormat.format(endDate)

        val request = MakeScheduleRequest(
            title = title,
            location = location,
            startedAt = formattedStart,
            finishedAt = formattedEnd,
            isAllDay = isChecked,
            period = period
        )

        studyViewModel.makeSchedule(
            studyId = studyId,
            request = request,
            onResult = { success ->
                if (success) showCompletionDialog()
                else Toast.makeText(requireContext(), "일정 추가 실패", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showCompletionDialog() {
        val dialog = CompleteScheduleDialog(requireContext(), startDateTime)
        dialog.start(parentFragmentManager)
    }
    private fun showDateTimePickerDialog(txYear: TextView, txTime: TextView, isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.MyDatePickerTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)

                txYear.text = String.format("%04d.%02d.%02d", selectedYear, selectedMonth + 1, selectedDay)

                if (isAllDay) {
                    if (isStart) {
                        startDateTime = "$selectedDate 00:00"
                        txTime.text = "00:00 am"
                        isStartDateSet = true
                    } else {
                        endDateTime = "$selectedDate 23:59"
                        txTime.text = "11:59 pm"
                        isEndDateSet = true
                    }
                    updateSaveButtonState()
                } else {
                    showTimePickerDialog(selectedDate, txTime, isStart)
                }

                if (isStart) {
                    startYearTx.visibility = View.VISIBLE
                    startTimeTx.visibility = View.VISIBLE
                } else {
                    endYearTx.visibility = View.VISIBLE
                    endTimeTx.visibility = View.VISIBLE
                }
            },
            year, month, day
        )

        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.show()

        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setTypeface(typeface, Typeface.BOLD)
        }

        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setTypeface(typeface, Typeface.BOLD)
        }
    }
    private fun showTimePickerDialog(selectedDate: String, txTime: TextView, isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyTimePickerTheme,
            { _, selectedHour, selectedMinute ->
                val isAm = selectedHour < 12
                val amPm = if (isAm) "am" else "pm"
                val hourFormatted = if (selectedHour % 12 == 0) 12 else selectedHour % 12
                val formattedTime = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)

                txTime.text = formattedTime

                if (isStart) {
                    startDateTime = "$selectedDate %02d:%02d".format(selectedHour, selectedMinute)
                    isStartDateSet = true
                } else {
                    endDateTime = "$selectedDate %02d:%02d".format(selectedHour, selectedMinute)
                    isEndDateSet = true
                }

                updateSaveButtonState()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.setCanceledOnTouchOutside(false)
        timePickerDialog.show()

        val negativeButton = timePickerDialog.findViewById<Button>(android.R.id.button2)
        negativeButton?.visibility = View.GONE

        timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setTypeface(typeface, Typeface.BOLD)
        }
    }



    companion object {
        fun newInstance(studyId: Int): CalendarAddEventFragment {
            val fragment = CalendarAddEventFragment()
            val args = Bundle()
            args.putInt("studyId", studyId)
            fragment.arguments = args
            return fragment
        }
    }
}
