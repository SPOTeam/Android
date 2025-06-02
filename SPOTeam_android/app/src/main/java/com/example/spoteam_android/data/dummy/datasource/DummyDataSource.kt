package com.example.spoteam_android.data.dummy.datasource

import com.example.spoteam_android.core.network.BaseResponse
import com.example.spoteam_android.data.dummy.dto.request.DummyRequestDto
import com.example.spoteam_android.data.dummy.dto.response.DummyResponseDto

interface DummyDataSource {
    suspend fun getService(request: DummyRequestDto): BaseResponse<DummyResponseDto>
}