package com.example.spoteam_android.ui.interestarea

import androidx.lifecycle.ViewModel

class InterestFilterViewModel : ViewModel() {
    var gender: String? = null
    var activityFee: String? = null
    var activityFeeAmount: String? = null
    var selectedStudyThemes: MutableList<String> = mutableListOf() // 복수 선택
    var minAge: Int = 18
    var maxAge: Int = 60
    var isRecruiting: String? = null

    fun reset() {
        gender = null
        minAge = 18
        maxAge = 60
        activityFee = null
        activityFeeAmount = null
        selectedStudyThemes.clear()
        isRecruiting = null
    }
}
