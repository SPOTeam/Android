package com.example.spoteam_android.domain.study.entity


data class StudyDataResponse(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val content: List<StudyContent>
) {
    data class StudyContent(
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
        val createdAt: String
    )
}

