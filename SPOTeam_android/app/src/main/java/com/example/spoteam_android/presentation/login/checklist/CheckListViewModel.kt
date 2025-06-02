package com.example.spoteam_android.presentation.login.checklist

import com.example.spoteam_android.domain.login.repository.LoginRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class CheckListViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {
    fun submitPreferences(
        themes: List<String>,
        purposes: List<Int>,
        regions: List<String>
    ): LiveData<Result<Unit>> {
        val result = MutableLiveData<Result<Unit>>()
        viewModelScope.launch {
            try {
                loginRepository.updateThemes(themes)
                loginRepository.updateStudyReasons(purposes)
                loginRepository.updateRegions(regions)
                result.value = Result.success(Unit)
            } catch (e: Exception) {
                result.value = Result.failure(e)
            }
        }
        return result
    }
}