package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyDataResponseDto(
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("first")
    val first: Boolean,
    @SerialName("last")
    val last: Boolean,
    @SerialName("size")
    val size: Int,
    @SerialName("content")
    val content: List<StudyContentResponseDto>,
    @SerialName("pageNumber")
    val pageNumber: Int
){
    @Serializable
    data class StudyContentResponseDto(
        @SerializedName("studyId")
        val studyId: Int,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("introduction")
        val introduction: String,
        @SerializedName("goal")
        val goal: String,
        @SerializedName("memberCount")
        val memberCount: Int,
        @SerializedName("heartCount")
        val heartCount: Int,
        @SerializedName("hitNum")
        val hitNum: Int,
        @SerializedName("maxPeople")
        val maxPeople: Int,
        @SerializedName("studyState")
        val studyState: String,
        @SerializedName("themeTypes")
        val themeTypes: List<String>,
        @SerializedName("regions")
        val regions: List<String>,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("liked")
        val liked: Boolean
    )
}