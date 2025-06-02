package com.example.spoteam_android.domain.study.entity


data class StudyMemberResponse(
    val totalCount: Int,
    val members: List<StudyMember>
) {
    data class StudyMember(
        val memberId: Int,
        val nickname: String,
        val profileImage: String
    )
}
