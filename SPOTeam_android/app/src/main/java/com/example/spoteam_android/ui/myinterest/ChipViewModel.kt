package com.example.spoteam_android.ui.myinterest

import androidx.lifecycle.ViewModel


class ChipViewModel : ViewModel() {
    var selectedChipId: Int? = null
    var selectedSpinnerPosition: Int? = null
    var ageRangeValues: List<Float>? = null
    var selectedAddress: String? = null
    var selectedCode: String? = null
}
