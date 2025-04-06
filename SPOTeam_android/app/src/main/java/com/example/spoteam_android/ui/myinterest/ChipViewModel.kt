package com.example.spoteam_android.ui.myinterest

import androidx.lifecycle.ViewModel


class ChipViewModel : ViewModel() {
    var selectedChipId: Int? = null
    var selectedSpinnerPosition: Int? = null
    var ageRangeValues: List<Float>? = null
    var selectedAddress: String? = null
    var selectedCode: String? = null

    // 🔽 새로 추가할 필터 값들
    var source: String? = null
    var gender: String? = null
    var minAge: String? = null
    var maxAge: String? = null
    var activityFee01: String? = null
    var activityFee02: String? = null
    var activityFeeAmount: String? = null
}
