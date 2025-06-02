package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.UploadImageResponseDto
import com.example.spoteam_android.domain.study.entity.UploadImageResponse

fun UploadImageResponseDto.toDomain() = UploadImageResponse(
    imageUrls = imageUrls.map {
        UploadImageResponse.ImageUrlData(
            imageUrl = it.imageUrl,
            uploadAt = it.uploadAt
        )
    },
    imageCount = imageCount
)