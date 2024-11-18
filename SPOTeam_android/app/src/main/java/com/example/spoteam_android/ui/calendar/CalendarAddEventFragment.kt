package com.example.spoteam_android.ui.calendar

import StudyApiService
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ScheduleRequest
import com.example.spoteam_android.ScheduleResponse
import com.example.spoteam_android.ui.study.CompleteScheduleDialog
import com.example.spoteam_android.ui.study.StudyFragment
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CalendarAddEventFragment : Fragment() {

    private lateinit var eventTitleEditText: EditText
    private lateinit var eventPositionEditText: EditText
    private lateinit var startDateTimeTextView: TextView
    private lateinit var endDateTimeTextView: TextView
    private lateinit var saveButton: Button
    private lateinit var closeButton: ImageView
    private lateinit var spinner: Spinner
    private lateinit var checkBox: CheckBox
    private lateinit var checkBoxViewModel: CheckBoxViewModel
    private lateinit var startYearTx: TextView
    private lateinit var startTimeTx: TextView
    private lateinit var endYearTx: TextView
    private lateinit var endTimeTx: TextView
    private lateinit var txEndGuide: TextView
    private var studyId: Int = 0
    private var period: String = "NONE"
    private var startDateTime = ""
    private var endDateTime = ""
    private var isAllDay = false


    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_add_event, container, false)

        checkBoxViewModel = ViewModelProvider(this).get(CheckBoxViewModel::class.java)

        eventTitleEditText = view.findViewById(R.id.eventTitleEditText)
        eventPositionEditText = view.findViewById(R.id.eventPositionEditText)
        startDateTimeTextView = view.findViewById(R.id.startDateTimeTextView)
        endDateTimeTextView = view.findViewById(R.id.endDateTimeTextView)

        saveButton = view.findViewById(R.id.fragment_introduce_study_bt)
        closeButton = view.findViewById(R.id.write_content_prev_iv)
        spinner = view.findViewById(R.id.routine_spinner)
        checkBox = view.findViewById(R.id.checkBox_every_day)
        startYearTx = view.findViewById(R.id.tx_start_year)
        startTimeTx = view.findViewById(R.id.tx_start_time)
        endYearTx = view.findViewById(R.id.tx_end_year)
        endTimeTx = view.findViewById(R.id.tx_end_time)
        txEndGuide = view.findViewById(R.id.tx_end_guide)

        studyId = arguments?.getInt("studyId") ?: 0

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.routine_array,
            R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter


        // CheckBox 상태가 변경될 때 ViewModel의 isSpinnerEnabled 값 변경
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            isAllDay = isChecked
            if (isAllDay) {
                // 시간을 자동으로 설정
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH는 0부터 시작
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                // 시작 시간 설정
                startDateTime = String.format("%04d-%02d-%02d 00:00", year, month, day)
                startYearTx.text = String.format("%04d.%02d.%02d", year, month, day)
                startTimeTx.text = "00:00 am"

                // 종료 시간 TextView 및 가이드 숨기기
                endDateTimeTextView.visibility = View.GONE
                txEndGuide.visibility = View.GONE
                endYearTx.visibility = View.GONE
                endTimeTx.visibility = View.GONE
            } else {

                resetDateTimeFields()

                // 종료 시간 TextView 및 가이드 표시
                endDateTimeTextView.visibility = View.VISIBLE
                txEndGuide.visibility = View.VISIBLE


            }
        }

        // Spinner의 onItemSelectedListener 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Spinner가 비활성화 상태면 기본 period 값 설정 후 동작을 무시
                if (checkBoxViewModel.isCheckBoxChecked.value == false) {
                    period = "NONE"
                    return
                }

//                 Spinner의 선택에 따라 period 값을 설정
                period = when (position) {
                    0 -> "NONE"
                    1 -> "DAILY"
                    2 -> "WEEKLY"
                    3 -> "BIWEEKLY"
                    4 -> "MONTHLY"
                    else -> "NONE"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 선택되지 않았을 때 기본값 설정
                period = "NONE"
            }
        }


        eventTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        eventPositionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        startDateTimeTextView.setOnClickListener {
            showDateTimePickerDialog(startYearTx,startTimeTx,true)
            updateSaveButtonState()
        }
        endDateTimeTextView.setOnClickListener {
            showDateTimePickerDialog(endYearTx,endTimeTx,false)
            updateSaveButtonState()
        }

        saveButton.setOnClickListener {
            addEventToViewModel(isAllDay)
            uploadScheduleToServer(isAllDay)
        }

        closeButton.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        view.setOnTouchListener { _, _ ->
            clearFocusFromEditTexts()
            true
        }

        return view
    }


    private fun showDateTimePickerDialog(txYear: TextView, txTime: TextView, isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format(
                    "%04d-%02d-%02d",
                    selectedYear,
                    selectedMonth + 1,
                    selectedDay
                )

                if (isStart) {
                    // 시작 날짜 및 시간 설정
                    startDateTime = "$selectedDate 00:00"
                    txYear.text = String.format("%04d.%02d.%02d", selectedYear, selectedMonth + 1, selectedDay)
                } else {
                    // 종료 날짜 설정 (endDateTime 유효성 확인)
                    val endTime = if (endDateTime.isNotEmpty() && endDateTime.contains(" ")) {
                        endDateTime.split(" ")[1] // 기존 종료 시간 유지
                    } else {
                        "23:59" // 기본 값
                    }
                    endDateTime = "$selectedDate $endTime"
                    txYear.text = String.format("%04d.%02d.%02d", selectedYear, selectedMonth + 1, selectedDay)
                }

                // TimePickerDialog 열기
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHourOfDay, selectedMinute ->
                        val isAm = selectedHourOfDay < 12
                        val amPm = if (isAm) "am" else "pm"
                        val hourFormatted =
                            if (selectedHourOfDay % 12 == 0) 12 else selectedHourOfDay % 12

                        val formattedTime = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)

                        if (isStart) {
                            startDateTime = String.format("%s %02d:%02d", selectedDate, selectedHourOfDay, selectedMinute)
                            txTime.text = formattedTime
                        } else {
                            endDateTime = String.format("%s %02d:%02d", selectedDate, selectedHourOfDay, selectedMinute)
                            txTime.text = formattedTime
                        }

                        // Visibility 업데이트
                        if (isStart) {
                            startYearTx.visibility = View.VISIBLE
                            startTimeTx.visibility = View.VISIBLE
                        } else {
                            endYearTx.visibility = View.VISIBLE
                            endTimeTx.visibility = View.VISIBLE
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
                )
                timePickerDialog.show()
            },
            year, month, day
        )

        // 종료 날짜 선택 시 제한 적용
        if (!isStart && period == "DAILY") {
            val startParts = startDateTime.split(" ", "-", ":")
            val startCalendar = Calendar.getInstance().apply {
                set(startParts[0].toInt(), startParts[1].toInt() - 1, startParts[2].toInt())
            }
            datePickerDialog.datePicker.minDate = startCalendar.timeInMillis
            datePickerDialog.datePicker.maxDate = startCalendar.timeInMillis
        }

        datePickerDialog.show()
    }




    private fun uploadScheduleToServer(isChecked: Boolean) {
        val title = eventTitleEditText.text.toString()
        val location = eventPositionEditText.text.toString() // 위치 입력
        val startDateTime = startDateTime
        val endDateTime = if (isChecked) {
            val dateParts = startDateTime.split(" ")
            "${dateParts[0]} 23:59"
        } else {
            endDateTime
        }

        val isAllDay = isAllDay
        val period = period

        // ISO 8601 형식으로 변환
        val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        // startDateTime과 endDateTime을 Date로 파싱
        val startDate: Date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(startDateTime)
        val endDate: Date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(endDateTime)

        // ISO 8601 형식으로 포맷팅
        val formattedStartDateTime = isoDateTimeFormat.format(startDate)
        val formattedEndDateTime = isoDateTimeFormat.format(endDate)

        val scheduleRequest = ScheduleRequest(
            title = title,
            location = location,
            startedAt = formattedStartDateTime,
            finishedAt = formattedEndDateTime,
            isAllDay = isAllDay,
            period = period
        )
        val jsonRequestBody = Gson().toJson(scheduleRequest)


        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val call = apiService.addSchedules(studyId, scheduleRequest)

        call.enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful) {
                    val scheduleResponse = response.body()
                    if (scheduleResponse != null && scheduleResponse.isSuccess) {
                        showCompletionDialog()
                    } else {
                        Toast.makeText(requireContext(), "일정 추가에 실패했습니다: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addEventToViewModel(isChecked: Boolean) {
        val title = eventTitleEditText.text.toString()
        val startDateTime = startDateTime
        val endDateTime = if (isChecked) {
            val dateParts = startDateTime.split(" ")
            "${dateParts[0]} 23:59"
        } else {
            endDateTime
        }


        val startParts = startDateTime.split(" ", "-", ":")
        val endParts = endDateTime.split(" ", "-", ":")

        val event = Event(
            id = EventRepository.getNextId(),
            title = title,
            startYear = startParts[0].toInt(),
            startMonth = startParts[1].toInt(),
            startDay = startParts[2].toInt(),
            startHour = startParts[3].toInt(),
            startMinute = startParts[4].toInt(),
            endYear = endParts[0].toInt(),
            endMonth = endParts[1].toInt(),
            endDay = endParts[2].toInt(),
            endHour = endParts[3].toInt(),
            endMinute = endParts[4].toInt()
        )
        eventViewModel.addEvent(event)
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
    private fun showCompletionDialog() {
        val dialog = CompleteScheduleDialog(requireContext(),startDateTime)
        dialog.start(parentFragmentManager)
    }
}