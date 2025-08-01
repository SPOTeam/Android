package com.spot.android.ui.study.calendar

import android.content.Context
import com.spot.android.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CustomRectangleDecorator(
    private val context: Context,
    private var selectedDate: CalendarDay? = null
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == selectedDate
    }

    override fun decorate(view: DayViewFacade) {
        // 사각형 Drawable을 설정
        view.setBackgroundDrawable(context.getDrawable(R.drawable.custom_rectangle_drawable)!!)
    }

    fun setSelectedDate(date: CalendarDay?) {
        selectedDate = date
    }
}

