package com.example.spoteam_android.todolist

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoRepository(private val apiService: TodoApiService) {

    fun addTodoItem(studyId: Int, content: String, date: String, callback: (TodolistResponse?) -> Unit) {
        Log.d("TodoRepository","$studyId")
        val request = TodoRequest(content, date)
        apiService.addTodo(studyId, request).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    Log.e("TodoRepository", "Failed to add todo: ${response.code()} - ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                Log.e("TodoRepository", "Error creating Todo", t)
                callback(null)
            }
        })
    }
}
