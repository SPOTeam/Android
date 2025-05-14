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

    var quiz: String = ""
    var scheduleId: Int = -1

    private val _timerSeconds = MutableLiveData<Long>()
    val timerSeconds: LiveData<Long> = _timerSeconds

    private val _remainingMillis = MutableLiveData<Long>() // ✅ 추가: 남은 시간 관리
    val remainingMillis: LiveData<Long> = _remainingMillis

    private var timerJob: Job? = null
    private var createdAtMillis: Long = 0L

    private val totalDurationMillis = 5 * 60 * 1000L // 5분



    fun startTimer(createdAt: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")

        createdAtMillis = sdf.parse(createdAt)?.time ?: return
        val nowMillis = System.currentTimeMillis()

        var elapsedSeconds = (nowMillis - createdAtMillis) / 1000

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                _timerSeconds.postValue(elapsedSeconds)

                val elapsedMillis = elapsedSeconds * 1000
                val remaining = totalDurationMillis - elapsedMillis
                _remainingMillis.postValue(remaining)

                if (remaining <= 0) {
                    _remainingMillis.postValue(0L)
                    stopTimer()
                    break
                }

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
