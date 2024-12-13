package com.example.spoteam_android.ui.study.todolist

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoRepository(private val apiService: TodoApiService) {

    fun addTodoItem(studyId: Int, content: String, date: String, callback: (TodolistResponse?) -> Unit) {
        val request = TodoRequest(content, date)
        apiService.addTodo(studyId, request).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {

                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    logError(response)
                    callback(null)
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun lookTodo(studyId: Int, page: Int, size: Int, date: String, callback: (TodolistResponse?) -> Unit) {
        val formattedDate = formatToDate(date)  // 날짜 형식을 보장

        apiService.lookTodo(studyId, page, size, formattedDate).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.result != null && responseBody.result.content.isNotEmpty()) {
                        callback(responseBody)
                    } else {
                        callback(null)
                    }
                } else {
                    logError(response)
                    callback(null)
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun lookOtherTodo(studyId: Int, memberId: Int, page: Int, size: Int, date: String, callback: (TodolistResponse?) -> Unit) {
        val formattedDate = formatToDate(date)  // 날짜 형식을 보장

        apiService.lookOtherTodo(studyId, memberId, page, size, formattedDate).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.result != null && responseBody.result.content.isNotEmpty()) {
                        callback(responseBody)
                    } else {
                        callback(null)
                    }
                } else {
                    logError(response)
                    callback(null)
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            date
        }
    }


    fun checkTodo(studyId: Int, toDoId: Int, callback: (TodolistResponse?) -> Unit) {
        apiService.checkTodo(studyId, toDoId).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null) // 실패한 경우 null 반환
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                Log.e("TodoRepository", "체크 API 호출 실패: ${t.message}")
                callback(null)
            }
        })
    }


    // Error logging helper
    private fun logError(response: Response<TodolistResponse>) {
        val errorCode = response.code()
        val errorBody = response.errorBody()?.string()
    }
}