package com.example.spoteam_android

data class CommunityResponse<T>(
    val isSuccess : String,
    val code : String,
    val message : String,
    val result : T
)

data class CommunityContentInfo(
    val title : String,
    val date : String,
    val place : String,
    val people : String,
    val feeling : String,
    val hashtag : String
)
