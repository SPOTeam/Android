package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class IsAppliedResponseDto(
    @SerializedName("studyId")
    val studyId: Int,
    @SerializedName("applied")
    val applied: Boolean
)