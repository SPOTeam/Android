package com.example.spoteam_android.data.study.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StudyRegisterRequestDto(
    @SerializedName("themes")
    val themes: List<String>,

    @SerializedName("title")
    val title: String,

    @SerializedName("goal")
    val goal: String,

    @SerializedName("introduction")
    val introduction: String,

    @SerializedName("isOnline")
    val isOnline: Boolean,

    @SerializedName("profileImage")
    val profileImage: String? = null,

    @SerializedName("regions")
    val regions: List<String>? = null,

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

    @SerializedName("hasFee")
    val hasFee: Boolean? = null
)