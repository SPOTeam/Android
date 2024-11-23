package com.example.spoteam_android.ui.study.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TodoViewModelFactory(private val repository: TodoRepository, private val studyId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository, studyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
