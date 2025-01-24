import com.example.spoteam_android.BookmarkResponse
import com.example.spoteam_android.GalleryResponse
import com.example.spoteam_android.IsAppliedResponse
import com.example.spoteam_android.LikeResponse
import com.example.spoteam_android.MemberResponse
import com.example.spoteam_android.RecentAnnounceResponse
import com.example.spoteam_android.ScheduleListResponse
import com.example.spoteam_android.ScheduleRequest
import com.example.spoteam_android.ScheduleResponse
import com.example.spoteam_android.StudyApplyResponse
import com.example.spoteam_android.StudyDetailsResponse
import com.example.spoteam_android.StudyItem
import com.example.spoteam_android.StudyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
interface StudyApiService {
    @POST("/spot//studies")
    fun submitStudyData(
        @Body studyRequest: RequestBody // @Body로 변경하여 JSON 데이터만 전송
    ): Call<ApiResponsed> // ApiResponse의 타입도 확인 필요


    //내가 참여하고 있는 스터디 불러오기
    @GET("/spot/search/studies/on-studies")
    fun getStudies(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<StudyResponse>
    //찜한 스터디 조회
    @GET("/spot/search/studies/liked")
    fun getBookmark(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BookmarkResponse>


    @Multipart
    @POST("/spot/util/images")
    fun uploadImages(
        @Part images: List<MultipartBody.Part>

    ): Call<UploadResponse>

    @GET("/spot/studies/{studyId}/members")
    fun getStudyMembers(
        @Path("studyId") studyId: Int
    ): Call<MemberResponse>

    @GET("/spot/studies/{studyId}")
    fun getStudyDetails(
        @Path("studyId") studyId: Int
    ): Call<StudyDetailsResponse>

    @POST("/spot/studies/{studyId}/schedules")
    fun addSchedules(
        @Path("studyId") studyId: Int,
        @Body scheduleRequest: ScheduleRequest
    ): Call<ScheduleResponse>
    //다가오는 모임 불러오기
    @GET("/spot/studies/{studyId}/upcoming-schedules")
    fun getStudySchedules(
        @Path("studyId") studyId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): Call<ScheduleListResponse>

    //최근 공지 1개 불러오기
    @GET("/spot/studies/{studyId}/announce")
    fun getRecentAnnounce(
        @Path("studyId") studyId: Int,
    ): Call<RecentAnnounceResponse>

    //스터디 참여 신청하기
    @POST("/spot/studies/{studyId}")
    fun applyStudy(
        @Path("studyId") studyId: Int,
        @Query("memberId") memberId: Int,
        @Body introduction: String
    ): Call<StudyApplyResponse>
    //스터디 찜하기
    @POST("/spot/studies/{studyId}/like")
    fun toggleStudyLike(
        @Path("studyId") studyId: Int,
    ): Call<LikeResponse>

    @GET("/spot/studies/{studyId}/is-applied")
    fun getIsApplied(
        @Path("studyId") studyId: Int
    ): Call<IsAppliedResponse>

    //갤러리 이미지 불러오기
    @GET("/spot/studies/{studyId}/images")
    fun getStudyImages(
        @Path("studyId") studyId: Int,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<GalleryResponse>


}

