package com.example.spoteam_android.data.study.mapper

import com.example.spoteam_android.data.study.dto.response.ImagesResponseDto
import com.example.spoteam_android.domain.study.entity.ImageResponse

fun ImagesResponseDto.toDomain() = ImageResponse(
    studyId = studyId,
    images = images.map {
        ImageResponse.ImagesInfo(
            imageId = it.imageId,
            imageUrl = it.imageUrl,
            postId = it.postId
        )
    }
)