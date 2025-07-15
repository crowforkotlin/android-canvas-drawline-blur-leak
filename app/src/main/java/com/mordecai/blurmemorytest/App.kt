package com.mordecai.blurmemorytest

import android.annotation.SuppressLint
import android.app.Application
import java.util.logging.Logger

val app = App.context

class App : Application() {

    companion object { lateinit var context: Application }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}