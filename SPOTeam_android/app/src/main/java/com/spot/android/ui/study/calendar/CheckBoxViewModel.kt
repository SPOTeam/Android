package com.spot.android.ui.study.calendar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// CheckBoxViewModel.kt
class CheckBoxViewModel : ViewModel() {
    val isCheckBoxChecked = MutableLiveData<Boolean>(true)

    // CheckBox 상태를 업데이트하는 메서드
    fun setChecked(checked: Boolean) {
        isCheckBoxChecked.value = checked
    }
}
