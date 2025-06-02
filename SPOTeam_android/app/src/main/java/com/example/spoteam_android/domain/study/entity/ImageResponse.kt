package com.example.spoteam_android.domain.study.entity

data class ImageResponse(
    val studyId: Int,
    val images: List<ImagesInfo>
) {
    data class ImagesInfo(
        val imageId: Int,
        val imageUrl: String,
        val postId: Int
    )
}