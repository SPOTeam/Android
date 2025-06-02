package com.example.spoteam_android.presentation.study

import StudyFormMode
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.data.login.datasource.local.TokenDataSource
import com.example.spoteam_android.domain.study.entity.ImageResponse
import com.example.spoteam_android.domain.study.entity.MakeScheduleRequest
import com.example.spoteam_android.domain.study.entity.RecentAnnounceResponse
import com.example.spoteam_android.domain.study.entity.ScheduleListResponse
import com.example.spoteam_android.domain.study.entity.StudyDataResponse
import com.example.spoteam_android.domain.study.entity.StudyDetailResponse
import com.example.spoteam_android.domain.study.entity.StudyMemberResponse
import com.example.spoteam_android.domain.study.entity.StudyRegisterRequest
import com.example.spoteam_android.domain.study.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
    private val tokenDataSource: TokenDataSource
) : ViewModel() {

    private val _mode = MutableLiveData(StudyFormMode.CREATE)
    val mode: LiveData<StudyFormMode> = _mode

    private val _studyRequest = MutableLiveData<StudyRegisterRequest?>()
    val studyRequest: LiveData<StudyRegisterRequest?> = _studyRequest

    private val _profileImageUri = MutableLiveData<String?>()
    val profileImageUri: LiveData<String?> = _profileImageUri

    private val _themes = MutableLiveData<List<String>>()
    val themes: LiveData<List<String>> = _themes

    private val _maxPeople = MutableLiveData<Int>()
    val maxPeople: LiveData<Int> = _maxPeople

    private val _memberCount = MutableLiveData<Int>()
    val memberCount: LiveData<Int> = _memberCount

    private val _studyOwner = MutableLiveData<String>()
    val studyOwner: LiveData<String> = _studyOwner


    private val _studyId = MutableLiveData<Int>()
    val studyId: LiveData<Int> = _studyId

    private val _studyImageUrl = MutableLiveData<String>()
    val studyImageUrl: LiveData<String> = _studyImageUrl

    private val _studyIntroduction = MutableLiveData<String>()
    val studyIntroduction: LiveData<String> = _studyIntroduction

    private val _patchSuccess = MutableLiveData<Boolean>()
    val patchSuccess: LiveData<Boolean> = _patchSuccess

    private val _recentStudyId = MutableLiveData<Int?>()
    val recentStudyId: LiveData<Int?> = _recentStudyId

    private val _hasFee = MutableLiveData<Boolean?>()
    val hasFee: LiveData<Boolean?> = _hasFee

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email


    private val _bookmarkListLiveData = MutableLiveData<List<StudyDataResponse.StudyContent>>()
    val bookmarkListLiveData: LiveData<List<StudyDataResponse.StudyContent>> = _bookmarkListLiveData

    private val _totalPages = MutableLiveData<Int>()
    val totalPages: LiveData<Int> = _totalPages

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun setStudyId(id: Int) {
        _studyId.value = id
    }

    fun setStudyData(id: Int, imageUrl: String, studyIntroduction: String) {
        _studyId.value = id
        _studyImageUrl.value = imageUrl
        _studyIntroduction.value = studyIntroduction
    }

    fun fetchStudyDetail(studyId: Int) {
        viewModelScope.launch {
            studyRepository.getStudyDetails(studyId)
                .onSuccess { result ->
                    _themes.value = result.themes
                    _studyIntroduction.value = result.introduction
                    _maxPeople.value = result.maxPeople
                    _memberCount.value = result.memberCount
                    _studyOwner.value = result.ownerName
                    _profileImageUri.value = result.profileImage
                    _hasFee.value = result.fee > 0
                    _studyRequest.value = result.toStudyRegisterRequest()
                }
                .onFailure {
                    Log.e("StudyViewModel", "Failed to fetch study details: ${it.message}")
                }
        }
    }

    fun getBookmarkList(page: Int, size: Int) {
        viewModelScope.launch {
            val result = studyRepository.getBookmarkStudies(page, size)
            result.fold(
                onSuccess = { response ->
                    _bookmarkListLiveData.value = response.content
                    _totalPages.value = response.totalPages
                },
                onFailure = { error ->
                    Log.e("StudyViewModel", "북마크 불러오기 실패", error)
                }
            )
        }
    }
    fun setStudyRequest(request: StudyRegisterRequest) {
        _studyRequest.value = request
    }



    fun fetchStudyData(
        page: Int,
        size: Int,
        onResult: (Result<StudyDataResponse>) -> Unit
    ) {
        viewModelScope.launch {
            studyRepository.getStudies(page, size)
                .onSuccess { data -> onResult(Result.success(data)) }
                .onFailure { error -> onResult(Result.failure(error)) }
        }
    }

    fun fetchIsApplied(studyId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            studyRepository.getIsApplied(studyId)
                .onSuccess { isAppliedResponse ->
                    onResult(isAppliedResponse.applied)
                }
                .onFailure {
                    onResult(false)
                }
        }
    }

    fun fetchRecentAnnounce(studyId: Int, onResult: (RecentAnnounceResponse?) -> Unit) {
        viewModelScope.launch {
            val result = studyRepository.getRecentAnnounce(studyId)
            onResult(result.getOrNull())
        }
    }

    fun fetchStudySchedules(
        studyId: Int,
        page: Int,
        size: Int,
        onResult: (ScheduleListResponse?) -> Unit
    ) {
        viewModelScope.launch {
            val result = studyRepository.getStudySchedules(studyId, page, size)
            onResult(result.getOrNull())
        }
    }

    fun applyStudy(
        studyId: Int,
        memberId: Int,
        introduction: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            studyRepository.applyStudy(studyId, memberId, introduction)
                .onSuccess {
                    onResult(true)
                }
                .onFailure { e ->
                    Log.e("StudyViewModel", "스터디 신청 실패: ${e.message}")
                    onResult(false)
                }
        }
    }

    fun makeSchedule(
        studyId: Int,
        request: MakeScheduleRequest,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            studyRepository.makeSchedules(studyId, request)
                .onSuccess { onResult(true) }
                .onFailure {
                    Log.e("StudyViewModel", "일정 추가 실패: ${it.message}")
                    onResult(false)
                }
        }
    }


    fun fetchStudyMembers(studyId: Int, onResult: (StudyMemberResponse?) -> Unit) {
        viewModelScope.launch {
            val result = studyRepository.getStudyMembers(studyId)
            onResult(result.getOrNull())
        }
    }


    fun patchStudyData() {
        val studyId = _studyId.value ?: return
        val studyData = _studyRequest.value ?: return
        viewModelScope.launch {
            runCatching {
                studyRepository.patchStudy(studyId, studyData)
            }.onSuccess {
                Log.d("StudyViewModel", "스터디 수정 성공")
                _patchSuccess.value = true
            }.onFailure {
                Log.e("StudyViewModel", "스터디 수정 실패: ${it.message}")
                _patchSuccess.value = false
            }
        }
    }

    suspend fun getStudyImages(studyId: Int, page: Int, limit: Int): Result<ImageResponse> {
        return withContext(Dispatchers.IO) {
            studyRepository.getStudyImages(studyId, page, limit)
        }
    }

    fun toggleLikeStatus(
        studyId: Int,
        onResult: (Result<Boolean>) -> Unit
    ) {
        viewModelScope.launch {
            val result = studyRepository.toggleStudyLike(studyId)
            result.fold(
                onSuccess = { response ->
                    val liked = response.status == "LIKE"
                    onResult(Result.success(liked))
                },
                onFailure = { error ->
                    onResult(Result.failure(error))
                }
            )
        }
    }

    fun StudyDetailResponse.toStudyRegisterRequest(): StudyRegisterRequest {
        return StudyRegisterRequest(
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

    fun setRecentStudyId(id: Int) {
        _recentStudyId.value = id
    }

    fun loadNickname() {
        _nickname.value = tokenDataSource.getNickname() ?: "Unknown"
        _email.value = tokenDataSource.getEmail() ?: ""
    }

}