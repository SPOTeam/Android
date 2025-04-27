package com.example.spoteam_android.ui.category

import androidx.lifecycle.ViewModel

class CategoryInterestViewModel : ViewModel() {
    var source : Boolean = false
    var gender: String? = null
    var minAge: Int = 18
    var maxAge: Int = 60
    var hasFee: Boolean? = null
    var minfee: Int? = null
    var maxfee: Int? = null
    var isRecruiting: String? = null
    var selectedItem : String = "ALL"
    var theme : String = "전체"

    val finalMinFee: Int?
        get() = if (hasFee == true) minfee else null

    val finalMaxFee: Int?
        get() = if (hasFee == true) maxfee else null


    fun reset() {
        source = false
        gender = null
        minAge = 18
        maxAge = 60
        hasFee = null
        isRecruiting = null
        minfee = null
        maxfee = null
    }
}
