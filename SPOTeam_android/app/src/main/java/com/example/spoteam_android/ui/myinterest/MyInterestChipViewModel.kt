package com.example.spoteam_android.ui.myinterest

import androidx.lifecycle.ViewModel

class MyInterestChipViewModel : ViewModel() {
    var gender: String = "MALE"
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean = false
    var hasFee: Boolean = false
    var minfee: Int? = null
    var maxfee: Int? = null
    var isRecruiting: Boolean = true
    var selectedAddress: String? = null
    var selectedCode: String? = null

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
        isRecruiting = true
        minfee = null
        maxfee = null
        selectedAddress = null
        selectedCode = null
    }
}
