package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StudyDetailResponseDto(
    @SerializedName("studyId")
    val studyId: Int,
    @SerializedName("studyName")
    val studyName: String,
    @SerializedName("studyOwner")
    val studyOwner: StudyOwnerDataResponseDto,
    @SerializedName("hitNum")
    val hitNum: Int,
    @SerializedName("heartCount")
    val heartCount: Int,
    @SerializedName("memberCount")
    val memberCount: Int,
    @SerializedName("maxPeople")
    val maxPeople: Int,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("minAge")
    val minAge: Int,
    @SerializedName("maxAge")
    val maxAge: Int,
    @SerializedName("fee")
    val fee: Int,
    @SerializedName("isOnline")
    val isOnline: Boolean,
    @SerializedName("profileImage")
    val profileImage: String,
    @SerializedName("themes")
    val themes: List<String>,
    @SerializedName("regions")
    val regions: List<String>,
    @SerializedName("goal")
    val goal: String,
    @SerializedName("introduction")
    val introduction: String,
) {
    @Serializable
    data class StudyOwnerDataResponseDto(
        val ownerId: Int,
        val ownerName: String
    )
}

