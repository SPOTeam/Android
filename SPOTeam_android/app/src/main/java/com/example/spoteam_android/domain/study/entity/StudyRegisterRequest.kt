package com.example.spoteam_android.domain.study.entity


data class StudyRegisterRequest(
    val themes: List<String>,
    val title: String,
    val goal: String,
    val introduction: String,
    val isOnline: Boolean,
    val profileImage: String? = null,
    val regions: List<String>? = null,
    val maxPeople: Int,
    val gender: String,
    val minAge: Int,
    val maxAge: Int,
    val fee: Int,
    val hasFee: Boolean? = null
)