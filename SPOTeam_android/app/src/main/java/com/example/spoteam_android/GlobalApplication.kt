package com.example.spoteam_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "1f11692fe82aa0d001930d969462a81e")
    }
}