package com.example.spoteam_android.ui.study.todolist
import com.google.gson.annotations.SerializedName

data class TodolistResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultData // 수정된 result 타입
)

data class ResultData(
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("size") val size: Int,
    @SerializedName("content") val content: List<TodoTask>, // 새 클래스 이름 사용
    @SerializedName("number") val number: Int
)

data class TodoTask( // 이름을 TodoTask로 변경
    @SerializedName("id") val id: Int,
    @SerializedName("content") var content: String,
    @SerializedName("date") val date: String,
    @SerializedName("done") var done: Boolean
)