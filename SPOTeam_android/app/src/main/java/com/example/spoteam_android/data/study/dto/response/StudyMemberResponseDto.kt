package com.example.spoteam_android.data.study.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudyMemberResponseDto(
    @SerialName("totalElements")
    val totalElements: Int,
    @SerialName("members")
    val members: List<StudyMemberDataResponse>
) {
    @Serializable
    data class StudyMemberDataResponse(
        @SerialName("memberId")
        val memberId: Int,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("profileImage")
        val profileImage: String
    )
}