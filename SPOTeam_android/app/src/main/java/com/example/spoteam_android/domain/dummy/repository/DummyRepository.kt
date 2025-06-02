package com.example.spoteam_android.domain.dummy.repository

import com.example.spoteam_android.domain.dummy.entity.DummyResponse
import com.example.spoteam_android.domain.dummy.entity.DummyRequest

interface DummyRepository {
    suspend fun getService(request: DummyResponse): Result<DummyRequest>
}