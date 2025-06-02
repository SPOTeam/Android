package com.example.spoteam_android.data.study.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ImagesResponseDto(
    @SerializedName("studyId")
    val studyId: Int,
    @SerializedName("images")
    val images: List<ImagesDto>
) {
    @Serializable
    data class ImagesDto(
        @SerializedName("imageId")
        val imageId: Int,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("postId")
        val postId: Int
    )
}