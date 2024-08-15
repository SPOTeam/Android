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



data class StudyItem(
    val title: String,
    val introduction: String,
    val memberCount: Int,
    val heartCount: Int,
    val hitNum: Int,
    val maxPeople: Int,
    val studyState: String,
    val themeTypes: List<String>,
    val regions: List<String>
    // val imageUrl: String // 주석 제거 시 추가
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: arrayListOf(),
        parcel.createStringArrayList() ?: arrayListOf()
        // parcel.readString() ?: "" // imageUrl을 추가할 경우
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(introduction)
        parcel.writeInt(memberCount)
        parcel.writeInt(heartCount)
        parcel.writeInt(hitNum)
        parcel.writeInt(maxPeople)
        parcel.writeString(studyState)
        parcel.writeStringList(themeTypes)
        parcel.writeStringList(regions)
        // parcel.writeString(imageUrl) // imageUrl을 추가할 경우
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StudyItem> {
        override fun createFromParcel(parcel: Parcel): StudyItem {
            return StudyItem(parcel)
        }

        override fun newArray(size: Int): Array<StudyItem?> {
            return arrayOfNulls(size)
        }
    }
}

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
    val profileImage: Int,
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

data class ThemePreferences(
    @SerializedName("themes")
    val themes: List<String>
)

data class StudyReasons(
    @SerializedName("reasons")
    val reasons: List<String>
)

data class RegionsPreferences(
    @SerializedName("regions")
    val regions: List<String>
)




