package com.example.spoteam_android.data.dummy.mapper

import com.example.spoteam_android.data.dummy.dto.request.DummyRequestDto
import com.example.spoteam_android.data.dummy.dto.response.DummyResponseDto
import com.example.spoteam_android.domain.dummy.entity.DummyResponse
import com.example.spoteam_android.domain.dummy.entity.DummyRequest

fun DummyResponseDto.toDummyServiceModel() = DummyRequest(
    info = info
)

fun DummyResponse.toDummyServiceRequest() = DummyRequestDto(
    id = id
)