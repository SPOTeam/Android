
package com.example.spoteam_android.ui.calendar

import java.util.*

object EventRepository {
    private val events = mutableListOf<Event>()
    private var nextId = 1

    fun addEvent(event: Event) {
        events.add(event)
    }
        fun getEventsByDate(year: Int, month: Int, day: Int): List<Event> {
            val startOfDay = Calendar.getInstance().apply {
                set(year, month - 1, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val endOfDay = Calendar.getInstance().apply {
                set(year, month - 1, day, 23, 59, 59)
                set(Calendar.MILLISECOND, 999)
            }

            return events.filter { event ->
                val startDate = Calendar.getInstance().apply {
                    set(event.startYear, event.startMonth - 1, event.startDay, event.startHour, event.startMinute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val endDate = Calendar.getInstance().apply {
                    set(event.endYear, event.endMonth - 1, event.endDay, event.endHour, event.endMinute, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                endDate.timeInMillis >= startOfDay.timeInMillis && startDate.timeInMillis <= endOfDay.timeInMillis
            }
        }


    fun getNextId(): Int {
        return nextId++
    }
}
