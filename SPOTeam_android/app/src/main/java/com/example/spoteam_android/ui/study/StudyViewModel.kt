import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudyViewModel : ViewModel() {

    private val _studyRequest = MutableLiveData<StudyRequest>()
    val studyRequest: LiveData<StudyRequest> = _studyRequest

    private val _profileImageUri = MutableLiveData<String?>()
    val profileImageUri: LiveData<String?> = _profileImageUri

    private val _themes = MutableLiveData<List<String>>()
    val themes: LiveData<List<String>> = _themes

    fun setProfileImageUri(uri: String?) {
        _profileImageUri.value = uri
        updateStudyRequest()
    }

    fun setStudyData(
        title: String, goal: String, introduction: String, isOnline: Boolean, profileImage: String?,
        regions: List<String>?, maxPeople: Int, gender: Gender, minAge: Int, maxAge: Int, fee: Int
    ) {
        val hasFee = fee > 0
        _studyRequest.value = StudyRequest(
            themes = _themes.value ?: listOf(),
            title = title,
            goal = goal,
            introduction = introduction,
            isOnline = isOnline,
            profileImage = profileImage,  // Use the image URL or path here
            regions = regions,
            maxPeople = maxPeople,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            fee = fee,
            hasFee = hasFee
        )
    }

    private fun updateStudyRequest() {
        _studyRequest.value = _studyRequest.value?.copy(
            profileImage = _profileImageUri.value,
            themes = _themes.value ?: listOf()
        )
    }

    fun updateThemes(newThemes: List<String>) {
        _themes.value = newThemes
        updateStudyRequest()
    }

    fun submitStudyData(memberId: Int) {  // Remove Context from method signature
        val retrofit = getRetrofit()
        val apiService = retrofit.create(StudyApiService::class.java)
        val studyData = _studyRequest.value

        if (studyData != null) {
            val studyRequestBody = createStudyRequestBody(studyData)

            apiService.submitStudyData(memberId, studyRequestBody)
                .enqueue(object : Callback<ApiResponsed> {  // Ensure ApiResponsed is the correct type
                    override fun onResponse(call: Call<ApiResponsed>, response: Response<ApiResponsed>) {
                        if (response.isSuccessful) {
                            response.body()?.let { apiResponse ->
                                Log.d("StudyViewModel", "Study data submitted successfully.")
                                Log.d("StudyViewModel", "Response: ${Gson().toJson(apiResponse)}")
                            } ?: run {
                                Log.d("StudyViewModel", "No response body returned.")
                            }
                        } else {
                            Log.e("StudyViewModel", "Failed to submit study data. Response code: ${response.code()}")
                            response.errorBody()?.let { errorBody ->
                                Log.e("StudyViewModel", "Error body: ${errorBody.string()}")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponsed>, t: Throwable) {
                        Log.e("StudyViewModel", "Error submitting study data: ${t.message}")
                    }
                })
        } else {
            Log.e("StudyViewModel", "No study data available to submit.")
        }
    }

    fun clearRegions() {
        _studyRequest.value = _studyRequest.value?.copy(regions = null)
    }

    private fun createStudyRequestBody(studyRequest: StudyRequest): RequestBody {
        val gson = Gson()
        val json = gson.toJson(studyRequest)
        return json.toRequestBody("application/json".toMediaType())
    }

    private fun getRetrofit(): Retrofit {
        val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NywidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzNjk4Nzk1LCJleHAiOjE3MjM3ODUxOTV9.mIyb1iGWC1QyyfLFgaBRSsg2QdPbpBLJiuEqztepvzY" // Ensure this is correct
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}