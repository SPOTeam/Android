package com.example.spoteam_android.data.study.datasource

import com.example.spoteam_android.core.network.BaseResponse
import com.example.spoteam_android.data.study.dto.request.MakeScheduleRequestDto
import com.example.spoteam_android.data.study.dto.request.StudyRegisterRequestDto
import com.example.spoteam_android.data.study.dto.response.ImagesResponseDto
import com.example.spoteam_android.data.study.dto.response.IsAppliedResponseDto
import com.example.spoteam_android.data.study.dto.response.MakeScheduleResponseDto
import com.example.spoteam_android.data.study.dto.response.RecentAnnounceResponseDto
import com.example.spoteam_android.data.study.dto.response.ScheduleListResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyApplyResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyDataResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyDetailResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyLikeResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyMemberResponseDto
import com.example.spoteam_android.data.study.dto.response.StudyRegisterResponseDto
import com.example.spoteam_android.data.study.dto.response.UploadImageResponseDto
import okhttp3.MultipartBody

interface StudyDataSource {

    suspend fun registerStudy(request: StudyRegisterRequestDto): BaseResponse<StudyRegisterResponseDto>

    suspend fun getStudies(page: Int, size: Int): BaseResponse<StudyDataResponseDto>

    suspend fun getBookmarkStudies(page: Int, size: Int): BaseResponse<StudyDataResponseDto>

    suspend fun getStudyMembers(studyId: Int): BaseResponse<StudyMemberResponseDto>

    suspend fun getStudyDetails(studyId: Int): BaseResponse<StudyDetailResponseDto>

    suspend fun makeSchedules(studyId: Int, request: MakeScheduleRequestDto): BaseResponse<MakeScheduleResponseDto>

    suspend fun getStudySchedules(studyId: Int, page: Int, size: Int): BaseResponse<ScheduleListResponseDto>

    suspend fun getRecentAnnounce(studyId: Int): BaseResponse<RecentAnnounceResponseDto>

    suspend fun applyStudy(studyId: Int, memberId: Int, introduction: String): BaseResponse<StudyApplyResponseDto>

    suspend fun toggleStudyLike(studyId: Int): BaseResponse<StudyLikeResponseDto>

    suspend fun getIsApplied(studyId: Int): BaseResponse<IsAppliedResponseDto>

    suspend fun patchStudy(studyId: Int, request: StudyRegisterRequestDto): BaseResponse<StudyRegisterResponseDto>

    suspend fun uploadImages(images: List<MultipartBody.Part>): BaseResponse<UploadImageResponseDto>

    suspend fun getStudyImages(studyId: Int, offset: Int, limit: Int): BaseResponse<ImagesResponseDto>
}