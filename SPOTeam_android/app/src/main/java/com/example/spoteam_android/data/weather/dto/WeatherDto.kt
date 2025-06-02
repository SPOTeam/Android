package com.example.spoteam_android.data.weather.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    @SerialName("response")
    val response: Response
) {
    @Serializable
    data class Response(
        @SerialName("header")
        val header: Header,
        @SerialName("body")
        val body: Body
    )

    @Serializable
    data class Header(
        @SerialName("resultCode")
        val resultCode: Int,
        @SerialName("resultMsg")
        val resultMsg: String
    )

    @Serializable
    data class Body(
        @SerialName("dataType")
        val dataType: String,
        @SerialName("items")
        val items: Items
    )

    @Serializable
    data class Items(
        @SerialName("item")
        val item: List<Item>
    )

    @Serializable
    data class Item(
        @SerialName("baseData")
        val baseData: Int,
        @SerialName("baseTime")
        val baseTime: Int,
        @SerialName("category")
        val category: String,
        @SerialName("fcstDate")
        val fcstDate: Int,
        @SerialName("fcstTime")
        val fcstTime: Int,
        @SerialName("fcstValue")
        val fcstValue: String,
        @SerialName("nx")
        val nx: Int,
        @SerialName("ny")
        val ny: Int
    )
}