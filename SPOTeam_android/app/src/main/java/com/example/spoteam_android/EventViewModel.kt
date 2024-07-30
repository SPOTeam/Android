package com.example.spoteam_android

import android.util.Log
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
        Log.d("EventViewModel", "Event added: ${event.title}, Date: ${event.year}-${event.month}-${event.day}")
        loadEvents(event.year, event.month, event.day)
    }

    fun loadEvents(year: Int, month: Int, day: Int) {
        val loadedEvents = EventRepository.getEventsByDate(year, month, day)
        Log.d("EventViewModel", "Events loaded: ${loadedEvents.size} events for date $year-$month-$day")
        _events.value = loadedEvents
    }
}
