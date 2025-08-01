package com.umcspot.android.ui.recruiting

import androidx.lifecycle.ViewModel

class RecruitingChipViewModel : ViewModel() {
    var gender: String? = null
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean? = null
    var hasFee: Boolean? = null
    var minfee: Int? = null
    var maxfee: Int? = null
    var selectedAddress: MutableList<String>? = mutableListOf()
    var selectedCode: MutableList<String>? = mutableListOf()
    var themeTypes: MutableList<String>? = mutableListOf()

    val finalMinFee: Int?
        get() = if (hasFee == true) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee == true) maxfee else null


    fun reset() {
        gender = null
        minAge = 18
        maxAge = 60
        hasFee = null
        isOnline = null
        minfee = null
        maxfee = null
        themeTypes?.clear()
        selectedAddress?.clear()
        selectedCode?.clear()
    }

}
