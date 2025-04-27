package com.example.spoteam_android.ui.study.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TimerViewModel : ViewModel() {

    var quiz : String = ""
    var scheduleId : Int = -1

    private val _timerSeconds = MutableLiveData<Long>()
    val timerSeconds: LiveData<Long> = _timerSeconds

    private var timerJob: Job? = null

    fun startTimer(createdAt: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")  // 필요하면 변경

        val createdAtMillis = sdf.parse(createdAt)?.time ?: return
        val nowMillis = System.currentTimeMillis()

        var elapsedSeconds = (nowMillis - createdAtMillis) / 1000  // 경과 시간(초)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                _timerSeconds.postValue(elapsedSeconds)
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
