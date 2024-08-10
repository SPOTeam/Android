import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        _studyRequest.value = StudyRequest(
            themes = _themes.value ?: listOf(), // 현재 themes 값을 사용
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
            fee = fee
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
        // Retrofit 인스턴스와 API 서비스 가져오기
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/") // 실제 서버의 URL로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(StudyApiService::class.java)

        // 현재 저장된 StudyRequest 데이터 가져오기
        val studyData = _studyRequest.value

        if (studyData != null) {
            // POST 요청 수행
            apiService.submitStudyData(memberId, studyData).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // 성공적으로 데이터가 전송된 경우
                        Log.d("StudyViewModel", "Study data submitted successfully.")
                    } else {
                        // 서버 응답이 성공적이지 않은 경우
                        Log.e("StudyViewModel", "Failed to submit study data. Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 네트워크 요청 실패
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
}
