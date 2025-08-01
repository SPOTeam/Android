package com.umcspot.android.ui.study.todolist

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoApiService {
    @POST("/spot/studies/{studyId}/to-do")
    fun addTodo(
        @Path("studyId") studyId: Int,          // URL 경로에 studyId 전달
        @Body request: TodoRequest               // 요청 본문에 TodoRequest 객체 전달
    ): Call<CreateTodoResponse>

    @GET("/spot/studies/{studyId}/to-do/my")
    fun lookTodo(
        @Path("studyId") studyId: Int,          // URL 경로에 studyId 전달
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("date") date: String
    ): Call<TodolistResponse>

    @POST("/spot/studies/{studyId}/to-do/{toDoId}/check")
    fun checkTodo(
        @Path("studyId") studyId: Int,          // URL 경로에 studyId 전달
        @Path("toDoId") toDoId: Int
    ): Call<TodolistResponse>

    @POST("/spot/studies/{studyId}/to-do/{toDoId}/update")
    fun updateTodo(
        @Path("studyId") studyId: Int,
        @Path("toDoId") toDoId: Int,
        @Body request: TodoRequest
    ): Call<TodolistResponse>

    @GET(" /spot/studies/{studyId}/to-do/members/{memberId}")
    fun lookOtherTodo(
        @Path("studyId") studyId: Int,
        @Path("memberId") memberId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("date") date: String
    ): Call<TodolistResponse>

    @DELETE("/spot/studies/{studyId}/to-do/{toDoId}")
    fun deleteTodo(
        @Path("studyId") studyId: Int,
        @Path("toDoId") toDoId: Int
    ): Call<TodolistResponse>
}
