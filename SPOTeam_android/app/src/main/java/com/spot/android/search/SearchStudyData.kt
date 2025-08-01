package com.spot.android.search

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,

    @SerializedName("code")
    val code: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("result")
    val result: SearchResult
)

data class SearchResult(
    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("first")
    val first: Boolean,

    @SerializedName("last")
    val last: Boolean,

    @SerializedName("size")
    val size: Int,

    @SerializedName("content")
    val content: List<SearchContent>,

    @SerializedName("number")
    val number: Int,
)

data class SearchContent(
    @SerializedName("studyId")
    val studyId: Int,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("introduction")
    val introduction: String,

    @SerializedName("goal")
    val goal: String,

    @SerializedName("memberCount")
    val memberCount: Int,

    @SerializedName("heartCount")
    val heartCount: Int,

    @SerializedName("hitNum")
    val hitNum: Int,

    @SerializedName("maxPeople")
    val maxPeople: Int,

    @SerializedName("studyState")
    val studyState: String,

    @SerializedName("themeTypes")
    val themeTypes: List<String>,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("liked")
    val liked: Boolean
)

data class PopularKeywordResponse(
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: PopularKeywordResult
)

data class PopularKeywordResult(
    @SerializedName("keyword")
    val keyword: List<KeywordItem>,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class KeywordItem(
    @SerializedName("keyword")
    val keyword: String,

    @SerializedName("point")
    val point: Int
)

data class StudyDetailResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: StudyDetailResult
)

data class StudyDetailResult(
    val studyId: Int,
    val imageUrl: String,
    val studyName: String,
    val studyOwner: StudyOwner,
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

data class StudyOwner(
    val ownerId: Int,
    val ownerName: String
)




