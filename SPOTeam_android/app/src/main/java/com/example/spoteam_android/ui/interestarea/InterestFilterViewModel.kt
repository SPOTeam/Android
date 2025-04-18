package com.example.spoteam_android.ui.interestarea

import androidx.lifecycle.ViewModel

class InterestFilterViewModel : ViewModel() {
    var gender: String = "MALE"
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean = false
    var hasFee: Boolean = false
    var minfee: Int? = null
    var maxfee: Int? = null
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
        hasFee = false
        themeTypes.clear()
        isRecruiting = true
        minfee = null
        maxfee = null
    }
}
