import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyApiService {
    @POST("/spot/members/{memberId}/studies") // 여기에 실제 API 엔드포인트를 넣습니다.
    fun submitStudyData(@Path("memberId") memberId: Int,@Body studyRequest: StudyRequest): Call<Void>
}
