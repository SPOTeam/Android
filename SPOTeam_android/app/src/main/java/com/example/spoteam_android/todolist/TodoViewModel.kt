package com.example.spoteam_android.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class TodoViewModel(private val repository: TodoRepository, private val studyId: Int) : ViewModel() {

    private val _myTodos = MutableLiveData<List<String>>()
    val myTodos: LiveData<List<String>> get() = _myTodos

    private val _addTodoResponse = MutableLiveData<TodolistResponse?>()
    val addTodoResponse: LiveData<TodolistResponse?> get() = _addTodoResponse

    private val _todoListResponse = MutableLiveData<TodolistResponse?>()
    val todoListResponse: LiveData<TodolistResponse?> get() = _todoListResponse

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val todayDate: String
        get() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
            return "$year-$month-$day"
        }

    init {
        // Initialize with today's date and fetch today's todo list
        _selectedDate.value = todayDate
        fetchTodoList(studyId = studyId, page = 0, size = 10, date = todayDate)
    }

    // Fetches the to-do list for a specific date and updates LiveData
    fun fetchTodoList(studyId: Int, page: Int, size: Int, date: String = todayDate) {
        repository.lookTodo(studyId, page, size, date) { response ->
            if (response == null) {
                _todoListResponse.postValue(null) // Update LiveData on failure or empty response
            } else {
                _todoListResponse.postValue(response)
            }
        }
    }

    // Updates the selected date and fetches data for the new date
    fun onDateChanged(newDate: String) {
        _selectedDate.value = newDate
        fetchTodoList(studyId = studyId, page = 0, size = 10, date = newDate)
    }

    // Adds a new to-do item
    fun addTodoItem(studyId: Int, content: String, date: String) {
        repository.addTodoItem(studyId, content, date) { response ->
            _addTodoResponse.postValue(response)
        }
    }
}
