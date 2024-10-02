import com.example.spoteam_android.search.SearchApiService
import com.example.spoteam_android.ui.calendar.CalendarApiService
import com.example.spoteam_android.ui.interestarea.GetInterestCategoryApiService
import com.example.spoteam_android.ui.interestarea.GetMemberInterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestSpecificAreaApiService
import com.example.spoteam_android.ui.interestarea.MyInterestStudyAllApiService
import com.example.spoteam_android.ui.interestarea.MyInterestStudySpecificApiService
import com.example.spoteam_android.ui.interestarea.RecommendStudyApiService
import com.example.spoteam_android.ui.interestarea.RecruitingStudyApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"  // 실제 API의 베이스 URL

    private val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6MTAsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTcyNzg1ODY4MCwiZXhwIjoxNzI3OTQ1MDgwfQ.U95Yqs2R9KMr3wNYzdslPVNXP88s_4ZGM361hbq-IBU"

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

    val SSService: SearchApiService by lazy{
        instance.create(SearchApiService::class.java)
    }

    val RSService: RecruitingStudyApiService by lazy {
        instance.create(RecruitingStudyApiService::class.java)
    }

    val MIAService: MyInterestStudyAllApiService by lazy{
        instance.create(MyInterestStudyAllApiService::class.java)
    }

    val GICService: GetInterestCategoryApiService by lazy{
        instance.create(GetInterestCategoryApiService::class.java)
    }

    val MISSerivice: MyInterestStudySpecificApiService by lazy{
        instance.create(MyInterestStudySpecificApiService::class.java)
    }

}
