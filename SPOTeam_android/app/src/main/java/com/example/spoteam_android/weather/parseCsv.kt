package com.example.spoteam_android.weather

import android.content.Context
import com.example.spoteam_android.R
import com.example.spoteam_android.RegionData
import java.io.BufferedReader
import java.io.InputStreamReader

fun parseCsv(context: Context): List<RegionData> {
    val regionDataList = mutableListOf<RegionData>()

    val assetManager = context.assets
    val inputStream = assetManager.open("weather_region_data.csv") // 파일 경로 지정
    val reader = BufferedReader(InputStreamReader(inputStream))

    reader.useLines { lines ->
        lines.drop(1).forEach { line -> // 첫 줄(헤더) 생략
            val tokens = line.split(",")
            if (tokens.size >= 15) {
                val data = RegionData(
                    category = tokens[0],
                    administrativeCode = tokens[1],
                    level1 = tokens[2].takeIf { it.isNotEmpty() },
                    level2 = tokens[3].takeIf { it.isNotEmpty() },
                    level3 = tokens[4].takeIf { it.isNotEmpty() },
                    gridX = tokens[5].toInt(),
                    gridY = tokens[6].toInt(),
                    longitudeHour = tokens[7].toInt(),
                    longitudeMinute = tokens[8].toInt(),
                    longitudeSecond = tokens[9].toDouble(),
                    latitudeHour = tokens[10].toInt(),
                    latitudeMinute = tokens[11].toInt(),
                    latitudeSecond = tokens[12].toDouble(),
                    longitudeSecond100 = tokens[13].toDouble(),
                    latitudeSecond100 = tokens[14].toDouble(),
                    locationUpdate = tokens.getOrNull(15)?.takeIf { it.isNotEmpty() }
                )
                regionDataList.add(data)
            }
        }
    }

    return regionDataList
}
