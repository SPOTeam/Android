package com.example.spoteam_android

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "dbd5ca7007d0fdd0b1b705db749a86f3")
        var keyHash = Utility.getKeyHash(this)
        Log.i("GlobalApplication", "$keyHash")
    }
}