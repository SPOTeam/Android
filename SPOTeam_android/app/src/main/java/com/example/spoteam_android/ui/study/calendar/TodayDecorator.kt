package com.example.spoteam_android.ui.study.calendar

import android.content.Context
import android.graphics.Typeface
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.example.spoteam_android.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TodayDecorator(
    private val context: Context,
    private var selectedDate: CalendarDay? = null // 선택된 날짜 추적
) : DayViewDecorator {

    private val today = CalendarDay.today() // 오늘 날짜
    private val activeBlueColor = context.getColor(R.color.active_blue) // 파란색
    private val whiteColor = context.getColor(android.R.color.white) // 흰색

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 오늘 날짜일 때만 장식 적용
        return day == today
    }

    override fun decorate(view: DayViewFacade) {
//        if (selectedDate == today) {
//            // 오늘 날짜가 선택된 경우 흰색으로 표시
//            view.addSpan(ForegroundColorSpan(whiteColor))
//        } else {
//            // 오늘 날짜가 선택되지 않았을 경우 파란색으로 표시
//            view.addSpan(ForegroundColorSpan(activeBlueColor))
//        }
        view.addSpan(StyleSpan(Typeface.BOLD)) // 굵게 표시
    }

    // 선택된 날짜 업데이트 메서드
    fun setSelectedDate(date: CalendarDay?) {
        selectedDate = date
    }


}
