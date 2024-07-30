package com.example.spoteam_android

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "dbd5ca7007d0fdd0b1b705db749a86f3")
    }
}