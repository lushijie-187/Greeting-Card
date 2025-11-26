// file: MyApplication.kt
package com.example.greetingcard

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 在这里初始化 Fresco
        Fresco.initialize(this)
    }
}
