package com.spot.android.ui.study.todolist

import com.spot.android.ui.study.calendar.Event

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spot.android.R
import java.time.LocalDate

class TodoEventAdapter(
    private var events: List<Event>,
    private val onCheckClick: (Event) -> Unit,
    private var isTodoList: Boolean,
    private var selectedDate: String = "" // 추가된 필드: 선택된 날짜 정보
) : RecyclerView.Adapter<TodoEventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo_event_list, parent, false)
        return EventViewHolder(view, onCheckClick, isTodoList)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event, selectedDate)

    }

    override fun getItemCount(): Int = events.size

    fun updateSelectedDate(newSelectedDate: String) {
        selectedDate = newSelectedDate
        notifyDataSetChanged() // 선택된 날짜가 변경되면 어댑터 갱신
    }

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }


    class EventViewHolder(
        itemView: View,
        private val onCheckClick: (Event) -> Unit,
        private val isTodoList: Boolean
    ) : RecyclerView.ViewHolder(itemView) {
        private val eventTitleTextView: TextView = itemView.findViewById(R.id.eventTitleTextView)
        private val eventTimeTextView: TextView = itemView.findViewById(R.id.eventTimeTextView)

        @SuppressLint("DefaultLocale")
        fun bind(event: Event, selectedDate: String) {
            eventTitleTextView.text = event.title

            if (selectedDate.isNotEmpty()) {
            }

            // 이벤트의 날짜 정보를 yyyy-MM-dd 형식으로 변환
            val eventStartDate = String.format("%04d-%02d-%02d", event.startYear, event.startMonth, event.startDay)
            val eventEndDate = String.format("%04d-%02d-%02d", event.endYear, event.endMonth, event.endDay)

            val eventTimeText = when (event.period) {
                "NONE" -> {
                    if (isSingleDayEvent(event)) {
                        "${event.startDateTime} - ${event.endDateTime}"
                    } else {
                        when {
                            selectedDate == eventStartDate -> {
                                "${event.startDateTime} - 하루종일"
                            }
                            selectedDate == eventEndDate -> {
                                "- ${event.endDateTime} 까지"
                            }
                            selectedDate > eventStartDate && selectedDate < eventEndDate -> {
                                "하루종일"
                            }
                            else -> {
                                ""
                            }
                        }
                    }
                }
                "DAILY", "WEEKLY", "BIWEEKLY", "MONTHLY" -> {
                    "${event.startDateTime} - ${event.endDateTime}"
                }
                else -> {
                    "${event.startDateTime} - ${event.endDateTime}"
                }
            }

            if (event.isAllDay) {
                eventTimeTextView.text = "하루종일"
            } else {
                eventTimeTextView.text = eventTimeText
            }

        }

        // 하루 일정인지 확인하는 함수
        private fun isSingleDayEvent(event: Event): Boolean {
            return event.startYear == event.endYear &&
                    event.startMonth == event.endMonth &&
                    event.startDay == event.endDay
        }
    }

    fun hasEventOnDay(day: Int): Boolean {
        return try {
            val selected = LocalDate.parse(selectedDate)
            val year = selected.year
            val month = selected.monthValue

            events.any { event ->
                val startDate = LocalDate.of(event.startYear, event.startMonth, event.startDay)
                val endDate = LocalDate.of(event.endYear, event.endMonth, event.endDay)
                val currentDate = LocalDate.of(year, month, day)
                !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)
            }
        } catch (e: Exception) {
            false
        }
    }


}
