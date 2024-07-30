package com.example.spoteam_android

import android.util.Log

object EventRepository {
    private val events = mutableListOf<Event>()
    private var nextId = 1

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun getEventsByDate(year: Int, month: Int, day: Int): List<Event> {
        return events.filter {
            Log.d("EventRepository", "Event: ${it.title}, Date: ${it.year}-${it.month}-${it.day}")
            it.year == year && it.month == month && it.day == day
        }
    }

    fun getNextId(): Int {
        return nextId++
    }
}
