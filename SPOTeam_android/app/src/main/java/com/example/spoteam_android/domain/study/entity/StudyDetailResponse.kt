package com.example.spoteam_android.domain.study.entity

data class StudyDetailResponse(
    val studyId: Int,
    val studyName: String,
    val ownerId: Int,
    val ownerName: String,
    val hitNum: Int,
    val heartCount: Int,
    val memberCount: Int,
    val maxPeople: Int,
    val gender: String,
    val minAge: Int,
    val maxAge: Int,
    val fee: Int,
    val isOnline: Boolean,
    val profileImage: String,
    val themes: List<String>,
    val regions: List<String>,
    val goal: String,
    val introduction: String
)
