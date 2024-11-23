package com.example.spoteam_android.ui.study.todolist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class TodoViewModel(private val repository: TodoRepository, private val studyId: Int) : ViewModel() {

    private val _myTodos = MutableLiveData<List<String>>()
    val myTodos: LiveData<List<String>> get() = _myTodos

    private val _addTodoResponse = MutableLiveData<TodolistResponse?>()
    val addTodoResponse: LiveData<TodolistResponse?> get() = _addTodoResponse


    private val _myTodoListResponse = MutableLiveData<TodolistResponse?>()
    val myTodoListResponse: LiveData<TodolistResponse?> get() = _myTodoListResponse

    private val _otherTodoListResponse = MutableLiveData<TodolistResponse?>()
    val otherTodoListResponse: LiveData<TodolistResponse?> get() = _otherTodoListResponse

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
                _myTodoListResponse.postValue(null) // Update LiveData on failure or empty response

            } else {
                _myTodoListResponse.postValue(response)
            }
        }
    }

    fun fetchOtherToDoList(studyId: Int, memberId: Int, page: Int, size: Int, date: String) {

        Log.d("TodoViewModel", "$studyId, $memberId,$page, $size, $date ")
        repository.lookOtherTodo(studyId, memberId, page, size, date) { response ->
            if (response == null) {
                _otherTodoListResponse.postValue(null)// Update LiveData on failure or empty response
                Log.d("TodoViewModel","response가 null이에용")
            } else {
                _otherTodoListResponse.postValue(response)
                Log.d("TodoViewModel","$response")
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

    fun checkTodo(studyId: Int, toDoId: Int) {
        Log.d("TodoViewModel", "체크 요청: studyId=$studyId, toDoId=$toDoId")
        repository.checkTodo(studyId, toDoId) { response ->
            if (response != null && response.isSuccess) {
                Log.d("TodoViewModel", "체크 상태 변경 성공")
                // 변경된 데이터를 다시 가져오기
                fetchTodoList(studyId, page = 0, size = 10, date = _selectedDate.value ?: todayDate)
            } else {
                Log.e("TodoViewModel", "체크 상태 변경 실패")
            }
        }
    }



}
