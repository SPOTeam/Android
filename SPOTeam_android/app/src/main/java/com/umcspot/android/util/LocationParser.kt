package com.umcspot.android.util

import android.content.Context
import com.umcspot.android.login.LocationItem
import java.io.BufferedReader
import java.io.InputStreamReader

fun parseLocationTsv(context: Context): List<LocationItem> {
    val locationList = mutableListOf<LocationItem>()

    val inputStream = context.assets.open("region_data_processed.tsv")
    val reader = BufferedReader(InputStreamReader(inputStream))
    val tsvData = reader.use { it.readText() }

    val rows = tsvData.split("\n").drop(1) // 첫 줄은 헤더

    for (row in rows) {
        val columns = row.split("\t")
        if (columns.size >= 5) {
            locationList.add(
                LocationItem(
                    code = columns[0],
                    city = columns[1],
                    districtCity = columns[2],
                    districtGu = columns[3],
                    subdistrict = columns[4]
                )
            )
        }
    }

    return locationList
}
