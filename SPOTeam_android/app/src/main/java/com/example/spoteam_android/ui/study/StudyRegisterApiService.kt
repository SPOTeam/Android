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
    @POST("/spot/members/{memberId}/studies")
    fun submitStudyData(
        @Path("memberId") memberId: Int,
        @Body studyRequest: RequestBody // @Body로 변경하여 JSON 데이터만 전송
    ): Call<ApiResponsed> // ApiResponse의 타입도 확인 필요



    @GET("/spot/search/studies/on-studies/members/{memberId}")
    fun getStudies(
        @Path("memberId") memberId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<StudyResponse>

    @GET("/spot/search/studies/liked/members/{memberId}")
    fun getBookmark(
        @Path("memberId") memberId: Int,
    ): Call<StudyResponse>


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
    @POST("/spot/members/{memberId}/studies/{studyId}")
    fun applyStudy(
        @Path("memberId") memberId: Int,
        @Path("studyId") studyId: Int,
        @Body introduction: String
    ): Call<StudyApplyResponse>

    @POST("/spot/studies/{studyId}/members/{memberId}/like")
    fun toggleStudyLike(
        @Path("studyId") studyId: Int,
        @Path("memberId") memberId: Int
    ): Call<LikeResponse>


}

