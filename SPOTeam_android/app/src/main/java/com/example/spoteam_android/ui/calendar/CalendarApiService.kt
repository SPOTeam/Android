package com.example.spoteam_android.ui.calendar

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CalendarApiService {
    @GET("/spot/studies/{studyId}/schedules")
    fun GetScheuled(
        @Path("studyId") studyId: Int,
        @Query("year") year : Int,
        @Query("month") month : Int,
    ): Call<ScheduleResponse>
}