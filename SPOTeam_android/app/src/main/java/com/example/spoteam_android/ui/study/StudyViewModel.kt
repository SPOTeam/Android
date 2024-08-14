import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spoteam_android.ui.mypage.ThemePreferenceFragment.LoginchecklistAuthInterceptor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

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
            profileImage = profileImage,
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

    fun submitStudyData(memberId: Int, context: Context) {
        val retrofit = getRetrofit()
        val apiService = retrofit.create(StudyApiService::class.java)
        val studyData = _studyRequest.value

        if (studyData != null) {
            // 이미지를 Base64로 변환
            val profileImageBase64 = studyData.profileImage?.let { uri ->
                Uri.parse(uri).let { convertImageUriToBase64(it, context) }
            }

            // Base64 문자열 길이 확인 (디버깅용)
            Log.d("StudyViewModel", "Profile Image Base64 Length: ${profileImageBase64?.length}")

            // StudyRequest 데이터에 Base64 문자열로 설정
            apiService.submitStudyData(memberId, studyData.copy(profileImage = profileImageBase64)).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("StudyViewModel", "Study data submitted successfully.")
                    } else {
                        Log.e("StudyViewModel", "Failed to submit study data. Response code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
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

    private fun convertImageUriToBase64(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 이미지 크기 조정 및 압축
            val maxSize = 400 // 더 작은 사이즈로 조정
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap!!, maxSize, maxSize, true)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream) // 압축 품질을 50으로 설정
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)

        } catch (e: IOException) {
            Log.e("ImageConversion", "Failed to convert image to Base64", e)
            null
        }
    }


    private fun getRetrofit(): Retrofit {
        val authToken = "eyJhbGciOiJIUzI1NiJ9.eyJtZW1iZXJJZCI6NywidG9rZW5UeXBlIjoiYWNjZXNzIiwiaWF0IjoxNzIzMzc3ODk5LCJleHAiOjE3MjMzODE0OTl9.pe2trdaOvCD-OHt640kqX10umJ-WHiRezN42fzIZXgI"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(LoginchecklistAuthInterceptor(authToken))
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.teamspot.site/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
