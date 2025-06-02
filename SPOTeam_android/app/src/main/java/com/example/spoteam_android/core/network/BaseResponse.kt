package com.example.spoteam_android.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("isSuccess")
    val isSuccess: Boolean,
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("result")
    val result: T? = null
)
