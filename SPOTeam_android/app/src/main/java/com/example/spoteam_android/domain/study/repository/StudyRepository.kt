package com.example.spoteam_android.domain.study.repository

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
import okhttp3.MultipartBody

interface StudyRepository {
    suspend fun registerStudy(request: StudyRegisterRequest): Result<StudyRegisterResponse>

    suspend fun patchStudy(studyId: Int, request: StudyRegisterRequest): Result<StudyRegisterResponse>

    suspend fun getStudies(page: Int, size: Int): Result<StudyDataResponse>

    suspend fun getBookmarkStudies(page: Int, size: Int): Result<StudyDataResponse>

    suspend fun getStudyDetails(studyId: Int): Result<StudyDetailResponse>

    suspend fun getStudyMembers(studyId: Int): Result<StudyMemberResponse>

    suspend fun makeSchedules(studyId: Int, schedule: MakeScheduleRequest): Result<MakeScheduleResponse>

    suspend fun getStudySchedules(studyId: Int, page: Int, size: Int): Result<ScheduleListResponse>

    suspend fun getRecentAnnounce(studyId: Int): Result<RecentAnnounceResponse>

    suspend fun applyStudy(studyId: Int, memberId: Int, introduction: String): Result<StudyApplyResponse>

    suspend fun toggleStudyLike(studyId: Int): Result<StudyLikeResponse>

    suspend fun getIsApplied(studyId: Int): Result<IsAppliedResponse>

    suspend fun uploadImages(images: List<MultipartBody.Part>): Result<UploadImageResponse>

    suspend fun getStudyImages(studyId: Int, offset: Int, limit: Int): Result<ImageResponse>
}
