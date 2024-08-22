package com.example.spoteam_android.ui.calendar

import StudyApiService
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.spoteam_android.R
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.ScheduleRequest
import com.example.spoteam_android.ScheduleResponse
import com.example.spoteam_android.ui.mypage.PurposeUploadComplteDialog
import com.example.spoteam_android.ui.study.CompleteScheduleDialog
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
    private var studyId: Int = 0

    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar_add_event, container, false)
        eventTitleEditText = view.findViewById(R.id.eventTitleEditText)
        eventPositionEditText = view.findViewById(R.id.eventPositionEditText)
        startDateTimeTextView = view.findViewById(R.id.startDateTimeTextView)
        endDateTimeTextView = view.findViewById(R.id.endDateTimeTextView)
        saveButton = view.findViewById(R.id.fragment_introduce_study_bt)

        studyId = arguments?.getInt("studyId") ?: 0

        Log.d("CalendarAddEventFragment1", "Received studyId: $studyId")

        eventTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.isEnabled = s?.isNotEmpty() == true
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 기본값으로 오늘 날짜와 시간 설정
        val calendar = Calendar.getInstance()
        val today = String.format(
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        val now = String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        startDateTimeTextView.text = "$today $now"
        endDateTimeTextView.text = "$today $now"

        startDateTimeTextView.setOnClickListener { showDateTimePickerDialog(startDateTimeTextView) }
        endDateTimeTextView.setOnClickListener { showDateTimePickerDialog(endDateTimeTextView) }

        saveButton.setOnClickListener {
            addEventToViewModel()
            // 서버에 일정 업로드
            uploadScheduleToServer()

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
                        textView.text = String.format(
                            "%04d-%02d-%02d %02d:%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay,
                            selectedHour,
                            selectedMinute
                        )
                    },
                    hour, minute, true
                )
                timePickerDialog.show()
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun uploadScheduleToServer() {
        val title = eventTitleEditText.text.toString()
        val location = eventPositionEditText.text.toString() // 위치 입력
        val startDateTime = startDateTimeTextView.text.toString()
        val endDateTime = endDateTimeTextView.text.toString()
        val isAllDay = false
        val period = "NONE"

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
        Log.d("CalendarAddEventFragment", "Received studyId2: $studyId")
        val jsonRequestBody = Gson().toJson(scheduleRequest)
        Log.d("CalendarAddEventFragment", "Request body: $jsonRequestBody")


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

    private fun addEventToViewModel() {
        val title = eventTitleEditText.text.toString()
        val startDateTime = startDateTimeTextView.text.toString()
        val endDateTime = endDateTimeTextView.text.toString()

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
        val dialog = CompleteScheduleDialog(requireContext())
        dialog.start(parentFragmentManager)
    }
}
