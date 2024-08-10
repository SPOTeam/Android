package com.example.spoteam_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "7878cb24cec56458df067991de5e7786")
    }
}