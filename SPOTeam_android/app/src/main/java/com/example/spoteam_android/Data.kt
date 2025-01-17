package com.example.spoteam_android

import android.os.Parcel
import android.os.Parcelable
import com.example.spoteam_android.data.ApiModels
import com.google.gson.annotations.SerializedName


//카카오 로그인 서버 응답
data class YourResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: KaKaoResult
)


// 카카오 로그인 서버 응답 결과 담기
data class KaKaoResult(
  val isSpotMember: Boolean,
    val signInDTO: SignInDTO
)
// 카카오 로그인 토큰 정보 담기
data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)


// 네이버 로그인 서버 응답 결과 담기
data class  NaverResult(
    val isSpotMember: Boolean,
    val signInDTO: SignInDTO
)

data class SignInDTO(
    val tokens: Tokens,
    val email: String,
    val memberId: Int,
    val loginType: String
)
//네이버 로그인 서버 응답
data class NaverResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: NaverResult?
)
data class NaverLoginRequest(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val expires_in: Long,
    val error: String? = null,
    val error_description: String? = null
)


data class UserInfo( //일반 로그인시 필요
    val name: String,
    val nickname: String = "TEMP_NICKNAME", // 임시 닉네임
    val frontRID: String,
    val backRID: String,
    val email: String,
    val loginId: String,
    val password: String,
    val pwCheck: String,
    val personalInfo: Boolean = false, // 임시 값
    val idInfo: Boolean = false        // 임시 값
)

data class IdResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: IdResult?,
)

data class IdResult(
    val reason: String,
    val available: Boolean,
)

data class EmailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: EmailResult?,
)

data class EmailResult(
    val reason: String,
    val available: Boolean
)

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


data class BoardItems (
    val studyId: Int,
    val studyName : String,
    val studyObject : String,
    val studyTO : Int,
    val studyPO : Int,
    val like: Int,
    val watch : Int
)


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
    var liked: Boolean
)


data class StudyItem( //StudyFragment에서 사용
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
    var liked: Boolean
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
    val isSuccess : Boolean,
    val code : String ,
    val message: String ,
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
    val imageUrl: String,
    val title: String,
    val introduction: String,
    val goal: String,
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


//스터디 신청
data class StudyApplyResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: StudyResponseResult
)

data class StudyResponseResult(
    val memberId: Int,
    val study: StudyApplyInfo
)

data class StudyApplyInfo(
    val studyId: Int,
    val title: String
)


//찜 구현
data class LikeResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: LikeResult
)

data class LikeResult(
    val studyTitle: String,
    val createdAt: String,
    val status: String // LIKE 또는 DISLIKE
)

//스터디 신청여부
data class IsAppliedResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: IsAppliedResult
)

data class  IsAppliedResult(
    val studyId: Int,
    val applied: Boolean
)


data class BookmarkResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: BookmarkResult
)

data class BookmarkResult(
    val totalPages: Int,
    val totalElements: Int,
    val first: Boolean,
    val last: Boolean,
    val size: Int,
    val content: List<BookmarkStudyItem>,
    val number: Int
)

data class BookmarkStudyItem(
    val studyId: Int,
    val imageUrl: String,
    val title: String,
    val introduction: String,
    val goal: String,
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



data class BookmarkItem( //StudyFragment에서 사용
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
    var liked: Boolean
)


data class GalleryResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: GalleryResult
)

data class GalleryResult(
    val studyId: Int,
    val images: List<GalleryItems>
)

data class GalleryItems(
    val imageId: Int,
    val imageUrl: String,
    val postId: Int
)

