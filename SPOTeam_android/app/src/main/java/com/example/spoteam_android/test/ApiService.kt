package com.example.spoteam_android.test

import com.example.spoteam_android.data.ApiModels
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface  ApiService {
    @POST("https://www.teamspot.site/spot/member/test") // 실제 엔드포인트를 여기에 작성
    fun postRequestData(@Body requestData: ApiModels.RequestData): Call<ApiModels.ResponseData>
}
