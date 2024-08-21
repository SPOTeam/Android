package com.example.spoteam_android.search

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
