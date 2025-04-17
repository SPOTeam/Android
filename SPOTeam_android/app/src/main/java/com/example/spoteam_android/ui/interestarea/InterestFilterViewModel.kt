package com.example.spoteam_android.ui.interestarea

import androidx.lifecycle.ViewModel

class InterestFilterViewModel : ViewModel() {
    var gender: String = "MALE"
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean = false
    var hasFee: Boolean = true
    var minfee: Int? = 0
    var maxfee: Int? = 10000
    var themeTypes: MutableList<String> = mutableListOf() // 복수 선택
    var isRecruiting: Boolean = true

    val finalMinFee: Int?
        get() = if (hasFee) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee) maxfee else null


    fun reset() {
        gender = "MALE"
        minAge = 18
        maxAge = 60
        hasFee = true
        themeTypes.clear()
        isRecruiting = false
        minfee = 0
        maxfee = 10000
    }
}
