package com.example.spoteam_android.data.dummy.datasourceimpl

import com.example.spoteam_android.core.network.BaseResponse
import com.example.spoteam_android.data.dummy.datasource.DummyDataSource
import com.example.spoteam_android.data.dummy.dto.request.DummyRequestDto
import com.example.spoteam_android.data.dummy.dto.response.DummyResponseDto
import com.example.spoteam_android.data.dummy.service.DummyService
import javax.inject.Inject

class DummyDataSourceImpl @Inject constructor(
    private val dummyService: DummyService
) : DummyDataSource {
    override suspend fun getService(request: DummyRequestDto): BaseResponse<DummyResponseDto> =
        dummyService.getServiceData(request)
}