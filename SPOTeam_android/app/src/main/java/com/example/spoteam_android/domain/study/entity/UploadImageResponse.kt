package com.example.spoteam_android.domain.study.entity


data class UploadImageResponse(
    val imageUrls: List<ImageUrlData>,
    val imageCount: Int
) {
    data class ImageUrlData(
        val imageUrl: String,
        val uploadAt: String
    )
}