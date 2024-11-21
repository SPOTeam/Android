package com.example.spoteam_android

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.navercorp.nid.NaverIdLoginSDK

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "7878cb24cec56458df067991de5e7786")
        NaverIdLoginSDK.initialize(
            this,
            "FymiLaGqw2ad12vsqb_H",
            "QVFVNzWxfm",
            "SPOT",
        )
        var keyHash = Utility.getKeyHash(this)

        Log.i("GlobalApplication", "$keyHash")

        RetrofitInstance.initialize(this)

    }
}