package com.example.spoteam_android.domain.study.entity

data class StudyApplyResponse(
    val memberId: Int,
    val study: StudyApplyInfo
) {
    data class StudyApplyInfo(
        val studyId: Int,
        val title: String
    )
}