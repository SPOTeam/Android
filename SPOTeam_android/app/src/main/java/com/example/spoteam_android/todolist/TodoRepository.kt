package com.example.spoteam_android.todolist

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodoRepository(private val apiService: TodoApiService) {

    fun addTodoItem(studyId: Int, content: String, date: String, callback: (TodolistResponse?) -> Unit) {
        val request = TodoRequest(content, date)
        apiService.addTodo(studyId, request).enqueue(object : Callback<TodolistResponse> {
            override fun onResponse(call: Call<TodolistResponse>, response: Response<TodolistResponse>) {
                Log.d("TodoRepository", "요청 Path - addTodo: ${call.request().url}")

                if (response.isSuccessful) {
                    Log.d("TodoRepository", "API 응답 성공 - addTodo: ${response.body()}")
                    callback(response.body())
                } else {
                    logError(response)
                    callback(null)
                }
            }

            override fun onFailure(call: Call<TodolistResponse>, t: Throwable) {
                Log.e("TodoRepository", "Error creating Todo", t)
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

    private fun formatToDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            String.format("%04d-%02d-%02d", parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        } else {
            date
        }
    }

    // Error logging helper
    private fun logError(response: Response<TodolistResponse>) {
        val errorCode = response.code()
        val errorBody = response.errorBody()?.string()
        Log.e("TodoRepository", "API 응답 실패: 코드=$errorCode, 메시지=$errorBody")
        Log.d("TodoRepository", "전체 응답: ${response.raw()}")
    }
}
