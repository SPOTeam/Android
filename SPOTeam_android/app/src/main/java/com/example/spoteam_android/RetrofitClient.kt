import com.example.spoteam_android.ui.calendar.CalendarApiService
import com.example.spoteam_android.ui.interestarea.GetMemberInterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestSpecificAreaApiService
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"  // 실제 API의 베이스 URL

    private val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MTAsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTcyMzk5MDYyNCwiZXhwIjoxNzI0MDc3MDI0fQ.DpY93HGLtXMvww3t0Yh2enJLCuhsv1bZ9mFA8eSeUCs"

    fun getAuthToken(): String {
        return "Bearer $authToken"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 설정
            .build()
    }

    val IaapiService: InterestAreaApiService by lazy {
        instance.create(InterestAreaApiService::class.java)
    }
    val IaSapiService: InterestSpecificAreaApiService by lazy {
        instance.create(InterestSpecificAreaApiService::class.java)
    }

    val GetIaService: GetMemberInterestAreaApiService by lazy {
        instance.create(GetMemberInterestAreaApiService::class.java)
    }

    val GetRSService: RecommendStudyApiService by lazy{
        instance.create(RecommendStudyApiService::class.java)
    }

    val CAService: CalendarApiService by lazy{
        instance.create(CalendarApiService::class.java)
    }
}
