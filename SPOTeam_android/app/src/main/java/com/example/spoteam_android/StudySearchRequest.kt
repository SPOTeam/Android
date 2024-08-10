package com.example.spoteam_android

import com.google.gson.annotations.SerializedName

data class StudySearchRequest(
    @SerializedName("themes")
    val themes: List<String>,

    @SerializedName("regions")
    val regions: List<String>? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("studyState")
    val studyState: String? = null
)
