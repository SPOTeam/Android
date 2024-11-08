package com.example.spoteam_android.todolist
import com.google.gson.annotations.SerializedName


data class TodolistResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ResultData
)

data class ResultData(
    @SerializedName("id") val studyId: Int,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
)