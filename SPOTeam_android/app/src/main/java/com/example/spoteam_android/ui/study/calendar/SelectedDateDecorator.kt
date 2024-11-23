package com.example.spoteam_android.ui.study.calendar

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class SelectedDateDecorator(
    private val context: Context,
    private var selectedDate: CalendarDay? = null // 선택된 날짜 추적
) : DayViewDecorator {

    private val drawable: Drawable = ColorDrawable(Color.RED) // 점의 색상 설정
    private val whiteColor = context.getColor(android.R.color.white) // 흰색

    override fun shouldDecorate(day: CalendarDay): Boolean {
        // 오늘 날짜가 아닌 선택된 날짜만 장식 적용
        return selectedDate != null && day == selectedDate && day != CalendarDay.today()
    }

    override fun decorate(view: DayViewFacade) {
        // 선택된 날짜는 흰색으로 표시
        view.addSpan(ForegroundColorSpan(whiteColor))
    }

    // 선택된 날짜 업데이트
    fun setSelectedDate(date: CalendarDay?) {
        selectedDate = date
    }
}

