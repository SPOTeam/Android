package com.example.spoteam_android.data.dummy.service

import com.example.spoteam_android.core.network.BaseResponse
import com.example.spoteam_android.data.dummy.dto.request.DummyRequestDto
import com.example.spoteam_android.data.dummy.dto.response.DummyResponseDto
import retrofit2.http.Body
import retrofit2.http.GET

interface DummyService {

    companion object {
        const val API = "api"
        const val V1 = "v1"
        const val SERVICE = "service"
    }

    @GET("/$API/$V1/$SERVICE")
    suspend fun getServiceData(
        @Body request: DummyRequestDto
    ): BaseResponse<DummyResponseDto>

}