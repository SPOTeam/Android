package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StudyLikeResponseDto(
    @SerializedName("studyTitle")
    val studyTitle: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("status")
    val status: String
)