package com.example.spoteam_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "fe65b6d5ea91f00e2268b47ba13661df")
    }
}