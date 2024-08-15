package com.example.spoteam_android.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class TodoViewModel : ViewModel() {
    private val _myTodos = MutableLiveData<List<String>>()
    val myTodos: LiveData<List<String>> get() = _myTodos

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    init {
        // ViewModel 초기화 시 오늘 날짜를 설정
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        _selectedDate.value = "$day"
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun loadTodosForDate(year: Int, month: Int, day: Int) {
        // 여기서 날짜에 따라 투두 리스트를 로드합니다. 아래는 예시 데이터입니다.
        _myTodos.value = listOf("할 일 1", "할 일 2", "할 일 3") // 예시 데이터
    }


}