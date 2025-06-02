package com.example.spoteam_android.data.weather.mapper

import com.example.spoteam_android.data.weather.dto.WeatherDto
import com.example.spoteam_android.domain.weather.entity.WeatherUiState
import com.example.spoteam_android.R

object WeatherMapper {
    fun toUiState(dto: WeatherDto): WeatherUiState {
        val items = dto.response.body.items.item
        val tmp = items.find { it.category == "TMP" }?.fcstValue?.toDoubleOrNull() ?: 0.0
        val pcp = items.find { it.category == "PCP" }?.fcstValue ?: "-"
        val sno = items.find { it.category == "SNO" }?.fcstValue ?: "-"
        val wsd = items.find { it.category == "WSD" }?.fcstValue?.toDoubleOrNull() ?: 0.0

        return WeatherUiState(
            temperature = "%.1f °C".format(tmp),
            message = getMessage(tmp, pcp, sno, wsd),
            imageRes = getIcon(tmp, pcp, sno, wsd)
        )
    }

    private fun getMessage(tmp: Double, pcp: String, sno: String, wsd: Double): String {
        val rainVal = parse(pcp)
        val snowVal = parse(sno)
        return when {
            rainVal >= 10 -> "실내에서 집중! 목표는 선명히!"
            snowVal >= 5 -> "눈길 조심! 한 걸음씩 나아가요!"
            wsd >= 9 -> "바람 조심! 흔들려도 전진!"
            tmp in 0.0..10.0 -> "따뜻하게! 오늘도 열정 가득!"
            tmp >= 25 -> "수분 보충! 더위도 이겨내요!"
            rainVal in 1.0..4.0 -> "우산 챙기고 오늘도 파이팅!"
            else -> "좋은 날! 목표 향해 달려요!"
        }
    }

    private fun getIcon(tmp: Double, pcp: String, sno: String, wsd: Double): Int {
        val rainVal = parse(pcp)
        val snowVal = parse(sno)
        return when {
            rainVal >= 10 -> R.drawable.ic_rainy
            snowVal >= 5 -> R.drawable.ic_snow
            wsd >= 9 -> R.drawable.ic_gale
            tmp in 0.0..10.0 -> R.drawable.ic_cold
            tmp >= 25 -> R.drawable.ic_hot
            rainVal in 1.0..4.0 -> R.drawable.ic_light_rainy
            else -> R.drawable.ic_sun
        }
    }

    private fun parse(value: String): Double =
        when (value) {
            "-", "강수없음", "적설없음" -> 0.0
            else -> value.toDoubleOrNull() ?: 0.0
        }
}