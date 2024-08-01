
package com.example.spoteam_android

import java.util.*

object EventRepository {
    private val events = mutableListOf<Event>()
    private var nextId = 1

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun getEventsByDate(year: Int, month: Int, day: Int): List<Event> {
        val targetDate = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
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

            targetDate.timeInMillis in startDate.timeInMillis..endDate.timeInMillis
        }
    }

    fun getNextId(): Int {
        return nextId++
    }
}
