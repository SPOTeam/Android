package com.example.spoteam_android

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SceduleItem (
    val dday: String,
    val day: String,
    val scheduleContent: String,
    val concreteTime: String,
    val place: String,
)
@Serializable
data class ProfileItem(
    val profileImage: String,
    val nickname: String
)
@Serializable
data class ProfileTemperatureItem(
    val profileImage: Int,
    val nickname: String,
    val temperature: String
)


@Serializable
data class ThemeResult(
    val memberId: Int,
    val themes: List<String>
)

@Serializable
data class Region(
    val province: String,
    val district: String,
    val neighborhood: String,
    val code: String
)








data class RegionData(
    val category: String,              // 구분
    val administrativeCode: String,    // 행정구역코드
    val level1: String?,               // 단계1
    val level2: String?,               // 단계2
    val level3: String?,               // 단계3
    val gridX: Int,                    // 격자 X
    val gridY: Int,                    // 격자 Y
    val longitudeHour: Int,            // 경도(시)
    val longitudeMinute: Int,          // 경도(분)
    val longitudeSecond: Double,       // 경도(초)
    val latitudeHour: Int,             // 위도(시)
    val latitudeMinute: Int,           // 위도(분)
    val latitudeSecond: Double,        // 위도(초)
    val longitudeSecond100: Double,    // 경도(초/100)
    val latitudeSecond100: Double,     // 위도(초/100)
    val locationUpdate: String?        // 위치업데이트
)

@Serializable
data class attendanceMemberData(
    val memberId : Int,
    var nickname: String,
    val profileImage: String,
    var introduction : String
)
@Serializable
data class HostWithDrawl(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: WithDrawlData
)
@Serializable
data class WithDrawlData(
    val studyId: Int,
    val studyName: String,
    val memberId: Int,
    val memberName: String
)
@Serializable
data class WithdrawHostRequest(
    val newHostId: Int,
    val reason: String
)

data class FinishedStudyResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: FinishedStudyResult
)

data class FinishedStudyResult(
    val studyHistories: StudyHistories
)

data class StudyHistories(
    val totalPages: Int,
    val totalElements: Int,
    val size: Int,
    val content: List<FinishedStudyItem>,
    val number: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

data class FinishedStudyItem(
    val studyId: Int,
    val title: String,
    val performance: String?,
    val createdAt: String,
    val finishedAt: String?
)
data class HostApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: HostResult
)

data class HostResult(
    val isOwned: Boolean,
    val host: HostData
)

data class HostData(
    val memberId: Int,
    val nickname: String
)
