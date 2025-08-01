package com.umcspot.android

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            "SPOT"
        )
        if (BuildConfig.DEBUG) {
            val keyHash = Utility.getKeyHash(this)
            Log.i("SpotApplication", "KeyHash: $keyHash")
        }
        RetrofitInstance.initialize(this)

    }
}