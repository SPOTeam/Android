package com.spot.android.ui.study.calendar

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.spot.android.R
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter
import org.threeten.bp.DayOfWeek

class CustomWeekDayFormatter(private val context: Context) : WeekDayFormatter {

    override fun format(dayOfWeek: DayOfWeek): CharSequence {
        val weekDayText = when (dayOfWeek) {
            DayOfWeek.SUNDAY -> "일"
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
        }

        return if (dayOfWeek == DayOfWeek.SUNDAY) {
            // 일요일은 res/color/b500.xml의 색상 적용
            SpannableString(weekDayText).apply {
                setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.b500)),
                    0, length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            // 나머지는 기본 색상
            weekDayText
        }
    }
}
