package com.umcspot.android.ui.study.calendar

import StudyApiService
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.umcspot.android.R
import com.umcspot.android.RetrofitInstance
import com.umcspot.android.ScheduleRequest
import com.umcspot.android.ScheduleResponse
import com.umcspot.android.databinding.FragmentCalendarAddEventBinding
import com.umcspot.android.ui.study.CompleteScheduleDialog
import com.umcspot.android.ui.study.FixedRoundedSpinnerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CalendarAddEventFragment  : BottomSheetDialogFragment() {
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
    private lateinit var checkBoxViewModel: CheckBoxViewModel
    private lateinit var startYearTx: TextView
    private lateinit var startTimeTx: TextView
    private lateinit var endYearTx: TextView
    private lateinit var endTimeTx: TextView
    private lateinit var txEndGuide: TextView
    private lateinit var txEveryDay: TextView
    private var studyId: Int = 0
    private var period: String = "NONE"
    private var startDateTime = ""
    private var endDateTime = ""
    private var isAllDay = false
    private var isStartDateSet = false
    private var isEndDateSet = false

    override fun getTheme(): Int = R.style.InterestBottomSheetDialogTheme

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let {
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val targetHeight = (screenHeight * 0.8).toInt() // 화면의 70%

            it.layoutParams.height = targetHeight

            val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
            behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private val eventViewModel: EventViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarAddEventBinding.inflate(inflater, container, false)

        setupSpinners()

        checkBoxViewModel = ViewModelProvider(this).get(CheckBoxViewModel::class.java)

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


        //'하루종일' 텍스트 클릭 시 체크박스 상태 변경 반영
        txEveryDay.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
        }


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




        eventTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                charTitleCountText.text = "($length/20)"
            }
        })

        eventPositionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                charPostionCountText.text = "($length/20)"
            }
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
            dismiss()
        }

        view?.setOnTouchListener { _, _ ->
            clearFocusFromEditTexts()
            true
        }



        return return binding.root
    }

    private fun setupSpinners() {
        val periodList = listOf("안함", "매일", "매주", "격주", "매월")
        val periodAdapter = FixedRoundedSpinnerAdapter(requireContext(), periodList)
        binding.routineSpinner.adapter = periodAdapter
        binding.routineSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                try {
                    // null-safe 처리
                    val isChecked = checkBoxViewModel.isCheckBoxChecked.value ?: false
                    if (!isChecked) {
                        period = "NONE"
                        return
                    }

                    val newPeriod = when (position) {
                        0 -> "NONE"
                        1 -> "DAILY"
                        2 -> "WEEKLY"
                        3 -> "BIWEEKLY"
                        4 -> "MONTHLY"
                        else -> "NONE"
                    }

                    if (newPeriod != period) {
                        period = newPeriod
                        resetDateTimeFields()
                    }

                } catch (e: Exception) {
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 안전하게 기본값 설정
                period = "NONE"
                resetDateTimeFields()
            }
        }
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

                    if (isAllDay) {
                        // All Day가 체크된 경우 시간 기본값 설정 및 TimePickerDialog 생략
                        txTime.text = "00:00 am" // 기본 시작 시간
                        isStartDateSet = true // 시작 날짜 설정 완료
                        updateSaveButtonState() // 버튼 상태 업데이트
                    } else {
                        // All Day가 체크되지 않은 경우 TimePickerDialog 표시
                        showTimePickerDialog(selectedDate, txTime, isStart)
                    }
                } else {
                    // 종료 날짜 설정
                    val endTime = if (endDateTime.isNotEmpty() && endDateTime.contains(" ")) {
                        endDateTime.split(" ")[1] // 기존 종료 시간 유지
                    } else {
                        "23:59" // 기본 종료 시간
                    }
                    endDateTime = "$selectedDate $endTime"
                    txYear.text = String.format("%04d.%02d.%02d", selectedYear, selectedMonth + 1, selectedDay)

                    if (isAllDay) {
                        // All Day가 체크된 경우 시간 기본값 설정 및 TimePickerDialog 생략
                        txTime.text = "11:59 pm" // 기본 종료 시간
                        isEndDateSet = true // 종료 날짜 설정 완료
                        updateSaveButtonState() // 버튼 상태 업데이트
                    } else {
                        // All Day가 체크되지 않은 경우 TimePickerDialog 표시
                        showTimePickerDialog(selectedDate, txTime, isStart)
                    }
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
            year, month, day
        )

        // 종료 날짜 제한 적용
        if (!isStart) {
            applyDateRestrictions(datePickerDialog)
        }

        datePickerDialog.show()
        datePickerDialog.setCanceledOnTouchOutside(false) // 바깥 터치 방지

        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) // 텍스트 색상
            setTypeface(typeface, Typeface.BOLD) // Bold 적용
        }

        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) // 텍스트 색상
            setTypeface(typeface, Typeface.BOLD) // Bold 적용
        }
    }

    // TimePickerDialog를 분리하여 가독성 개선
    private fun showTimePickerDialog(selectedDate: String, txTime: TextView, isStart: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.MyTimePickerTheme, // 스타일 적용
            { _, selectedHourOfDay, selectedMinute ->
                val isAm = selectedHourOfDay < 12
                val amPm = if (isAm) "am" else "pm"
                val hourFormatted =
                    if (selectedHourOfDay % 12 == 0) 12 else selectedHourOfDay % 12
                val formattedTime = String.format("%02d:%02d %s", hourFormatted, selectedMinute, amPm)

                val startParts = startDateTime.split(" ", ":", "-")
                val startDate = "${startParts[0]}-${startParts[1]}-${startParts[2]}"
                val startHour = startParts[3].toInt()
                val startMinute = startParts[4].toInt()

                if (selectedDate == startDate) {
                    // 같은 날일 때는 종료 시간이 시작 시간 이후인지 체크
                    if (selectedHourOfDay < startHour || (selectedHourOfDay == startHour && selectedMinute <= startMinute)) {
                        Toast.makeText(requireContext(), "종료 시간은 시작 시간 이후여야 합니다", Toast.LENGTH_SHORT).show()
                        resetEndDateTimeFields() // 종료 시간 초기화
                        return@TimePickerDialog
                    }
                }

                if (isStart) {
                    startDateTime = String.format("%s %02d:%02d", selectedDate, selectedHourOfDay, selectedMinute)
                    txTime.text = formattedTime
                    isStartDateSet = true // 시작 날짜 설정 완료
                } else {
                    endDateTime = String.format("%s %02d:%02d", selectedDate, selectedHourOfDay, selectedMinute)
                    txTime.text = formattedTime
                    isEndDateSet = true // 종료 날짜 설정 완료
                }

                updateSaveButtonState() // 버튼 상태 업데이트
            },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
        timePickerDialog.setCanceledOnTouchOutside(false) // 바깥 터치 방지

        val negativeButton = timePickerDialog.findViewById<Button>(android.R.id.button2)
        negativeButton?.visibility = View.GONE

        timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black)) // 텍스트 색상
            setTypeface(typeface, Typeface.BOLD) // Bold 적용
        }
    }

    // 종료 날짜 제한 로직
    private fun applyDateRestrictions(datePickerDialog: DatePickerDialog) {
        Log.d("CalendarDebug", "💬 startDateTime = \"$startDateTime\"")  // ✅ 콘솔 출력

        if (startDateTime.isBlank() || !startDateTime.contains("-")) return

        val parts = startDateTime.split("-", " ", ":")
        if (parts.size < 3) return

        val startCalendar = Calendar.getInstance().apply {
            set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // 날짜 제한 설정
        datePickerDialog.datePicker.minDate = startCalendar.timeInMillis

        when (period) {
            "DAILY" -> {
                datePickerDialog.datePicker.minDate = startCalendar.timeInMillis
                datePickerDialog.datePicker.maxDate = startCalendar.timeInMillis
            }
            "WEEKLY" -> {
                val maxEndCalendar = (startCalendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, 6)
                }
                datePickerDialog.datePicker.minDate = startCalendar.timeInMillis
                datePickerDialog.datePicker.maxDate = maxEndCalendar.timeInMillis
            }
            "BIWEEKLY" -> {
                val maxEndCalendar = (startCalendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, 6)
                }
                datePickerDialog.datePicker.minDate = startCalendar.timeInMillis
                datePickerDialog.datePicker.maxDate = maxEndCalendar.timeInMillis
            }
            "MONTHLY" -> {
                val maxEndCalendar = (startCalendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, 29)
                }
                datePickerDialog.datePicker.minDate = startCalendar.timeInMillis
                datePickerDialog.datePicker.maxDate = maxEndCalendar.timeInMillis
            }
        }
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
        dialog.start{
            dismiss()
        }

    }

    private fun updateSaveButtonState() {
        val isTitleValid = eventTitleEditText.text.isNotEmpty()
        val isPositionValid = eventPositionEditText.text.isNotEmpty()

        // 모든 조건이 만족되는 경우에만 saveButton 활성화
        saveButton.isEnabled = isTitleValid && isPositionValid && isStartDateSet  && isEndDateSet || isTitleValid && isPositionValid && checkBox.isChecked

    }

    private fun clearFocusFromEditTexts() {
        // 현재 포커스를 가진 View의 포커스를 제거
        view?.let {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }

        // 모든 EditText의 포커스를 제거
        eventTitleEditText.clearFocus()
        eventPositionEditText.clearFocus()
    }

    private fun resetDateTimeFields() {
        // 시작 날짜 및 시간 초기화
        startDateTime = ""
        startYearTx.text = ""
        startTimeTx.text = ""

        // 종료 날짜 및 시간 초기화
        endDateTime = ""
        endYearTx.text = ""
        endTimeTx.text = ""

        // 시작/종료 날짜 및 시간 관련 View 숨기기
        startYearTx.visibility = View.GONE
        startTimeTx.visibility = View.GONE
        endYearTx.visibility = View.GONE
        endTimeTx.visibility = View.GONE

        isStartDateSet = false // 초기화
        isEndDateSet = false   // 초기화
        updateSaveButtonState()
    }

    private fun resetEndDateTimeFields() {
        // 종료 날짜 및 시간 초기화
        endDateTime = ""
        endYearTx.text = ""
        endTimeTx.text = ""

        // 종료 날짜 및 시간 관련 View 숨기기
        endYearTx.visibility = View.GONE
        endTimeTx.visibility = View.GONE

        isEndDateSet = false // 종료 날짜 설정 초기화
        updateSaveButtonState()
    }
}