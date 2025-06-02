package com.example.spoteam_android.presentation.study.register

import StudyFormMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.domain.study.entity.StudyRegisterRequest
import com.example.spoteam_android.domain.study.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class StudyRegisterViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _mode = MutableStateFlow(StudyFormMode.CREATE)
    val mode: StateFlow<StudyFormMode> = _mode

    private val _studyId = MutableStateFlow<Int?>(null)
    val studyId: StateFlow<Int?> = _studyId

    private val _studyRequest = MutableStateFlow<StudyRegisterRequest?>(null)
    val studyRequest: StateFlow<StudyRegisterRequest?> = _studyRequest

    private val _localImageUri = MutableStateFlow<String?>(null)
    val localImageUri: StateFlow<String?> = _localImageUri

    private val _uploadedImageUrl = MutableStateFlow<String?>(null)
    val uploadedImageUrl: StateFlow<String?> = _uploadedImageUrl

    fun reset(mode: StudyFormMode = StudyFormMode.CREATE) {
        _mode.value = mode
        _studyId.value = null
        _uploadedImageUrl.value = null
        _localImageUri.value = null
        _studyRequest.value = StudyRegisterRequest(
            themes = emptyList(),
            title = "",
            goal = "",
            introduction = "",
            isOnline = true,
            profileImage = null,
            regions = emptyList(),
            maxPeople = 1,
            gender = "ALL",
            minAge = 18,
            maxAge = 60,
            fee = 0,
            hasFee = false
        )
    }

    fun setStudyId(id: Int) {
        _studyId.value = id
    }

    fun setStudyRequest(request: StudyRegisterRequest) {
        _studyRequest.value = request
        _uploadedImageUrl.value = request.profileImage
    }
    fun setMode(newMode: StudyFormMode) {
        _mode.value = newMode
    }

    fun updateTitleGoalIntro(title: String, goal: String, intro: String) {
        _studyRequest.update { it?.copy(title = title, goal = goal, introduction = intro) }
    }

    fun updateThemes(themes: List<String>) {
        _studyRequest.update { it?.copy(themes = themes) }
    }

    fun updateRegions(regions: List<String>) {
        _studyRequest.update { it?.copy(regions = regions) }
    }

    fun updateLocalImageUri(uri: String) {
        _localImageUri.value = uri
    }
    fun updateUploadedImageUrl(url: String) {
        _uploadedImageUrl.value = url
    }
    fun uploadImageAndSubmitStudy(
        imagePart: MultipartBody.Part,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            studyRepository.uploadImages(listOf(imagePart))
                .onSuccess { uploadResponse ->
                    val imageUrl = uploadResponse.imageUrls.firstOrNull()?.imageUrl
                    if (imageUrl != null) {
                        updateUploadedImageUrl(imageUrl)
                        submitStudyData(
                            onSuccess = {
                                onSuccess()
                            },
                            onFailure = {
                                onFailure()
                            }
                        )
                    } else {
                        onFailure()
                    }
                }
                .onFailure {
                    onFailure()
                }
        }
    }


    fun submitStudyData(
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val currentRequest = _studyRequest.value?.copy(
            profileImage = _uploadedImageUrl.value,
            regions = _studyRequest.value?.regions ?: emptyList()
        )
        if (currentRequest == null) {
            onFailure()
            return
        }

        viewModelScope.launch {
            when (_mode.value) {
                StudyFormMode.CREATE -> {
                    studyRepository.registerStudy(currentRequest)
                        .onSuccess { onSuccess() }
                        .onFailure { onFailure() }
                }
                StudyFormMode.EDIT -> {
                    val id = _studyId.value
                    if (id != null) {
                        studyRepository.patchStudy(id, currentRequest)
                            .onSuccess { onSuccess() }
                            .onFailure { onFailure() }
                    } else {
                        onFailure()
                    }
                }
            }
        }
    }
}