package com.example.spoteam_android.presentation.home

import android.util.Log
import androidx.lifecycle.*
import com.example.spoteam_android.domain.study.entity.StudyMemberResponse
import com.example.spoteam_android.domain.study.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _likeToggleResult = MutableLiveData<Pair<Int, Boolean>>()
    val likeToggleResult: LiveData<Pair<Int, Boolean>> = _likeToggleResult

    private val _studyMembers = MutableLiveData<List<StudyMemberResponse.StudyMember>>()
    val studyMembers: LiveData<List<StudyMemberResponse.StudyMember>> = _studyMembers

    fun toggleLike(studyId: Int) {
        viewModelScope.launch {
            val result = studyRepository.toggleStudyLike(studyId)
            result.onSuccess { response ->
                val isLiked = response.status == "LIKE"
                _likeToggleResult.value = studyId to isLiked
            }.onFailure {
                Log.e("HomeViewModel", "toggleLike 실패: ${it.message}")
            }
        }
    }
    fun fetchStudyMembers(studyId: Int) {
        viewModelScope.launch {
            val result = studyRepository.getStudyMembers(studyId)
            result.onSuccess { response ->
                _studyMembers.value = response.members
            }.onFailure {
                Log.e("HomeViewModel", "fetch study member 실패: ${it.message}")
            }
        }
    }
}
