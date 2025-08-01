package com.umcspot.android.login

import StudyRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface LocationApiService {
    @POST("your/endpoint")
    fun sendLocationCode(@Query("regions") code: String): Call<Void> // 반환 타입은 서버의 응답에 따라 조정
    fun sendStudyData(@Body requestBody: StudyRequest): Call<Void>
}
