package com.example.spoteam_android

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class IndexData(
    val index : String,
    val content : String,
    val commentNum : String
)

data class CategoryData(
    val category : String,
    val content : String,
    val commentNum : String
)

data class CommunityData(
    val title : String,
    val content : String,
    val likeNum : String,
    val commentNum : String,
    val viewNum : String,
    val bookmarkNum : String,
    val writer : String,
    val date : String
)


data class BoardItem (
    val studyName : String,
    val studyObject : String,
    val studyTO : Int,
    val studyPO : Int,
    val like: Int,
    val watch : Int
)



data class StudyItem( //StudyFragment에서 사용
    val studyId: Int,
    val title: String,
    val goal: String,
    val introduction: String,
    val memberCount: Int,
    val heartCount: Int,
    val hitNum: Int,
    val maxPeople: Int,
    val studyState: String,
    val themeTypes: List<String>,
    val regions: List<String>,
    val imageUrl: String
)

data class StudyDetailsResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: StudyDetailsResult
)

data class StudyDetailsResult( //StudyFragment에서 detail하게 들어갔을때 사용
    val studyId: Int,
    val studyName: String,
    val studyOwner: Owner,
    val hitNum: Int,
    val heartCount: Int,
    val memberCount: Int,
    val maxPeople: Int,
    val gender: String,
    val minAge: Int,
    val maxAge: Int,
    val fee: Int,
    val isOnline: Boolean,
    val themes: List<String>,
    val goal: String,
    val introduction: String
)

data class Owner(
    val ownerId: Int,
    val ownerName: String
)


data class MemberResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: MemberResult
)

data class MemberResult(
    val totalElements: Int,
    val members: List<Member>
)

data class Member(
    val memberId: Int,
    val nickname: String,
    val profileImage: String
)

data class StudyResponse(
    val result: StudyResult
)

data class StudyResult(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: List<StudyData>,
    val number: Int
)



data class StudyData(
    val studyId: Int,
    val goal: String,
    val imageUrl: String,
    val title: String,
    val introduction: String,
    val memberCount: Int,
    val heartCount: Int,
    val hitNum: Int,
    val maxPeople: Int,
    val studyState: String,
    val themeTypes: List<String>,
    val regions: List<String>,
    val createdAt: String,
    val liked: Boolean
)


data class SceduleItem (
    val dday: String,
    val day: String,
    val scheduleContent: String,
    val concreteTime: String,
    val place: String,
)

data class ProfileItem(
    val profileImage: String,
    val nickname: String
)

data class GalleryItem (
    val imgId : Int
)

data class ProfileTemperatureItem(
    val profileImage: Int,
    val nickname: String,
    val temperature: String
)

data class AlertInfo(
    val contentText : String,
    val type : Int
)


data class StudyInfo(
    val studyName : String
)


data class CommentInfo(
    val commentWriter : String,
    val Comment : String,
    val commentType : Int
)

data class ThemeApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ThemeResult
)

data class ThemeResult(
    val memberId: Int,
    val themes: List<String>
)


data class ThemePreferences(
    @SerializedName("themes")
    val themes: List<String>
)

data class ReasonApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ReasonResult
)

data class ReasonResult(
    val memberId: Int,
    val reasons: List<String>
)

data class RegionApiResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RegionResult
)

data class RegionResult(
    val memberId: Int,
    val regions: List<Region>
)

data class Region(
    val province: String,
    val district: String,
    val neighborhood: String,
    val code: String
)


data class StudyReasons(
    @SerializedName("reasons")
    val reasons: List<Int>
)

data class RegionsPreferences(
    @SerializedName("regions")
    val regions: List<String>
)


//일정 생성
data class ScheduleRequest(
    val title: String,
    val location: String,
    val startedAt: String,
    val finishedAt: String,
    val isAllDay: Boolean,
    val period: String
)

data class ScheduleResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ScheduleResult
)

data class ScheduleResult(
    val title: String,
    val location: String,
    val startedAt: String,
    val finishedAt: String,
    val isAllDay: Boolean,
    val period: String
)


//일정 불러오기
data class ScheduleListResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: ScheduleListResult
)

data class ScheduleListResult(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val schedules: List<ScheduleItem>
)

data class ScheduleItem(
    val staredAt: String,
    val title: String,
    val location: String
)

//최근 공지 불러오기
data class RecentAnnounceResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: RecentAnnounceResult
)

data class RecentAnnounceResult(
    val title: String,
    val content: String
)







