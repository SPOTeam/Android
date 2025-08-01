package com.spot.android.login

data class LocationItem(
    val code: String,
    val city: String,
    val districtCity: String,
    val districtGu: String,
    val subdistrict: String,
){
    val address: String
        get() = "$city $districtCity $districtGu $subdistrict"
}

