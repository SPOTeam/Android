package com.example.spoteam_android.ui.recruiting

import androidx.lifecycle.ViewModel

class RecruitingChipViewModel : ViewModel() {
    var gender: String = "MALE"
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean = false
    var hasFee: Boolean = false
    var minfee: Int? = null
    var maxfee: Int? = null
    var selectedAddress: String? = null
    var selectedCode: String? = null
    var themeTypes: MutableList<String> = mutableListOf() // 복수 선택

    val finalMinFee: Int?
        get() = if (hasFee) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee) maxfee else null


    fun reset() {
        gender = "MALE"
        minAge = 18
        maxAge = 60
        hasFee = false
        isOnline= true
        minfee = null
        maxfee = null
        themeTypes.clear()
        selectedAddress = null
        selectedCode = null
    }
}
