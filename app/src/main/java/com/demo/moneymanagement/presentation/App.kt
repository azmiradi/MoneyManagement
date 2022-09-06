package com.demo.moneymanagement.presentation

import android.app.Application
import com.rollbar.android.Rollbar
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application(){
    override fun onCreate() {
        super.onCreate()
        Rollbar.init(this);
    }
}