package com.example.spoteam_android.data.dummy.repositoryimpl

import com.example.spoteam_android.data.dummy.mapper.toDummyServiceModel
import com.example.spoteam_android.data.dummy.mapper.toDummyServiceRequest
import com.example.spoteam_android.data.dummy.service.DummyService
import com.example.spoteam_android.domain.dummy.entity.DummyResponse
import com.example.spoteam_android.domain.dummy.entity.DummyRequest
import com.example.spoteam_android.domain.dummy.repository.DummyRepository
import javax.inject.Inject

class DummyRepositoryImpl @Inject constructor(
    private val dummyService: DummyService
) : DummyRepository {
    override suspend fun getService(request: DummyResponse): Result<DummyRequest> =
        runCatching {
            val response = dummyService.getServiceData(
                request = request.toDummyServiceRequest()
            )
            response.result?.toDummyServiceModel() ?: throw Exception("Response data is null")
        }
}