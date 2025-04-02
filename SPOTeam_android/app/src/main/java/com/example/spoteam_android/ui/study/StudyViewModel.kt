import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spoteam_android.RetrofitInstance
import com.example.spoteam_android.StudyDetailsResponse
import com.example.spoteam_android.StudyDetailsResult
import com.example.spoteam_android.login.LocationItem
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
    private val _mode = MutableLiveData<StudyFormMode>(StudyFormMode.CREATE)
    val mode: LiveData<StudyFormMode> get() = _mode
    fun setMode(newMode: StudyFormMode) {
        _mode.value = newMode
    }

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

    // 내부용 (private)
    private val _studyOwner = MutableLiveData<String>()

    private val _locationList = mutableListOf<LocationItem>()

    private val _studyId = MutableLiveData<Int>()
    val studyId: LiveData<Int> get() = _studyId

    private val _studyImageUrl = MutableLiveData<String>()
    val studyImageUrl: LiveData<String> get() = _studyImageUrl

    private val _studyIntroduction = MutableLiveData<String>()
    val studyIntroduction: LiveData<String> get() = _studyIntroduction

    private val _patchSuccess = MutableLiveData<Boolean>()
    val patchSuccess: LiveData<Boolean> get() = _patchSuccess

    // 외부 노출용 (읽기 전용)
    val studyOwner: LiveData<String> get() = _studyOwner

    private val _recentStudyId = MutableLiveData<Int?>()
    val recentStudyId: LiveData<Int?> get() = _recentStudyId



    fun setStudyOwner(name: String) {
        _studyOwner.value = name
    }

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


    fun setStudyData(id: Int, imageUrl: String, studyIntroduction: String) {
        Log.d("StudyViewModel", "setStudyData 호출: studyId = $studyId, imageUrl = $imageUrl, introduction = $studyIntroduction")
        _studyId.value = id
        _studyImageUrl.value = imageUrl
        _studyIntroduction.value = studyIntroduction
    }

    fun fetchStudyDetail(studyId: Int) {
        val service = RetrofitInstance.retrofit.create(StudyApiService::class.java)

        service.getStudyDetails(studyId).enqueue(object : Callback<StudyDetailsResponse> {
            override fun onResponse(
                call: Call<StudyDetailsResponse>,
                response: Response<StudyDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        val result = body.result
                        Log.d("StudyViewModel", "서버 응답 isOnline: ${result?.isOnline}, regions: ${result?.regions}")

                        _themes.value = result.themes
                        _studyIntroduction.value = result.introduction
                        _studyId.value = result.studyId
                        _maxPeople.value = result.maxPeople
                        _memberCount.value = result.memberCount
                        _studyOwner.value = result.studyOwner.ownerName
                        _profileImageUri.value = result.profileImage
                        _studyRequest.value = result.toStudyRequest()

                    } else {
                        Log.e("StudyViewModel", "서버 응답 실패: ${body?.message}")
                    }
                } else {
                    Log.e("StudyViewModel", "HTTP 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StudyDetailsResponse>, t: Throwable) {
                Log.e("StudyViewModel", "통신 오류: ${t.message}")
            }
        })
    }






    fun setStudyData(
        title: String, goal: String, introduction: String, isOnline: Boolean, profileImage: String?,
        regions: List<String>?, maxPeople: Int, gender: Gender, minAge: Int, maxAge: Int, fee: Int
    ) {
        val hasFee = fee > 0

        val newRequest = StudyRequest(
            themes = _themes.value ?: listOf(),
            title = title,
            goal = goal,
            introduction = introduction,
            isOnline = isOnline,
            profileImage = profileImage,
            regions = regions,
            maxPeople = maxPeople,
            gender = gender,
            minAge = minAge,
            maxAge = maxAge,
            fee = fee,
            hasFee = hasFee
        )

        if (_studyRequest.value != newRequest) {
            Log.d("StudyViewModel", "✅ studyRequest 값 변경됨. 옵저버 갱신")
            _studyRequest.value = newRequest
        } else {
            Log.d("StudyViewModel", "⚠️ studyRequest 값이 같아서 옵저버 미호출됨")
        }
        _studyRequest.value = newRequest
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
    //스터디 수정하기
    fun patchStudyData() {
        val studyId = _studyId.value ?: return
        val studyData = _studyRequest.value ?: return
        val apiService = RetrofitInstance.retrofit.create(StudyApiService::class.java)
        val requestBody = createStudyRequestBody(studyData)

        apiService.patchStudyData(studyId, requestBody)
            .enqueue(object : Callback<ApiResponsed> {
                override fun onResponse(call: Call<ApiResponsed>, response: Response<ApiResponsed>) {
                    if (response.isSuccessful) {
                        Log.d("StudyViewModel", "스터디 수정 성공")
                        _patchSuccess.value = true
                    } else {
                        Log.e("StudyViewModel", "스터디 수정 실패: ${response.code()}")
                        _patchSuccess.value = false
                    }
                }

                override fun onFailure(call: Call<ApiResponsed>, t: Throwable) {
                    Log.e("StudyViewModel", "스터디 수정 오류: ${t.message}")
                    _patchSuccess.value = false
                }
            })
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

    fun StudyDetailsResult.toStudyRequest(): StudyRequest {
        return StudyRequest(
            themes = this.themes,
            title = this.studyName,
            goal = this.goal,
            introduction = this.introduction,
            isOnline = this.isOnline,
            profileImage = this.profileImage,
            regions = this.regions,
            maxPeople = this.maxPeople,
            gender = this.gender,
            minAge = this.minAge,
            maxAge = this.maxAge,
            fee = this.fee,
            hasFee = this.fee > 0
        )
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

    fun setLocationList(list: List<LocationItem>) {
        _locationList.clear()
        _locationList.addAll(list)
    }

    fun findAddressFromCode(code: String): String? {
        return _locationList.find { it.code == code }?.address
    }

    fun reset() {
        _mode.value = StudyFormMode.CREATE
        _studyRequest.value = null
        _themes.value = listOf()
        _profileImageUri.value = null
        _maxPeople.value = 0
        _memberCount.value = 0
        _studyOwner.value = ""
        _recentStudyId.value = null
        _studyId.value = 0
        _studyImageUrl.value = ""
        _studyIntroduction.value = ""
    }


}