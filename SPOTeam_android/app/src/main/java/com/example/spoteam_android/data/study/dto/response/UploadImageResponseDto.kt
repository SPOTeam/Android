package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UploadImageResponseDto(
    @SerializedName("imageUrls")
    val imageUrls: List<ImageUrlDataDto>,
    @SerializedName("imageCount")
    val imageCount: Int
) {
    @Serializable
    data class ImageUrlDataDto(
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("uploadAt")
        val uploadAt: String
    )
}