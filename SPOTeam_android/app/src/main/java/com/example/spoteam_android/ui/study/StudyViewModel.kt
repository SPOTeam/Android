import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spoteam_android.RetrofitInstance
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

    private val _maxPeople = MutableLiveData<Int>()
    val maxPeople: LiveData<Int> = _maxPeople

    private val _memberCount = MutableLiveData<Int>()
    val memberCount: LiveData<Int> = _memberCount

    private val _recentStudyId = MutableLiveData<Int?>()
    val recentStudyId: LiveData<Int?> get() = _recentStudyId

    fun setMaxPeople(value: Int) {
        _maxPeople.value = value
    }

    fun setMemberCount(value: Int) {
        _memberCount.value = value
    }

    fun setProfileImageUri(uri: String?) {
        _profileImageUri.value = uri
        updateStudyRequest()
    }
    private val _studyId = MutableLiveData<Int>()
    val studyId: LiveData<Int> get() = _studyId
    private val _studyImageUrl = MutableLiveData<String>()
    val studyImageUrl: LiveData<String> get() = _studyImageUrl
    private val _studyIntroduction = MutableLiveData<String>()
    val studyOwner = MutableLiveData<String>()
    val studyIntroduction: LiveData<String> get() = _studyIntroduction

    fun setStudyData(id: Int, imageUrl: String, studyIntroduction: String) {
        Log.d("StudyViewModel", "setStudyData 호출: studyId = $studyId, imageUrl = $imageUrl, introduction = $studyIntroduction")
        _studyId.value = id
        _studyImageUrl.value = imageUrl
        _studyIntroduction.value = studyIntroduction
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

    fun submitStudyData(memberId: Int) {
        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val studyData = _studyRequest.value

        if (studyData != null) {
            val studyRequestBody = createStudyRequestBody(studyData)

            apiService.submitStudyData(memberId, studyRequestBody)
                .enqueue(object : Callback<ApiResponsed> {
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

    fun setRecentStudyId(id: Int) {
        _recentStudyId.value = id
        Log.d("StudyViewModel", "최근 조회한 스터디 ID 설정: $id")
    }


}