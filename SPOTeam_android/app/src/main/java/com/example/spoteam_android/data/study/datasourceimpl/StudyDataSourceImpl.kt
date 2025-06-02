package com.example.spoteam_android.data.study.datasourceimpl

import com.example.spoteam_android.data.study.datasource.StudyDataSource
import com.example.spoteam_android.data.study.dto.request.MakeScheduleRequestDto
import com.example.spoteam_android.data.study.dto.request.StudyRegisterRequestDto
import com.example.spoteam_android.data.study.service.StudyService
import okhttp3.MultipartBody
import javax.inject.Inject

class StudyDataSourceImpl @Inject constructor(
    private val studyService: StudyService
) : StudyDataSource {

    override suspend fun registerStudy(request: StudyRegisterRequestDto) =
        studyService.registerStudy(request)

    override suspend fun getStudies(page: Int, size: Int) =
        studyService.getStudies(page, size)

    override suspend fun getBookmarkStudies(page: Int, size: Int) =
        studyService.getBookmarkStudies(page, size)

    override suspend fun getStudyMembers(studyId: Int) =
        studyService.getStudyMembers(studyId)

    override suspend fun getStudyDetails(studyId: Int) =
        studyService.getStudyDetails(studyId)

    override suspend fun makeSchedules(studyId: Int, request: MakeScheduleRequestDto) =
        studyService.makeSchedules(studyId, request)

    override suspend fun getStudySchedules(studyId: Int, page: Int, size: Int) =
        studyService.getStudySchedules(studyId, page, size)

    override suspend fun getRecentAnnounce(studyId: Int) =
        studyService.getRecentAnnounce(studyId)

    override suspend fun applyStudy(studyId: Int, memberId: Int, introduction: String) =
        studyService.applyStudy(studyId, memberId, introduction)

    override suspend fun toggleStudyLike(studyId: Int) =
        studyService.toggleStudyLike(studyId)

    override suspend fun getIsApplied(studyId: Int) =
        studyService.getIsApplied(studyId)

    override suspend fun patchStudy(studyId: Int, request: StudyRegisterRequestDto) =
        studyService.patchStudy(studyId, request)

    override suspend fun uploadImages(images: List<MultipartBody.Part>) =
        studyService.uploadImages(images)

    override suspend fun getStudyImages(studyId: Int, offset: Int, limit: Int) =
        studyService.getStudyImages(studyId, offset, limit)
}