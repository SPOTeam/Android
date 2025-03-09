package com.example.spoteam_android.ui.study.todolist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class TodoDateAdapter(
    private var dates: List<Triple<Int?, Boolean, Boolean>>, // (날짜, 현재 달 여부, 클릭 가능 여부)
    private var selectedDate: Int,
    private val onDateSelected: (Int) -> Unit
) : RecyclerView.Adapter<TodoDateAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val (date, isCurrentMonth, isClickable) = dates[position]

        // ✅ 정확한 날짜를 직접 `date` 값을 활용해서 계산하도록 수정
        val localDate = if (date != null) {
            LocalDate.of(LocalDate.now().year, LocalDate.now().month, date)
        } else {
            null
        }

        val dayOfWeek = localDate?.dayOfWeek?.getDisplayName(TextStyle.SHORT, Locale.KOREAN) ?: ""

        holder.bind(date, isCurrentMonth, isClickable, selectedDate, onDateSelected)
    }


    override fun getItemCount(): Int = dates.size

    fun updateDates(newDates: List<Triple<Int?, Boolean, Boolean>>, newSelectedDate: Int) {
        dates = newDates
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }

    fun getPositionForDate(day: Int): Int {
        return dates.indexOfFirst { it.first == day }
    }

    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayNumber: TextView = itemView.findViewById(R.id.tv_day_number)
        private val tvDayOfWeek: TextView = itemView.findViewById(R.id.tv_day_of_week)
        private val todayIndicator: View = itemView.findViewById(R.id.todayIndicator)

        fun bind(date: Int?, isCurrentMonth: Boolean, isClickable: Boolean, selectedDate: Int, onDateSelected: (Int) -> Unit) {
            val context = itemView.context
            val currentYear = LocalDate.now().year
            val currentMonth = LocalDate.now().monthValue

            if (date == null) {
                // 빈 칸 처리 (숫자 & 요일 숨김)
                tvDayNumber.text = ""
                tvDayOfWeek.text = ""
                todayIndicator.visibility = View.GONE
                itemView.isClickable = false
                itemView.isEnabled = false
                return
            }

            // 현재 날짜의 요일 가져오기
            val localDate = LocalDate.of(currentYear, currentMonth, 1).plusDays(adapterPosition.toLong())
            val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)


            tvDayNumber.text = date.toString()
            tvDayOfWeek.text = dayOfWeek

            if (isCurrentMonth) {
                // 현재 월이면 기본 색상 적용
                tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.black))
                tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.black))
                itemView.isClickable = true
                itemView.isEnabled = true

                // 선택된 날짜는 배경 변경
                if (date == selectedDate) {
                    tvDayNumber.setBackgroundResource(R.drawable.custom_rectangle_drawable)
                } else {
                    tvDayNumber.setBackgroundColor(Color.TRANSPARENT)
                }
            } else {
                // 이전/다음 달이면 회색 처리 및 클릭 비활성화
                tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.gray))
                tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.black))
                itemView.isClickable = false
                itemView.isEnabled = false
                tvDayNumber.setBackgroundColor(Color.TRANSPARENT) // 배경 없애기
            }

            val today = LocalDate.now().dayOfMonth

            // 오늘 날짜라면 파란 점 표시
            todayIndicator.visibility = if (date == today && isCurrentMonth) View.VISIBLE else View.GONE

            // 일요일이면 파란색 적용, 단 이전/다음 달일 경우 회색 적용
            if (localDate.dayOfWeek == DayOfWeek.SUNDAY) {
                if (isCurrentMonth) {
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.b400))
                    tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.b400))
                } else {
                    tvDayOfWeek.setTextColor(ContextCompat.getColor(context, R.color.black))
                    tvDayNumber.setTextColor(ContextCompat.getColor(context, R.color.gray))
                }
            }

            // 클릭 이벤트 설정 (이전/다음 달 클릭 불가능)
            itemView.setOnClickListener {
                if (isCurrentMonth) {
                    onDateSelected(date) // ✅ 선택한 날짜를 콜백으로 전달
                }
            }

        }
    }
}
