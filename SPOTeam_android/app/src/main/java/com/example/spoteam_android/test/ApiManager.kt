package com.example.spoteam_android.test

import com.example.spoteam_android.data.ApiModels
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiManager {

    fun sendRequestData(
        requestData: ApiModels.RequestData,
        onSuccess: (ApiModels.ResponseData) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val call = apiService.postRequestData(requestData)

        call.enqueue(object : Callback<ApiModels.ResponseData> {
            override fun onResponse(call: Call<ApiModels.ResponseData>, response: Response<ApiModels.ResponseData>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: onFailure(Throwable("응답 데이터가 null입니다."))
                } else {
                    onFailure(Throwable("응답 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<ApiModels.ResponseData>, t: Throwable) {
                onFailure(t)
            }
        })
    }
}
