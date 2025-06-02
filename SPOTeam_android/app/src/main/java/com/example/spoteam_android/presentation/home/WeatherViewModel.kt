package com.example.spoteam_android.presentation.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spoteam_android.domain.weather.entity.WeatherUiState
import com.example.spoteam_android.domain.weather.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableStateFlow(WeatherUiState("", "", 0))
    val weather: StateFlow<WeatherUiState> = _weather

    fun fetchWeather() {
        viewModelScope.launch {
            repository.getTodayWeather()
                .onSuccess { _weather.value = it }
                .onFailure { /// TODO: 실패시? }
                }
        }
    }
}