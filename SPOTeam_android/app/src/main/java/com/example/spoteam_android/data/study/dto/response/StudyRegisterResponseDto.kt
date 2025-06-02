package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StudyRegisterResponseDto(
    @SerializedName("studyId")
    val studyId: Int,
    @SerializedName("title")
    val title: String
)