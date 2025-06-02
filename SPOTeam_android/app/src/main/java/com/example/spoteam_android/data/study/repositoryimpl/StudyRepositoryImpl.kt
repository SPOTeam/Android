package com.example.spoteam_android.data.study.repositoryimpl

import com.example.spoteam_android.data.study.datasource.StudyDataSource
import com.example.spoteam_android.data.study.mapper.toDomain
import com.example.spoteam_android.data.study.mapper.toDto
import com.example.spoteam_android.domain.study.entity.ImageResponse
import com.example.spoteam_android.domain.study.entity.IsAppliedResponse
import com.example.spoteam_android.domain.study.entity.MakeScheduleRequest
import com.example.spoteam_android.domain.study.entity.MakeScheduleResponse
import com.example.spoteam_android.domain.study.entity.RecentAnnounceResponse
import com.example.spoteam_android.domain.study.entity.ScheduleListResponse
import com.example.spoteam_android.domain.study.entity.StudyApplyResponse
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.domain.study.entity.StudyDetailResponse
import com.example.spoteam_android.domain.study.entity.StudyLikeResponse
import com.example.spoteam_android.domain.study.entity.StudyMemberResponse
import com.example.spoteam_android.domain.study.entity.StudyRegisterRequest
import com.example.spoteam_android.domain.study.entity.StudyRegisterResponse
import com.example.spoteam_android.domain.study.entity.UploadImageResponse
import com.example.spoteam_android.domain.study.repository.StudyRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class StudyRepositoryImpl @Inject constructor(
    private val studyDataSource: StudyDataSource
) : StudyRepository {

    override suspend fun registerStudy(request: StudyRegisterRequest): Result<StudyRegisterResponse> = runCatching {
        val response = studyDataSource.registerStudy(request.toDto())
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun patchStudy(studyId: Int, request: StudyRegisterRequest): Result<StudyRegisterResponse> = runCatching {
        val response = studyDataSource.patchStudy(studyId, request.toDto())
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getStudies(page: Int, size: Int): Result<StudyDataResponse> = runCatching {
        val response = studyDataSource.getStudies(page, size)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getBookmarkStudies(page: Int, size: Int): Result<StudyDataResponse> = runCatching {
        val response = studyDataSource.getBookmarkStudies(page, size)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getStudyDetails(studyId: Int): Result<StudyDetailResponse> = runCatching {
        val response = studyDataSource.getStudyDetails(studyId)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getStudyMembers(studyId: Int): Result<StudyMemberResponse> = runCatching {
        val response = studyDataSource.getStudyMembers(studyId)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun makeSchedules(studyId: Int, schedule: MakeScheduleRequest): Result<MakeScheduleResponse> = runCatching {
        val response = studyDataSource.makeSchedules(studyId, schedule.toDto())
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getStudySchedules(studyId: Int, page: Int, size: Int): Result<ScheduleListResponse> = runCatching {
        val response = studyDataSource.getStudySchedules(studyId, page, size)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getRecentAnnounce(studyId: Int): Result<RecentAnnounceResponse> = runCatching {
        val response = studyDataSource.getRecentAnnounce(studyId)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun applyStudy(studyId: Int, memberId: Int, introduction: String): Result<StudyApplyResponse> = runCatching {
        val response = studyDataSource.applyStudy(studyId, memberId, introduction)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun toggleStudyLike(studyId: Int): Result<StudyLikeResponse> = runCatching {
        val response = studyDataSource.toggleStudyLike(studyId)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getIsApplied(studyId: Int): Result<IsAppliedResponse> = runCatching {
        val response = studyDataSource.getIsApplied(studyId)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun uploadImages(images: List<MultipartBody.Part>): Result<UploadImageResponse> = runCatching {
        val response = studyDataSource.uploadImages(images)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }

    override suspend fun getStudyImages(studyId: Int, offset: Int, limit: Int): Result<ImageResponse> = runCatching {
        val response = studyDataSource.getStudyImages(studyId, offset, limit)
        response.result?.toDomain() ?: throw IllegalStateException("No result")
    }
}
