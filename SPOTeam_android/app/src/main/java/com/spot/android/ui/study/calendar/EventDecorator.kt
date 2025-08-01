package com.spot.android.ui.study.calendar

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(
    private val eventDates: Set<CalendarDay> // 이벤트가 있는 날짜들
) : DayViewDecorator {

    // 이벤트가 있는 날짜에만 장식을 적용
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return eventDates.contains(day)
    }

    // 점 추가
    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(7f, Color.parseColor("#3E83FF")))
    }
}
