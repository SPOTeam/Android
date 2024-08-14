import com.example.spoteam_android.StudyResponse
import com.example.spoteam_android.ui.mypage.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyApiService {
    @POST("/spot/members/{memberId}/studies") // 여기에 실제 API 엔드포인트를 넣습니다.
    fun submitStudyData(@Path("memberId") memberId: Int,@Body studyRequest: StudyRequest): Call<Void>


    @GET("/spot/search/studies/on-studies/members/{memberId}")
    fun getStudies(
        @Path("memberId") memberId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<StudyResponse>
}
