import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudyViewModel : ViewModel() {

    private val _studyRequest = MutableLiveData<StudyRequest>()
    val studyRequest: LiveData<StudyRequest> = _studyRequest

    private val _profileImageUri = MutableLiveData<String?>()

    fun setProfileImageUri(uri: String?) {
        _profileImageUri.value = uri
        updateStudyRequest()
    }

    fun setStudyData(
        title: String, goal: String, introduction: String, isOnline: Boolean,profileImage: String?,
        regions: List<String>, maxPeople: Int, gender: Gender, minAge: Int, maxAge: Int, fee: Int
    ) {
        _studyRequest.value = StudyRequest(
            themes = listOf("어학"), // 예시로 추가된 값
            title = title,
            goal = goal,
            introduction = introduction,
            isOnline = isOnline,
            profileImage = _profileImageUri.value,
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
            profileImage = _profileImageUri.value
        )
    }

    fun submitStudyData() {
        // 여기서 _studyRequest.value를 사용해 서버로 POST 요청을 보냅니다.
    }
}
