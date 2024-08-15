import com.example.spoteam_android.search.SearchApiService
import com.example.spoteam_android.ui.interestarea.InterestAreaApiService
import com.example.spoteam_android.ui.interestarea.InterestSpecificAreaApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.teamspot.site/"  // 실제 API의 베이스 URL

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

    val apiService: SearchApiService by lazy {
        instance.create(SearchApiService::class.java)
    }
    val IaapiService: InterestAreaApiService by lazy {
        instance.create(InterestAreaApiService::class.java)
    }
    val IaSapiService: InterestSpecificAreaApiService by lazy {
        instance.create(InterestSpecificAreaApiService::class.java)
    }
}
