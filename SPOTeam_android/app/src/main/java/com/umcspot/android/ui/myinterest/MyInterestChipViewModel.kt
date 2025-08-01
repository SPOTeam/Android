package com.umcspot.android.ui.myinterest

import androidx.lifecycle.ViewModel

class MyInterestChipViewModel : ViewModel() {
    var gender: String? = null
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean? = null
    var hasFee: Boolean? = null
    var minfee: Int? = null
    var maxfee: Int? = null
    var isRecruiting: Boolean? = null
    var selectedAddress: MutableList<String>? = mutableListOf()
    var selectedCode: MutableList<String>? = mutableListOf()

    val finalMinFee: Int?
        get() = if (hasFee == true) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee == true) maxfee else null


    fun reset() {
        gender = null
        minAge = 18
        maxAge = 60
        hasFee = null
        isOnline= null
        isRecruiting = null
        minfee = null
        maxfee = null
        selectedAddress?.clear()
        selectedCode?.clear()
    }

}
