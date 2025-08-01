package com.umcspot.android.ui.study.calendar

import java.util.Calendar

data class Event(
    val id: Int,
    val title: String,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val startHour: Int,
    val startMinute: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val endHour: Int,
    val endMinute: Int,
    val period: String = "NONE",
    val isAllDay: Boolean = false
) {

    // 시작 시간 AM/PM 형식으로 반환
    val startDateTime: String
        get() = formatTime(startYear, startMonth, startDay, startHour, startMinute)

    // 종료 시간 AM/PM 형식으로 반환
    val endDateTime: String
        get() = formatTime(endYear, endMonth, endDay, endHour, endMinute)

    // 시간 포맷팅 함수 (AM/PM 형식)
    private fun formatTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, hour, minute) // month는 0부터 시작하므로 -1
        }

        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm"
        val hourFormatted = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it } // 12시간제 처리

        return String.format("%02d:%02d%s", hourFormatted, minute, amPm)
    }

}
