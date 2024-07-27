import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface StudyApiService {
    @POST("https://www.teamspot.site/spot/members/1/studies") // 여기에 실제 API 엔드포인트를 넣습니다.
    fun submitStudyData(@Body studyRequest: StudyRequest): Call<Void> // 서버가 응답으로 반환할 타입을 지정합니다.
}
