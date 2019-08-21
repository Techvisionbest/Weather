package com.vision.weather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlin.properties.Delegates

class WeatherApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext
    }

    companion object{
        private var mContext: Context by Delegates.notNull()
        fun getContext() = mContext
    }

}