package com.spot.android.ui.study.calendar

object EventRepository {
    private val events = mutableListOf<Event>()
    private var nextId = 1

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun getNextId(): Int {
        return nextId++
    }
}