package com.example.spoteam_android.domain.study.entity

data class BoardItem (
    val studyId: Int,
    val title: String,
    val goal: String,
    val introduction: String,
    val memberCount: Int,
    var heartCount: Int,
    val hitNum: Int,
    val maxPeople: Int,
    val studyState: String,
    val themeTypes: List<String>,
    val regions: List<String>,
    val imageUrl: String,
    var liked: Boolean,
    val isOwned: Boolean = false,
    val isHost : Boolean
)
