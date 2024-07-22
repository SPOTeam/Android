package com.example.spoteam_android

data class indexData(
    val index : String,
    val content : String,
    val commentNum : String
)

data class categoryData(
    val category : String,
    val content : String,
    val commentNum : String
)

data class communityData(
    val title : String,
    val content : String,
    val likeNum : String,
    val commentNum : String,
    val viewNum : String,
    val bookmarkNum : String,
    val writer : String,
    val date : String
)
