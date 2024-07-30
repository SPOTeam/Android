package com.example.spoteam_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    init {
        _events.value = emptyList()
    }

    fun addEvent(event: Event) {
        EventRepository.addEvent(event)
        loadEvents(event.startYear, event.startMonth, event.startDay)
    }

    fun loadEvents(year: Int, month: Int, day: Int) {
        val loadedEvents = EventRepository.getEventsByDate(year, month, day)
        _events.value = loadedEvents
    }
}
