package com.example.spoteam_android.ui.calendar

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spoteam_android.R

class EventAdapter(
    private var events: List<Event>,
    private val onCheckClick: (Event) -> Unit,
    private val isTodoList: Boolean,
    private var selectedDate: String = "" // 추가된 필드: 선택된 날짜 정보
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
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
        private val icCheck: ImageView = itemView.findViewById(R.id.ic_check)

        @SuppressLint("DefaultLocale")
        fun bind(event: Event, selectedDate: String) {
            eventTitleTextView.text = event.title

            if (selectedDate.isNotEmpty()) {
                Log.d("EventViewHolder", "현재 선택된 날짜: $selectedDate")
            }

            // 이벤트의 날짜 정보를 yyyy-MM-dd 형식으로 변환
            val eventStartDate = String.format("%04d-%02d-%02d", event.startYear, event.startMonth, event.startDay)
            val eventEndDate = String.format("%04d-%02d-%02d", event.endYear, event.endMonth, event.endDay)

            val eventTimeText = when (event.period) {
                "NONE" -> {
                    if (isSingleDayEvent(event)) {
                        "${event.startDateTime} ~ ${event.endDateTime}"
                    } else {
                        when {
                            selectedDate == eventStartDate -> {
                                Log.d("EventViewHolder", "Event 시작 날짜: $eventStartDate, 선택 날짜: $selectedDate")
                                "${event.startDateTime} 시작"
                            }
                            selectedDate == eventEndDate -> {
                                Log.d("EventViewHolder", "Event 종료 날짜: $eventEndDate, 선택 날짜: $selectedDate")
                                "${event.endDateTime} 종료"
                            }
                            selectedDate > eventStartDate && selectedDate < eventEndDate -> {
                                Log.d("EventViewHolder", "선택 날짜가 이벤트 중간에 해당: $selectedDate")
                                "하루종일"
                            }
                            else -> {
                                Log.d("EventViewHolder", "선택 날짜가 이벤트와 관련 없음: $selectedDate")
                                ""
                            }
                        }
                    }
                }
                "DAILY", "WEEKLY", "BIWEEKLY", "MONTHLY" -> {
                    Log.d("EventViewHolder", "반복 이벤트: ${event.period}, ${event.startDateTime} ~ ${event.endDateTime}")
                    "${event.startDateTime} ~ ${event.endDateTime}"
                }
                else -> {
                    Log.d("EventViewHolder", "기타 이벤트: ${event.period}, ${event.startDateTime} ~ ${event.endDateTime}")
                    "${event.startDateTime} ~ ${event.endDateTime}"
                }
            }

            if (event.isAllDay) {
                Log.d("EventViewHolder", "하루종일 이벤트: $event")
                eventTimeTextView.text = "하루종일"
            } else {
                eventTimeTextView.text = eventTimeText
            }

            // 최종 텍스트 로그 출력
            Log.d("EventViewHolder", "Event 시간 텍스트: $eventTimeText, 전체 텍스트: ${eventTimeTextView.text}")

        // 체크 아이콘 visibility 설정
            icCheck.visibility = if (isTodoList) View.GONE else View.VISIBLE

            // 클릭 이벤트 설정
            icCheck.setOnClickListener {
                onCheckClick(event)
            }
        }

        // 하루 일정인지 확인하는 함수
        private fun isSingleDayEvent(event: Event): Boolean {
            return event.startYear == event.endYear &&
                    event.startMonth == event.endMonth &&
                    event.startDay == event.endDay
        }
    }
}
