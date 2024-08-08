package com.example.spoteam_android.ui.community

import java.util.Date

data class CommunityResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: CommunityContentInfo
)

data class CommunityContentInfo(
    val sortType: String,
    val postBest5Responses: List<ContentDetailInfo>
)

data class ContentDetailInfo(
    val rank: Int,
    val postTitle: String,
    val commentCount: Int
)
/**************************************/
data class RepresentativeResponse(
    val isSuccess : String,
    val code : String,
    val message : String,
    val result : RepresentativeContentInfo
)

data class RepresentativeContentInfo(
    val responses: List<RepresentativeDetailInfo>
)

data class RepresentativeDetailInfo (
    val postType : String,
    val postTitle : String,
    val commentCount : Int
)
/**************************************/
data class AnnouncementResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: AnnouncementContentInfo
)

data class AnnouncementContentInfo(
    val responses: List<AnnouncementDetailInfo>
)

data class AnnouncementDetailInfo(
    val rank: Int,
    val postTitle: String,
    val commentCount: Int
)
/**************************************/
data class CategoryPagesResponse(
    val isSuccess: String,
    val code: String,
    val message: String,
    val result: CategoryPagesInfo
)

data class CategoryPagesInfo(
    val postType : String,
    val postResponses: List<CategoryPagesDetail>,
    val totalPages : Int,
    val totalElements : Int,
    val isFirst : String,
    val isLast : String
)

data class CategoryPagesDetail(
    val writer : String,
    val writtenTime : String,
    val scrapCount : Int,
    val title : String,
    val content : String,
    val likeCount : Int,
    val commentCount : Int,
    val viewCount : Int
)
