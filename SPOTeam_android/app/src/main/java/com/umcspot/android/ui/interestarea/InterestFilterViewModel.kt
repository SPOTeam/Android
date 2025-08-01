package com.umcspot.android.ui.interestarea

import androidx.lifecycle.ViewModel

class InterestFilterViewModel : ViewModel() {
    var gender: String? = null
    var minAge: Int = 18
    var maxAge: Int = 60
    var isOnline: Boolean? = null
    var hasFee: Boolean? = null
    var minfee: Int? = null
    var maxfee: Int? = null
    var themeTypes: MutableList<String>? = null
    var isRecruiting: Boolean? = null

    val finalMinFee: Int?
        get() = if (hasFee == true) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee == true) maxfee else null


    fun reset() {
        gender = null
        minAge = 18
        maxAge = 60
        hasFee = null
        themeTypes?.clear()
        isRecruiting = null
        minfee = null
        maxfee = null
    }
}
