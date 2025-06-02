package com.example.spoteam_android.data.study.service

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyService {

    //register study
    @POST("/spot/studies")
    suspend fun registerStudy(@Body request: StudyRegisterRequestDto): BaseResponse<StudyRegisterResponseDto>

    //search on studies
    @GET("/spot/search/studies/on-studies")
    suspend fun getStudies(
        @Query("page") page: Int,
        @Query("size") size: Int = 5
    ): BaseResponse<StudyDataResponseDto>

    //get bookmark studies
    @GET("/spot/search/studies/liked")
    suspend fun getBookmarkStudies(
        @Query("page") page: Int,
        @Query("size") size: Int = 5
    ): BaseResponse<StudyDataResponseDto>

    //get study members
    @GET("/spot/studies/{studyId}/members")
    suspend fun getStudyMembers(
        @Path("studyId") studyId: Int
    ): BaseResponse<StudyMemberResponseDto>

    //get study details
    @GET("/spot/studies/{studyId}")
    suspend fun getStudyDetails(
        @Path("studyId") studyId: Int
    ): BaseResponse<StudyDetailResponseDto>

    //make schedule
    @POST("/spot/studies/{studyId}/schedules")
    suspend fun makeSchedules(
        @Path("studyId") studyId: Int,
        @Body scheduleRequest: MakeScheduleRequestDto
    ): BaseResponse<MakeScheduleResponseDto>

    //get schedule
    @GET("/spot/studies/{studyId}/upcoming-schedules")
    suspend fun getStudySchedules(
        @Path("studyId") studyId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int = 2,
    ): BaseResponse<ScheduleListResponseDto>

    //get recent announce
    @GET("/spot/studies/{studyId}/announce")
    suspend fun getRecentAnnounce(
        @Path("studyId") studyId: Int,
    ): BaseResponse<RecentAnnounceResponseDto>

    //apply Study
    @POST("/spot/studies/{studyId}")
    suspend fun applyStudy(
        @Path("studyId") studyId: Int,
        @Query("memberId") memberId: Int,
        @Body introduction: String
    ): BaseResponse<StudyApplyResponseDto>

    //toggle study like
    @POST("/spot/studies/{studyId}/like")
    suspend fun toggleStudyLike(
        @Path("studyId") studyId: Int,
    ): BaseResponse<StudyLikeResponseDto>

    //get is applied study
    @GET("/spot/studies/{studyId}/is-applied")
    suspend fun getIsApplied(
        @Path("studyId") studyId: Int
    ): BaseResponse<IsAppliedResponseDto>

    //patch study
    @PATCH("/spot/studies/{studyId}")
    suspend fun patchStudy(
        @Path("studyId") studyId: Int,
        @Body request: StudyRegisterRequestDto
    ): BaseResponse<StudyRegisterResponseDto>

    //upload image
    @Multipart
    @POST("/spot/util/images")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>
    ): BaseResponse<UploadImageResponseDto>

    //get Images
    @GET("/spot/studies/{studyId}/images")
    suspend fun getStudyImages(
        @Path("studyId") studyId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): BaseResponse<ImagesResponseDto>
}
